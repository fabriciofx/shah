/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hash;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.BytesDiff;
import java.util.HexFormat;

/**
 * Hash32.
 *
 * <p>A 32-bit hash implementation.</p>
 *
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (200 lines)
 */
public final class Hash32 implements Hash {
    /**
     * The hash.
     */
    private final int value;

    /**
     * Ctor.
     * @param scalar A Scalar the returns a byte[]
     */
    public Hash32(final Scalar<byte[]> scalar) {
        this(scalar.value());
    }

    /**
     * Ctor.
     *
     * @param bytes The 4 bytes of the hash
     */
    public Hash32(final byte... bytes) {
        this(
            (bytes[0] & 0xFF)
                | ((bytes[1] & 0xFF) << 8)
                | ((bytes[2] & 0xFF) << 16)
                | ((bytes[3] & 0xFF) << 24)
        );
    }

    /**
     * Ctor.
     *
     * @param value The 32-bit hash value
     */
    public Hash32(final int value) {
        this.value = value;
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{
            (byte) this.value,
            (byte) (this.value >>> 8),
            (byte) (this.value >>> 16),
            (byte) (this.value >>> 24),
        };
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return 32;
    }

    @Override
    public Hash diff(final Hash other) {
        return new Hash32(
            new BytesDiff(
                this.asBytes(),
                other.asBytes()
            )
        );
    }

    @Override
    public byte byteAt(final int index) {
        return (byte) ((this.value >>> (index << 3)) & 0xFF);
    }

    @Override
    public int bitAt(final int index) {
        return (this.value >>> index) & 1;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Hash32
            && this.value == Hash32.class.cast(other).value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public int compareTo(final Hash other) {
        return Integer.compare(this.value, Hash32.class.cast(other).value);
    }
}
