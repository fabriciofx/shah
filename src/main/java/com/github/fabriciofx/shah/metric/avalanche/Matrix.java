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
     * Threshold.
     */
    private final double threshold;

    /**
     * Ctor.
     * @param probs Probabilities matrix
     */
    public Matrix(final double[]... probs) {
        this(Matrix.DEFAULT_THRESHOLD, probs);
    }

    /**
     * Ctor.
     * @param threshold Probability threshold
     * @param probs Probabilities matrix
     */
    public Matrix(final double threshold, final double[]... probs) {
        this.probs = probs;
        this.threshold = threshold;
    }

    @Override
    public double[][] value() {
        return this.probs;
    }

    /**
     * Calculate the probability of elements of the matrix under a threshold
     * (normally 50%).
     * @return The probability of elements under a threshold
     */
    public double probability() {
        int count = 0;
        for (final double[] row : this.probs) {
            for (final double probability : row) {
                if (probability < this.threshold) {
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
