/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Hash function.
 *
 * <p>A hash function takes a key and produces a hash value.</p>
 *
 * @since 0.0.1
 */
@FunctionalInterface
public interface Func {
    /**
     * Apply the hash function to the given key.
     * @return The hash value
     */
    Hash hash();
}
