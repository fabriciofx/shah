/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Test.
 * @param <T> Type of the test metric
 * @since 0.0.1
 */
@FunctionalInterface
public interface Test<T> {
    /**
     * Get the metric of the test.
     * @return The metric of the test
     */
    T metric();
}
