/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Metric;

/**
 * Avalanche bias metric from SMHasher.
 *
 * <p>Compute the worst avalanche bias. The worst bias as a value between 0.0
 * and 1.0.</p>
 *
 * <p>Given a matrix of bit-flip counts, computes the worst bias
 * across all (input bit, output bit) pairs. For each pair, the bias
 * is {@code |2 * (flip_count / reps) - 1|}. A bias of 0.0 means
 * perfect (exactly 50% flip rate). SMHasher considers a bias above
 * 1% (0.01) as a failure.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class AvalancheBias implements Metric {
    /**
     * Flip-count matrix: bins[inputBit][outputBit].
     */
    private final int[][] bins;

    /**
     * Number of repetitions used to fill the bins.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param bins Flip-count matrix bins[inputBit][outputBit]
     * @param repetitions Number of repetitions used to fill the bins
     */
    public AvalancheBias(final int[][] bins, final int repetitions) {
        this.bins = bins.clone();
        this.repetitions = repetitions;
    }

    @Override
    public double value() {
        double worst = 0.0;
        for (final int[] row : this.bins) {
            for (final int count : row) {
                final double bias = Math.abs(
                    2.0 * count / this.repetitions - 1.0
                );
                if (bias > worst) {
                    worst = bias;
                }
            }
        }
        return worst;
    }
}
