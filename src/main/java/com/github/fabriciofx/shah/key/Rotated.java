/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;

/**
 * Rotated.
 *
 * <p>Set the low width bits of the byte array to value, then left-rotate the
 * entire array by rotate bits. Matches SMHasher's approach:
 * {@code key = i; lrot(key, size, j)}.</p>
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
public final class Rotated implements Key {
    /**
     * Key to be rotated.
     */
    private final Key origin;

    /**
     * Value to set in low bits.
     */
    private final int value;

    /**
     * Width in bits.
     */
    private final int width;

    /**
     * Number of bits to rotate left.
     */
    private final int rotate;

    /**
     * Ctor.
     * @param key Key to be rotated
     * @param value Value to set in low bits
     * @param width Width in bits
     * @param rotate Number of bits to rotate left
     */
    public Rotated(
        final Key key,
        final int value,
        final int width,
        final int rotate
    ) {
        this.origin = key;
        this.value = value;
        this.width = width;
        this.rotate = rotate;
    }

    @Override
    public byte[] asBytes() {
        final byte[] bytes = this.origin.asBytes();
        final int nbits = bytes.length * Byte.SIZE;
        for (int bit = 0; bit < this.width; ++bit) {
            if (((this.value >> bit) & 1) == 1) {
                final int pos = (bit + this.rotate) % nbits;
                bytes[pos >> 3] |= (byte) (1 << (pos & 7));
            }
        }
        return bytes;
    }

    @Override
    public String asString() {
        return this.origin.asString();
    }

    @Override
    public int bits() {
        return this.origin.bits();
    }

    @Override
    public int size() {
        return this.origin.size();
    }
}
