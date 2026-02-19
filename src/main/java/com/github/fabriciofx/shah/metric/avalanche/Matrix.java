/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.Scalar;

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
@SuppressWarnings({
    "PMD.ArrayIsStoredDirectly",
    "PMD.MethodReturnsInternalArray"
})
public final class Matrix implements Scalar<double[][]> {
    /**
     * Threshold of 50%.
     */
    private static final double DEFAULT_THRESHOLD = 0.50;

    /**
     * Probabilities matrix.
     */
    private final double[][] probs;

    /**
     * Ctor.
     * @param probs Probabilities matrix
     */
    public Matrix(final double[]... probs) {
        this.probs = probs;
    }

    @Override
    public double[][] value() {
        return this.probs;
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
        for (final double[] row : this.probs) {
            for (final double probability : row) {
                if (probability < limit) {
                    ++count;
                }
            }
        }
        return count / (double) (this.probs.length * this.probs[0].length);
    }

    /**
     * Get the bias of the matrix.
     * @return The bias
     */
    public Bias bias() {
        return new Bias(this.probs);
    }
}
