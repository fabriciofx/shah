/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Scalar;

/**
 * Ratio.
 * @since 0.0.1
 */
public final class Ratio implements Scalar<Double> {
    /**
     * Antecedent, the ratio numerator.
     */
    private final double antecedent;

    /**
     * Consequent, the ratio denominator.
     */
    private final double consequent;

    /**
     * Ctor.
     * @param antecedent The ratio numerator
     * @param consequent The ratio denominator
     */
    public Ratio(final double antecedent, final double consequent) {
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    @Override
    public Double value() {
        final double ratio;
        if (this.antecedent == 0.0 || this.consequent == 0.0) {
            ratio = 0.0;
        } else {
            ratio = this.antecedent / this.consequent;
        }
        return ratio;
    }
}
