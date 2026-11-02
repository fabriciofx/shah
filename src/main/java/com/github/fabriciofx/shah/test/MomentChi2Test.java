/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.KeyOf;
import java.util.function.BiFunction;

/**
 * Moment chi-squared test from SMHasher.
 *
 * <p>Analyzes hashes produced from a series of linearly increasing
 * integer keys by measuring the popcount (number of set bits)
 * distribution of hash values and their derivatives (XOR of
 * consecutive hashes).</p>
 *
 * <p>For each hash value, the popcount of the first
 * {@code min(hashbits, 64)} bits is computed, raised to the 5th
 * power, and accumulated. The mean and variance of these 5th-power
 * values are then compared against pre-computed reference values
 * using a chi-squared statistic.</p>
 *
 * <p>The derivative test XORs consecutive hash values and applies
 * the same popcount analysis, testing whether transitions between
 * consecutive keys produce properly distributed bit changes.</p>
 *
 * <p>Returns the worst (maximum) chi-squared value across all four
 * measurements (bits-1, bits-0, derivative bits-1, derivative
 * bits-0). Lower values indicate better hash quality: below 5.0
 * is "Great", below 50.0 is "Good", below 500.0 is "pass".</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class MomentChi2Test implements Test<Double> {

    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Step between consecutive key values.
     */
    private final int step;

    /**
     * Number of keys to generate.
     */
    private final int count;

    /**
     * Ctor with defaults.
     *
     * @param func The hash function under test
     * @param seed The hash function seed
     */
    public MomentChi2Test(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed
    ) {
        this(
            func,
            seed,
            4,
            2,
            1_000_000
        );
    }

    /**
     * Ctor.
     *
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param size Key size in bytes
     * @param step Step between consecutive key values
     * @param count Number of keys to generate
     */
    public MomentChi2Test(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int size,
        final int step,
        final int count
    ) {
        this.func = func;
        this.seed = seed;
        this.size = size;
        this.step = step;
        this.count = count;
    }

    @Override
    public Double metric() {
        final int bits = Math.min(
            this.func.apply(new KeyOf(this.size), this.seed).bits(),
            64
        );
        final double[] ref = MomentChi2Test.refs(bits);
        return MomentChi2Test.worstChi(
            this.accumulate(bits),
            this.count,
            ref[0],
            ref[1]
        );
    }

    private double[] accumulate(final int bits) {
        final double[] result = new double[8];
        final byte[] bytes = new byte[this.size];
        MomentChi2Test.store(bytes, (long) -this.step);
        long prev = MomentChi2Test.hashToLong(
            this.func.apply(new KeyOf(bytes.clone()), this.seed),
            bits
        );
        for (int idx = 0; idx < this.count; ++idx) {
            MomentChi2Test.store(bytes, (long) idx * this.step);
            final long hash = MomentChi2Test.hashToLong(
                this.func.apply(new KeyOf(bytes.clone()), this.seed),
                bits
            );
            MomentChi2Test.addMoments(result, 0, hash, bits);
            MomentChi2Test.addMoments(result, 4, prev ^ hash, bits);
            prev = hash;
        }
        return result;
    }

    private static double[] refs(final int bits) {
        final double[] ref = new double[2];
        if (bits > 32) {
            ref[0] = 38_918_200.0;
            ref[1] = 273_633.333_333;
        } else {
            ref[0] = 1_391_290.0;
            ref[1] = 686.666_666_7;
        }
        return ref;
    }

    private static double worstChi(
        final double[] moments,
        final double num,
        final double srefh,
        final double srefl
    ) {
        double worst = 0.0;
        for (int idx = 0; idx < 8; idx += 2) {
            final double mean = moments[idx] / num;
            worst = Math.max(
                worst,
                MomentChi2Test.chiSquared(
                    mean,
                    (moments[idx + 1] / num - mean * mean) / num,
                    srefh,
                    srefl
                )
            );
        }
        return worst;
    }

    private static void addMoments(
        final double[] acc,
        final int off,
        final long value,
        final int bits
    ) {
        final int ones = Long.bitCount(value);
        final double pow = Math.pow(ones, 5);
        final double zpow = Math.pow(bits - ones, 5);
        acc[off] += pow;
        acc[off + 1] += pow * pow;
        acc[off + 2] += zpow;
        acc[off + 3] += zpow * zpow;
    }

    private static long hashToLong(final Hash hash, final int bits) {
        final byte[] bytes = hash.asBytes();
        final int length = Math.min(
            bytes.length,
            8
        );
        long result = 0L;
        for (int idx = 0; idx < length; ++idx) {
            result |= (long) (bytes[idx] & 0xFF) << (idx * 8);
        }
        if (bits < 64) {
            result &= (1L << bits) - 1;
        }
        return result;
    }

    private static void store(final byte[] buf, final long value) {
        final int length = Math.min(buf.length, 8);
        for (int idx = 0; idx < length; ++idx) {
            buf[idx] = (byte) (value >>> (idx * 8));
        }
    }

    private static double chiSquared(
        final double observed,
        final double variance,
        final double refmean,
        final double refvar
    ) {
        final double diff = observed - refmean;
        return diff * diff / (variance + refvar);
    }
}
