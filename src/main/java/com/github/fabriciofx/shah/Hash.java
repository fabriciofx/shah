/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Hash.
 *
 * <p>A hash output.</p>
 *
 * @since 0.0.1
 */
public interface Hash {
    /**
     * Get the hash as a byte array.
     * @return The byte array representation of the hash
     */
    byte[] asBytes();

    /**
     * Get the hash as a hexadecimal string.
     * @return The hexadecimal string representation of the hash
     */
    String asString();

    /**
     * Get the number of bits in the hash.
     * @return The number of bits in the hash
     */
    int bits();
}
