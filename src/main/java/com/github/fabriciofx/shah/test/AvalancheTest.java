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
import com.github.fabriciofx.shah.metric.AvalancheBias;
import com.github.fabriciofx.shah.scalar.BitDiff;
import com.github.fabriciofx.shah.scalar.HashBit;
import java.util.Random;
import java.util.function.Function;

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
 * {@link AvalancheBias}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.). The hash bit count is derived from the byte array length
 * of the first hash output.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings({
    "PMD.TestClassWithoutTestCases",
    "PMD.UnnecessaryLocalRule"
})
public final class AvalancheTest implements Test<Double> {
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
     * @param func The hash under test
     * @param size Size in key
     * @param repetitions Number of repetitions
     * @param seed Random seed for reproducibility
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public AvalancheTest(
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
        final int[][] bins = new int[probe.bits()][hash.bits()];
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final Key key = new Randomized(new KeyOf(this.size), random);
            final Hash original = this.func.apply(key);
            for (int ibit = 0; ibit < probe.bits(); ++ibit) {
                final Hash flipped = this.func.apply(new Flipped(key, ibit));
                for (int obit = 0; obit < hash.bits(); ++obit) {
                    final int orig = new HashBit(original, obit).value();
                    final int flip = new HashBit(flipped, obit).value();
                    bins[ibit][obit] += new BitDiff(orig, flip).value();
                }
            }
        }
        return new AvalancheBias(bins, this.repetitions).value();
    }
}
