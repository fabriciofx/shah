/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.Metric;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;

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
public final class Matrix implements Metric<double[][]> {
    /**
     * Threshold of 50%.
     */
    private static final double DEFAULT_THRESHOLD = 0.50;

    /**
     * Probabilities matrix.
     */
    private final Scalar<double[][]> probs;

    /**
     * Ctor.
     * @param repetitions Number of repetitions
     * @param flips Flips matrix
     */
    public Matrix(final int repetitions, final int[]... flips) {
        this.probs = new Cached<>(
            () -> {
                final double[][] matrix =
                    new double[flips.length][flips[0].length];
                for (int row = 0; row < flips.length; ++row) {
                    for (int column = 0; column < flips[0].length; ++column) {
                        matrix[row][column] = flips[row][column]
                            / (double) repetitions;
                    }
                }
                return matrix;
            }
        );
    }

    @Override
    public double[][] value() {
        return this.probs.value();
    }

    /**
     * Computer the probability of elements of the matrix under a threshold
     * (normally 50%).
     * @param threshold The threshold value. If not specified, a 50% threshold
     *  will be assumed.
     * @return The probability of elements under a threshold
     */
    public double probability(final double... threshold) {
        final double limit;
        if (threshold.length == 0) {
            limit = Matrix.DEFAULT_THRESHOLD;
        } else {
            limit = threshold[0];
        }
        int count = 0;
        for (final double[] row : this.value()) {
            for (final double probability : row) {
                if (probability < limit) {
                    ++count;
                }
            }
        }
        return count / (double) (this.value().length * this.value()[0].length);
    }

    /**
     * Get the bias of the matrix.
     * @return The bias
     */
    public Bias bias() {
        return new Bias(this.value());
    }
}
