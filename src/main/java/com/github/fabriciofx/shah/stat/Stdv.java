/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.stat;

import com.github.fabriciofx.shah.Scalar;

/**
 * Standard deviation of a collection of values.
 * @since 0.0.1
 */
public final class Stdv implements Scalar<Double> {
    /**
     * Variance of the values.
     */
    private final Scalar<Double> variance;

    /**
     * Ctor.
     * @param variance Variance of the values
     */
    public Stdv(final double variance) {
        this(() -> variance);
    }

    /**
     * Ctor.
     * @param variance Variance of the values
     */
    public Stdv(final Scalar<Double> variance) {
        this.variance = variance;
    }

    @Override
    public Double value() {
        return Math.sqrt(this.variance.value());
    }
}
