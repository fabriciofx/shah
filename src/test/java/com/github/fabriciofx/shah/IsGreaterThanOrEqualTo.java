/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Hamcrest matcher that checks if a value is greater than or equal to than a
 * threshold.
 *
 * @since 0.0.1
 * @checkstyle ProtectedMethodInFinalClassCheck (200 lines)
 */
public final class IsGreaterThanOrEqualTo extends TypeSafeMatcher<Double> {
    /**
     * The threshold value.
     */
    private final double threshold;

    /**
     * Description label.
     */
    private final String label;

    /**
     * Ctor.
     * @param threshold The threshold value
     * @param label Description label for the metric
     */
    public IsGreaterThanOrEqualTo(final double threshold, final String label) {
        super();
        this.threshold = threshold;
        this.label = label;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText(this.label)
            .appendText(" greater than or equal to ")
            .appendValue(this.threshold);
    }

    @Override
    protected boolean matchesSafely(final Double actual) {
        return actual >= this.threshold;
    }

    @Override
    protected void describeMismatchSafely(
        final Double item,
        final Description description
    ) {
        description.appendText("was ").appendValue(item);
    }
}
