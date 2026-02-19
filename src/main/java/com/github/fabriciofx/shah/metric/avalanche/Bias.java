/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Arrays;

/**
 * Avalanche bias from SMHasher.
 *
 * <p>Given a matrix of probabilities, computes the worst bias across all
 * matrix. The bias is {@code |2 * probability - 1|}. A bias of 0.0 means
 * perfect (exactly 50% flip rate). SMHasher considers a bias above 1% (0.01)
 * as a failure.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class Bias {
    /**
     * Bias matrix.
     */
    private final Scalar<double[][]> biases;

    /**
     * Ctor.
     *
     * @param probs Avalanche probability matrix
     */
    public Bias(final double[]... probs) {
        this.biases = new Cached<>(
            () -> {
                final double[][] bias =
                    new double[probs.length][probs[0].length];
                for (int row = 0; row < probs.length; ++row) {
                    for (int col = 0; col < probs[0].length; ++col) {
                        bias[row][col] = Math.abs(2.0 * probs[row][col] - 1.0);
                    }
                }
                return bias;
            }
        );
    }

    /**
     * Compute the mean of the bias.
     * @return The mean
     */
    public double mean() {
        return Arrays.stream(this.biases.value())
            .flatMapToDouble(Arrays::stream)
            .average()
            .orElse(0.0);
    }

    /**
     * Compute the worst bias. The worst bias is a value between 0.0 and 1.0.
     * @return The worst bias
     */
    public double max() {
        return Arrays.stream(this.biases.value())
            .flatMapToDouble(Arrays::stream)
            .max()
            .orElse(0.0);
    }

    /**
     * Compute the root-mean-square (rms) of the bias.
     * @return The rms
     */
    public double rms() {
        return Math.sqrt(
            Arrays.stream(this.biases.value())
                .flatMapToDouble(Arrays::stream)
                .map(bias -> bias * bias)
                .average()
                .orElse(0.0)
        );
    }

    /**
     * Compute the worst bias in a row.
     * @param num The row number (0 based)
     * @return The worst bias in a row
     */
    public double row(final int num) {
        return Arrays.stream(this.biases.value()[num])
            .max()
            .orElse(0.0);
    }

    /**
     * Compute the worst bias in a column.
     * @param num The column number (0 based)
     * @return The worst bias in a column
     */
    public double column(final int num) {
        return Arrays.stream(this.biases.value())
            .mapToDouble(row -> row[num])
            .max()
            .orElse(0.0);
    }
}
