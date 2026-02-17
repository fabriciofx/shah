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
 * <p>Given a matrix of probabilities, computes the worst bias across all
 * matrix. The bias is {@code |2 * probability - 1|}. A bias of 0.0 means
 * perfect (exactly 50% flip rate). SMHasher considers a bias above 1% (0.01)
 * as a failure.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
public final class AvalancheBias implements Metric {
    /**
     * Probabilities matrix.
     */
    private final double[][] probs;

    /**
     * Ctor.
     * @param probs Probabilities matrix
     */
    public AvalancheBias(final double[]... probs) {
        this.probs = probs;
    }

    @Override
    public double value() {
        double worst = 0.0;
        for (final double[] row : this.probs) {
            for (final double probability : row) {
                final double bias = Math.abs(2.0 * probability - 1.0);
                if (bias > worst) {
                    worst = bias;
                }
            }
        }
        return worst;
    }
}
