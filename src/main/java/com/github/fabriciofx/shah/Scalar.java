/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Scalar.
 * @param <T> Type of the scalar value
 * @since 0.0.1
 */
@FunctionalInterface
public interface Scalar<T> {
    /**
     * Get the scalar value.
     * @return The scalar value
     */
    T value();
}
