/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.stat;

import com.github.fabriciofx.shah.Scalar;
import java.util.Collection;

/**
 * Variance of a collection of values.
 * @since 0.0.1
 */
public final class Variance implements Scalar<Double> {
    /**
     * Values.
     */
    private final Collection<? extends Number> values;

    /**
     * Mean of the values.
     */
    private final Scalar<Double> mean;

    /**
     * Ctor.
     * @param values Values
     */
    public Variance(final Collection<? extends Number> values) {
        this(values, new Mean(values));
    }

    /**
     * Ctor.
     * @param values Values
     * @param mean Mean of the values
     */
    public Variance(
        final Collection<? extends Number> values,
        final double mean
    ) {
        this(values, () -> mean);
    }

    /**
     * Ctor.
     * @param values Values
     * @param mean Mean of the values
     */
    public Variance(
        final Collection<? extends Number> values,
        final Scalar<Double> mean
    ) {
        this.values = values;
        this.mean = mean;
    }

    @Override
    public Double value() {
        return this.values.stream()
            .mapToDouble(Number::doubleValue)
            .map(
                value -> {
                    final double diff = value - this.mean.value();
                    return diff * diff;
                }
            )
            .sum() / (this.values.size() - 1);
    }
}
