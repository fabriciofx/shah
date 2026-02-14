/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * A statistical measurement of hash function quality.
 *
 * <p>A Metric measures a specific statistical property of hash output
 * (e.g., avalanche bias, bit independence, collision ratio, distribution
 * uniformity, etc.).</p>
 *
 * @since 0.0.1
 */
@FunctionalInterface
public interface Metric {
    /**
     * Compute the metric value.
     * @return The metric score, where 0.0 means perfect
     */
    double value();
}
