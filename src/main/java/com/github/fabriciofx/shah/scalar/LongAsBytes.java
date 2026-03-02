/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;

/**
 * LongAsBytes.
 * @since 0.0.1
 */
public final class LongAsBytes implements Scalar<byte[]> {
    /**
     * Value.
     */
    private final long input;

    /**
     * Amount of bits.
     */
    private final int bits;

    /**
     * Ctor.
     * @param input The value to be converted
     */
    public LongAsBytes(final long input) {
        this(input, 64);
    }

    /**
     * Ctor.
     * @param input The value to be converted
     * @param bits The amount of bits
     */
    public LongAsBytes(final long input, final int bits) {
        this.input = input;
        this.bits = bits;
    }

    @Override
    public byte[] value() {
        final int size = (this.bits + 7) / 8;
        final byte[] bytes = new byte[size];
        for (int idx = 0; idx < size; ++idx) {
            bytes[idx] = (byte) (this.input >>> (8 * idx));
        }
        return bytes;
    }
}
