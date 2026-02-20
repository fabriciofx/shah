/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Key.
 *
 * <p>A Key is an input to a hash function.</p>
 *
 * @since 0.0.1
 */
public interface Key {
    /**
     * Get the key as a byte array.
     * @return The byte array representation of the key
     */
    byte[] asBytes();

    /**
     * Get the key as a hexadecimal string.
     * @return The hexadecimal string representation of the key
     */
    String asString();

    /**
     * Get the number of bits in the key.
     * @return The number of bits in the key
     */
    int bits();

    /**
     * Retrieve the number of bytes of the key.
     * @return The number of bytes
     */
    int size();
}
