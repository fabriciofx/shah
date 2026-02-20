/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.avalanche.Bias;
import com.github.fabriciofx.shah.metric.avalanche.Matrix;
import com.github.fabriciofx.shah.scalar.BitDiff;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Avalanche test from SMHasher.
 *
 * <p>Generate flip-count bins and compute the worst avalanche bias. The worst
 * bias as a value between 0.0 and 1.0.<p>
 *
 * <p>For each input bit, flips it and measures how many output bits
 * change. A good hash function should flip approximately 50% of
 * output bits when any single input bit is changed. The worst bias
 * across all (input bit, output bit) pairs is reported via
 * {@link Bias}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.). The hash bit count is derived from the byte array length
 * of the first hash output.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class AvalancheTest implements Test<Matrix> {
    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Size in key.
     */
    private final int size;

    /**
     * Seed for the hash function.
     */
    private final long seed;

    /**
     * Initial value for key generation.
     */
    private final long initial;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Size in key
     * @param seed Seed for the hash function.
     * @param initial Initial value for key generation
     * @param repetitions Number of repetitions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public AvalancheTest(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final long initial,
        final int repetitions
    ) {
        this.func = func;
        this.size = size;
        this.seed = seed;
        this.initial = initial;
        this.repetitions = repetitions;
    }

    @Override
    public Matrix value() {
        final Random random = new Random(this.initial);
        final Key probe = new Randomized(new KeyOf(this.size), random);
        final Hash hash = this.func.apply(probe, this.seed);
        final int[][] flips = new int[probe.bits()][hash.bits()];
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final Key key = new Randomized(new KeyOf(this.size), random);
            final Hash original = this.func.apply(key, this.seed);
            for (int row = 0; row < probe.bits(); ++row) {
                final Key flipped = new Flipped(key, row);
                final Hash changed = this.func.apply(flipped, this.seed);
                for (int column = 0; column < hash.bits(); ++column) {
                    final int obit = original.bitAt(column);
                    final int cbit = changed.bitAt(column);
                    flips[row][column] += new BitDiff(obit, cbit).value();
                }
            }
        }
        return new Matrix(this.repetitions, flips);
    }
}
