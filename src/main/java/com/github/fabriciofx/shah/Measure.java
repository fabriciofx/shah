/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Measure.
 *
 * @param <M> Type of the measure metric
 * @since 0.0.1
 */
@FunctionalInterface
public interface Measure<M> {

    /**
     * Get the metric of the measure.
     *
     * @return The metric of the measure
     */
    M metric();
}
