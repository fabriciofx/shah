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
 * @checkstyle MagicNumberCheck (300 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class MomentChi2Test implements Test<Double> {
    /**
     * Reference mean for 32-bit hashes.
     */
    private static final double SREFH_32 = 1_391_290.0;

    /**
     * Reference variance term for 32-bit hashes (step=2).
     */
    private static final double SREFL_32 = 686.666_666_7;

    /**
     * Reference mean for 64-bit hashes.
     */
    private static final double SREFH_64 = 38_918_200.0;

    /**
     * Reference variance term for 64-bit hashes (step=2).
     */
    private static final double SREFL_64 = 273_633.333_333;

    /**
     * Default number of keys.
     */
    private static final int DEFAULT_COUNT = 1_000_000;

    /**
     * Default step between consecutive keys.
     */
    private static final int DEFAULT_STEP = 2;

    /**
     * Default key size in bytes.
     */
    private static final int DEFAULT_SIZE = 4;

    /**
     * Number of bytes to extract for popcount (max 8).
     */
    private static final int POPCOUNT_MAX = 8;

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
            MomentChi2Test.DEFAULT_SIZE,
            MomentChi2Test.DEFAULT_STEP,
            MomentChi2Test.DEFAULT_COUNT
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param size Key size in bytes
     * @param step Step between consecutive key values
     * @param count Number of keys to generate
     * @checkstyle ParameterNumberCheck (5 lines)
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

    /**
     * Accumulate popcount moments over all keys.
     * Returns 8 values: mean/var for bits-1, bits-0, dbits-1, dbits-0.
     * @param bits Number of hash bits to analyze
     * @return Array of 8 accumulated moment values
     */
    private double[] accumulate(final int bits) {
        final double[] result = new double[8];
        final byte[] bytes = new byte[this.size];
        MomentChi2Test.toLittleEndian(bytes, (long) -this.step);
        long prev = MomentChi2Test.hashToLong(
            this.func.apply(new KeyOf(bytes.clone()), this.seed),
            bits
        );
        for (int idx = 0; idx < this.count; ++idx) {
            MomentChi2Test.toLittleEndian(bytes, (long) idx * this.step);
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

    /**
     * Pick reference values based on hash width.
     * @param bits Number of hash bits
     * @return Array with {srefh, srefl}
     */
    private static double[] refs(final int bits) {
        final double[] ref = new double[2];
        if (bits > 32) {
            ref[0] = MomentChi2Test.SREFH_64;
            ref[1] = MomentChi2Test.SREFL_64;
        } else {
            ref[0] = MomentChi2Test.SREFH_32;
            ref[1] = MomentChi2Test.SREFL_32;
        }
        return ref;
    }

    /**
     * Compute worst chi-squared across all 4 moment pairs.
     * @param moments The 8 accumulated moment values
     * @param num Number of keys
     * @param srefh Reference mean
     * @param srefl Reference variance term
     * @return Worst chi-squared value
     * @checkstyle ParameterNumberCheck (5 lines)
     */
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

    /**
     * Add popcount-based 5th power moments to the accumulator.
     * @param acc Accumulator array
     * @param off Offset into accumulator (0 for raw, 4 for derivative)
     * @param value Hash value (up to 64 bits)
     * @param bits Number of hash bits
     * @checkstyle ParameterNumberCheck (5 lines)
     */
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

    /**
     * Extract up to 64 bits from a hash as a long value.
     * @param hash The hash
     * @param bits Number of bits to use
     * @return Long value from the hash bytes
     */
    private static long hashToLong(final Hash hash, final int bits) {
        final byte[] bytes = hash.asBytes();
        final int length = Math.min(
            bytes.length,
            MomentChi2Test.POPCOUNT_MAX
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

    /**
     * Store a long value into a byte array in little-endian order.
     * @param buf Target buffer
     * @param value Value to store
     */
    private static void toLittleEndian(final byte[] buf, final long value) {
        final int length = Math.min(buf.length, 8);
        for (int idx = 0; idx < length; ++idx) {
            buf[idx] = (byte) (value >>> (idx * 8));
        }
    }

    /**
     * Compute chi-squared statistic.
     * @param observed Observed mean
     * @param variance Observed variance
     * @param refmean Reference mean
     * @param refvar Reference variance term
     * @return Chi-squared value
     * @checkstyle ParameterNumberCheck (5 lines)
     */
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
