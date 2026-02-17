/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.scalar.BitDiff;
import com.github.fabriciofx.shah.scalar.Cached;
import com.github.fabriciofx.shah.scalar.HashBit;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Avalanche matrix.
 *
 * <p>An avalanche matrix is a statistical representation used to evaluate how
 * changes in input bits affect output bits in a function, typically a hash
 * function or cryptographic primitive.
 * More precisely, it is a matrix where each element Pij represents:
 *
 *              P(output bit j flips | input bit i flipped)
 *
 * In other words, it measures the probability that a specific output bit
 * changes when a specific input bit is toggled.
 * An ideal avalanche matrix has values close to 0.5 in every cell, indicating
 * that flipping any single input bit causes each output bit to change with
 * equal probability.<p>
 *
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (100 lines)
 * @checkstyle ParameterNumberCheck (100 lines)
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class Matrix implements Scalar<double[][]> {
    /**
     * Threshold of 50%.
     */
    private static final double THRESHOLD = 0.50;

    /**
     * Probabilities matrix.
     */
    private final Scalar<double[][]> mtx;

    /**
     * Bias.
     */
    private final Scalar<Bias> bas;

    /**
     * Ctor.
     * @param func Hash function under test
     * @param size Key size
     * @param seed Seed for the hash function
     * @param initial Initial value for key generation
     * @param repetitions Number of repetitions
     */
    public Matrix(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final long initial,
        final int repetitions
    ) {
        this.mtx = new Cached<>(
            () -> {
                final Random random = new Random(initial);
                final Key probe = new Randomized(new KeyOf(size), random);
                final Hash hash = func.apply(probe, seed);
                final int[][] flips = new int[probe.bits()][hash.bits()];
                for (int rep = 0; rep < repetitions; ++rep) {
                    final Key key = new Randomized(new KeyOf(size), random);
                    final Hash original = func.apply(key, seed);
                    for (int row = 0; row < probe.bits(); ++row) {
                        final Key flipped = new Flipped(key, row);
                        final Hash changed = func.apply(flipped, seed);
                        for (int column = 0; column < hash.bits(); ++column) {
                            final int obit = new HashBit(original, column)
                                .value();
                            final int fbit = new HashBit(changed, column)
                                .value();
                            flips[row][column] += new BitDiff(obit, fbit)
                                .value();
                        }
                    }
                }
                final double[][] probs = new double[probe.bits()][hash.bits()];
                for (int row = 0; row < probe.bits(); ++row) {
                    for (int column = 0; column < hash.bits(); ++column) {
                        probs[row][column] = flips[row][column]
                            / (double) repetitions;
                    }
                }
                return probs;
            }
        );
        this.bas = new Cached<>(() -> new Bias(this));
    }

    @Override
    public double[][] value() {
        return this.mtx.value();
    }

    /**
     * Calculate the probability of elements of the matrix under a threshold
     * (normally 50%).
     * @return The probability of elements under a threshold
     */
    public double probability() {
        final double[][] probs = this.mtx.value();
        int count = 0;
        for (final double[] row : probs) {
            for (final double probability : row) {
                if (probability < Matrix.THRESHOLD) {
                    ++count;
                }
            }
        }
        return count / (double) (probs.length * probs[0].length);
    }

    /**
     * Get the bias of the matrix.
     * @return The bias
     */
    public Bias bias() {
        return this.bas.value();
    }
}
