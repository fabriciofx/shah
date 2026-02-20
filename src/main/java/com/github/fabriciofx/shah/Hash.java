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
public interface Hash extends Comparable<Hash> {
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

    /**
     * Compute the difference between it and other hash.
     * @param other The other hash
     * @return A new hash that is a difference
     */
    Hash diff(Hash other);

    /**
     * Retrieve the nth (0 - based) byte from hash.
     * @param index Nth (0 - based) byte
     * @return The nth byte
     */
    byte byteAt(int index);

    /**
     * Retrieve the nth (0 - based) bit from hash.
     * @param index Nth (0 - based) bit
     * @return The nth bit
     */
    int bitAt(int index);
}
