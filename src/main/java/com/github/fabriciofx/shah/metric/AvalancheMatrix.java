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
import com.github.fabriciofx.shah.scalar.HashBit;
import com.github.fabriciofx.shah.scalar.HashDiff;
import java.util.function.Function;

/**
 * AvalancheMatrix.
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (100 lines)
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class AvalancheMatrix implements Scalar<double[][]> {
    /**
     * Hash function under test.
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
     * Ctor.
     * @param func Hash function under test
     * @param size Key size
     * @param repetitions Number of repetitions
     */
    public AvalancheMatrix(
        final Function<Key, Hash> func,
        final int size,
        final int repetitions
    ) {
        this.func = func;
        this.size = size;
        this.repetitions = repetitions;
    }

    @Override
    public double[][] value() {
        final Key probe = new Randomized(new KeyOf(this.size));
        final Hash hash = this.func.apply(probe);
        final int[][] counts = new int[probe.bits()][hash.bits()];
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final Key key = new Randomized(new KeyOf(this.size));
            final Hash original = this.func.apply(key);
            for (int ibit = 0; ibit < probe.bits(); ++ibit) {
                final Key flip = new Flipped(key, ibit);
                final Hash flipped = this.func.apply(flip);
                final Hash diff = new HashDiff(original, flipped).value();
                for (int obit = 0; obit < hash.bits(); ++obit) {
                    if (new HashBit(diff, obit).value() != 0) {
                        ++counts[ibit][obit];
                    }
                }
            }
        }
        final double[][] matrix = new double[probe.bits()][hash.bits()];
        for (int row = 0; row < probe.bits(); ++row) {
            for (int col = 0; col < hash.bits(); ++col) {
                matrix[row][col] = counts[row][col] / (double) this.repetitions;
            }
        }
        return matrix;
    }
}
