/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.BicBias;
import com.github.fabriciofx.shah.scalar.ByteDiff;
import com.github.fabriciofx.shah.scalar.FirstBit;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Bit Independence Criterion (BIC) test from SMHasher.
 *
 * <p>Generate contingency tables and create a BIC bias.</p>
 *
 * <p>When a single input bit is flipped, the resulting changes in any pair of
 * output bits should be statistically independent. For each pair of output bits
 * (i, j), a 2x2 contingency table is built counting the four possible outcomes
 * {00, 01, 10, 11}. The worst bias across all input bits is reported via
 * {@link BicBias}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle CyclomaticComplexityCheck (200 lines)
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class BicTest implements Test<BicBias> {
    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Seed for the hash function.
     */
    private final Seed seed;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Key's seed.
     */
    private final Seed initial;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed Seed for the hash function
     * @param size Key's size in bytes
     * @param initial Key's seed
     * @param repetitions Number of repetitions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public BicTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int size,
        final Seed initial,
        final int repetitions
    ) {
        this.func = func;
        this.seed = seed;
        this.size = size;
        this.initial = initial;
        this.repetitions = repetitions;
    }

    @Override
    public BicBias metric() {
        final Random random = this.initial.random();
        final Key probe = new Randomized(new KeyOf(this.size), random);
        final Hash hash = this.func.apply(probe, this.seed);
        final int[][][][] bins =
            new int[probe.bits()][hash.bits()][hash.bits()][4];
        for (int bit = 0; bit < probe.bits(); ++bit) {
            for (int rep = 0; rep < this.repetitions; ++rep) {
                final Key key = new Randomized(new KeyOf(this.size), random);
                final Hash original = this.func.apply(key, this.seed);
                final Hash flipped = this.func.apply(
                    new Flipped(key, bit),
                    this.seed
                );
                for (int one = 0; one < hash.bits(); ++one) {
                    final int idx = one >> 3;
                    final int first = new FirstBit(
                        new ByteDiff(original.byteAt(idx), flipped.byteAt(idx)),
                        one
                    ).value();
                    for (int two = one + 1; two < hash.bits(); ++two) {
                        final int pos = two >> 3;
                        final int second = new FirstBit(
                            new ByteDiff(
                                original.byteAt(pos),
                                flipped.byteAt(pos)
                            ),
                            two
                        ).value();
                        final int index = first | (second << 1);
                        ++bins[bit][one][two][index];
                    }
                }
            }
        }
        return new BicBias(bins, this.repetitions);
    }
}
