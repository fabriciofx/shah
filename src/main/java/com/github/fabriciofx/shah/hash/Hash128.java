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
 * Hash128.
 *
 * <p>A 128-bit hash implementation.</p>
 *
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (200 lines)
 */
public final class Hash128 implements Hash {
    /**
     * The hash.
     */
    private final long[] value;

    /**
     * Ctor.
     * @param scalar A Scalar the returns a byte[]
     */
    public Hash128(final Scalar<byte[]> scalar) {
        this(scalar.value());
    }

    /**
     * Ctor.
     *
     * @param bytes The 16 bytes of the hash
     */
    public Hash128(final byte... bytes) {
        this(
            (bytes[0] & 0xFFL)
                | ((bytes[1] & 0xFFL) << 8)
                | ((bytes[2] & 0xFFL) << 16)
                | ((bytes[3] & 0xFFL) << 24)
                | ((bytes[4] & 0xFFL) << 32)
                | ((bytes[5] & 0xFFL) << 40)
                | ((bytes[6] & 0xFFL) << 48)
                | ((bytes[7] & 0xFFL) << 56),
            (bytes[8] & 0xFFL)
                | ((bytes[9] & 0xFFL) << 8)
                | ((bytes[10] & 0xFFL) << 16)
                | ((bytes[11] & 0xFFL) << 24)
                | ((bytes[12] & 0xFFL) << 32)
                | ((bytes[13] & 0xFFL) << 40)
                | ((bytes[14] & 0xFFL) << 48)
                | ((bytes[15] & 0xFFL) << 56)
        );
    }

    /**
     * Ctor.
     *
     * @param value The 128-bit hash value
     */
    public Hash128(final long... value) {
        this.value = value.clone();
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{
            (byte) this.value[0],
            (byte) (this.value[0] >>> 8),
            (byte) (this.value[0] >>> 16),
            (byte) (this.value[0] >>> 24),
            (byte) (this.value[0] >>> 32),
            (byte) (this.value[0] >>> 40),
            (byte) (this.value[0] >>> 48),
            (byte) (this.value[0] >>> 56),
            (byte) this.value[1],
            (byte) (this.value[1] >>> 8),
            (byte) (this.value[1] >>> 16),
            (byte) (this.value[1] >>> 24),
            (byte) (this.value[1] >>> 32),
            (byte) (this.value[1] >>> 40),
            (byte) (this.value[1] >>> 48),
            (byte) (this.value[1] >>> 56),
        };
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return 128;
    }

    @Override
    public Hash diff(final Hash other) {
        return new Hash128(
            new BytesDiff(
                this.asBytes(),
                other.asBytes()
            )
        );
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Hash128
            && this.value[0] == Hash128.class.cast(other).value[0]
            && this.value[1] == Hash128.class.cast(other).value[1];
    }

    @Override
    public int hashCode() {
        long code = this.value[0];
        code ^= this.value[1] + 0x9e3779b97f4a7c15L + (code << 6)
            + (code >>> 2);
        return Long.hashCode(code);
    }
}
