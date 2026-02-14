/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Metric;

/**
 * Bit Independence Criterion (BIC) bias metric from SMHasher.
 *
 * <p>Compute the worst BIC bias. The worst bias as a value between 0.0 and
 * 1.0.</p>
 *
 * <p>Given a 2x2 contingency table for each pair of output bits,
 * computes the worst bias. Under independence, each of the 4
 * outcomes {00, 01, 10, 11} should appear with probability 1/4.
 * The bias is {@code |count / expected - 1|}. SMHasher considers
 * a bias above 5% (0.05) as a failure.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle CyclomaticComplexityCheck (100 lines)
 * @checkstyle NestedForDepthCheck (100 lines)
 */
@SuppressWarnings({"PMD.CognitiveComplexity", "PMD.UnnecessaryLocalRule"})
public final class BicBias implements Metric {
    /**
     * Contingency tables: bins[outputBit1][outputBit2][outcome].
     */
    private final int[][][] bins;

    /**
     * Number of repetitions used to fill the bins.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param bins Contingency tables bins[outBit1][outBit2][outcome]
     * @param repetitions Number of repetitions used to fill the bins
     */
    public BicBias(final int[][][] bins, final int repetitions) {
        this.bins = bins.clone();
        this.repetitions = repetitions;
    }

    @Override
    public double value() {
        final double expected = this.repetitions / 4.0;
        double worst = 0.0;
        for (final int[][] row : this.bins) {
            for (final int[] pair : row) {
                for (final int count : pair) {
                    if (count > 0) {
                        final double bias = Math.abs(
                            count / expected - 1.0
                        );
                        if (bias > worst) {
                            worst = bias;
                        }
                    }
                }
            }
        }
        return worst;
    }
}
