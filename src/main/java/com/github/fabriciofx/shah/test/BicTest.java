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
import com.github.fabriciofx.shah.metric.BicBias;
import com.github.fabriciofx.shah.scalar.ByteDiff;
import com.github.fabriciofx.shah.scalar.FirstBit;
import com.github.fabriciofx.shah.scalar.HashByte;
import java.util.Random;
import java.util.function.Function;

/**
 * Bit Independence Criterion (BIC) test from SMHasher.
 *
 * <p>Generate contingency tables and compute the worst BIC bias. The worst bias
 * as a value between 0.0 and 1.0</p>
 *
 * <p>When a single input bit is flipped, the resulting changes in
 * any pair of output bits should be statistically independent.
 * For each pair of output bits (i, j), a 2x2 contingency table
 * is built counting the four possible outcomes {00, 01, 10, 11}.
 * The worst bias across all input bits is reported via
 * {@link BicBias}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle CyclomaticComplexityCheck (200 lines)
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class BicTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Key size.
     */
    private final int size;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Random seed for reproducibility.
     */
    private final long seed;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Key size in key
     * @param repetitions Number of repetitions
     * @param seed Random seed for reproducibility
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BicTest(
        final Function<Key, Hash> func,
        final int size,
        final int repetitions,
        final long seed
    ) {
        this.func = func;
        this.size = size;
        this.repetitions = repetitions;
        this.seed = seed;
    }

    @Override
    public Double value() {
        final Random random = new Random(this.seed);
        final Key probe = new Randomized(new KeyOf(this.size), random);
        final Hash hash = this.func.apply(probe);
        double worst = 0.0;
        for (int ibit = 0; ibit < probe.bits(); ++ibit) {
            final int[][][] bins = new int[hash.bits()][hash.bits()][4];
            for (int rep = 0; rep < this.repetitions; ++rep) {
                final Key key = new Randomized(new KeyOf(this.size), random);
                final Hash original = this.func.apply(key);
                final Hash flipped = this.func.apply(new Flipped(key, ibit));
                for (int one = 0; one < hash.bits(); ++one) {
                    final int first = new FirstBit(
                        new ByteDiff(
                            new HashByte(original, one >> 3),
                            new HashByte(flipped, one >> 3)
                        ),
                        one
                    ).value();
                    for (int two = one + 1; two < hash.bits(); ++two) {
                        final int second = new FirstBit(
                            new ByteDiff(
                                new HashByte(original, two >> 3),
                                new HashByte(flipped, two >> 3)
                            ),
                            two
                        ).value();
                        final int index = first | (second << 1);
                        bins[one][two][index] += 1;
                    }
                }
            }
            final double bias = new BicBias(bins, this.repetitions).value();
            if (bias > worst) {
                worst = bias;
            }
        }
        return worst;
    }
}
