/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hash;

import com.github.fabriciofx.shah.Hash;
import java.util.HexFormat;

/**
 * Hash64.
 *
 * <p>A 64-bit hash implementation.</p>
 *
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (200 lines)
 */
public final class Hash64 implements Hash {
    /**
     * The hash.
     */
    private final long value;

    /**
     * Ctor.
     *
     * @param bytes The 8 bytes of the hash
     */
    public Hash64(final byte... bytes) {
        this(
            (bytes[0] & 0xFFL)
                | ((bytes[1] & 0xFFL) << 8)
                | ((bytes[2] & 0xFFL) << 16)
                | ((bytes[3] & 0xFFL) << 24)
                | ((bytes[4] & 0xFFL) << 32)
                | ((bytes[5] & 0xFFL) << 40)
                | ((bytes[6] & 0xFFL) << 48)
                | ((bytes[7] & 0xFFL) << 56)
        );
    }

    /**
     * Ctor.
     *
     * @param value The 64-bit hash value
     */
    public Hash64(final long value) {
        this.value = value;
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{
            (byte) this.value,
            (byte) (this.value >>> 8),
            (byte) (this.value >>> 16),
            (byte) (this.value >>> 24),
            (byte) (this.value >>> 32),
            (byte) (this.value >>> 40),
            (byte) (this.value >>> 48),
            (byte) (this.value >>> 56),
        };
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return 64;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Hash64
            && this.value == Hash64.class.cast(other).value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.value);
    }
}
