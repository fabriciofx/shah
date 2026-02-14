/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Test.
 * @param <T> Type of the test result
 * @since 0.0.1
 */
@FunctionalInterface
public interface Test<T> {
    /**
     * Get the result of the test.
     * @return The result of the test
     */
    T value();
}
