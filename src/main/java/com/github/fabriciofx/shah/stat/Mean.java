/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.stat;

import com.github.fabriciofx.shah.Scalar;
import java.util.Collection;

/**
 * Mean (average) of a collection of values.
 * @since 0.0.1
 */
public final class Mean implements Scalar<Double> {
    /**
     * Values.
     */
    private final Collection<? extends Number> values;

    /**
     * Ctor.
     * @param values Values
     */
    public Mean(final Collection<? extends Number> values) {
        this.values = values;
    }

    @Override
    public Double value() {
        return this.values.stream()
            .mapToDouble(Number::doubleValue)
            .average()
            .orElse(0.0);
    }
}
