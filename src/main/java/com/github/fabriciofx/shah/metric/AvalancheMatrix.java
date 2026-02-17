/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.scalar.BitDiff;
import com.github.fabriciofx.shah.scalar.HashBit;
import java.util.function.BiFunction;

/**
 * AvalancheMatrix.
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (100 lines)
 * @checkstyle ParameterNumberCheck (100 lines)
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class AvalancheMatrix implements Scalar<double[][]> {
    /**
     * Hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key size.
     */
    private final int size;

    /**
     * Random seed for reproducibility.
     */
    private final long seed;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func Hash function under test
     * @param size Key size
     * @param seed Random seed for reproducibility
     * @param repetitions Number of repetitions
     */
    public AvalancheMatrix(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final int repetitions
    ) {
        this.func = func;
        this.size = size;
        this.seed = seed;
        this.repetitions = repetitions;
    }

    @Override
    public double[][] value() {
        final Key probe = new Randomized(new KeyOf(this.size));
        final Hash hash = this.func.apply(probe, this.seed);
        final int[][] flips = new int[probe.bits()][hash.bits()];
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final Key key = new Randomized(new KeyOf(this.size));
            final Hash original = this.func.apply(key, this.seed);
            for (int row = 0; row < probe.bits(); ++row) {
                final Key flipped = new Flipped(key, row);
                final Hash changed = this.func.apply(flipped, this.seed);
                for (int column = 0; column < hash.bits(); ++column) {
                    final int obit = new HashBit(original, column).value();
                    final int fbit = new HashBit(changed, column).value();
                    flips[row][column] += new BitDiff(obit, fbit).value();
                }
            }
        }
        final double[][] probs = new double[probe.bits()][hash.bits()];
        for (int row = 0; row < probe.bits(); ++row) {
            for (int column = 0; column < hash.bits(); ++column) {
                probs[row][column] = flips[row][column]
                    / (double) this.repetitions;
            }
        }
        return probs;
    }
}
