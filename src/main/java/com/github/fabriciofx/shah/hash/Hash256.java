/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hash;

import com.github.fabriciofx.shah.Hash;
import java.util.HexFormat;

/**
 * Hash256.
 *
 * <p>A 256-bit hash implementation.</p>
 *
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (200 lines)
 */
public final class Hash256 implements Hash {
    /**
     * The hash.
     */
    private final long[] value;

    /**
     * Ctor.
     *
     * @param bytes The 32 bytes of the hash
     */
    public Hash256(final byte... bytes) {
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
                | ((bytes[15] & 0xFFL) << 56),
            (bytes[16] & 0xFFL)
                | ((bytes[17] & 0xFFL) << 8)
                | ((bytes[18] & 0xFFL) << 16)
                | ((bytes[19] & 0xFFL) << 24)
                | ((bytes[20] & 0xFFL) << 32)
                | ((bytes[21] & 0xFFL) << 40)
                | ((bytes[22] & 0xFFL) << 48)
                | ((bytes[23] & 0xFFL) << 56),
            (bytes[24] & 0xFFL)
                | ((bytes[25] & 0xFFL) << 8)
                | ((bytes[26] & 0xFFL) << 16)
                | ((bytes[27] & 0xFFL) << 24)
                | ((bytes[28] & 0xFFL) << 32)
                | ((bytes[29] & 0xFFL) << 40)
                | ((bytes[30] & 0xFFL) << 48)
                | ((bytes[31] & 0xFFL) << 56)
        );
    }

    /**
     * Ctor.
     *
     * @param value The 256-bit hash value
     */
    public Hash256(final long... value) {
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
            (byte) this.value[2],
            (byte) (this.value[2] >>> 8),
            (byte) (this.value[2] >>> 16),
            (byte) (this.value[2] >>> 24),
            (byte) (this.value[2] >>> 32),
            (byte) (this.value[2] >>> 40),
            (byte) (this.value[2] >>> 48),
            (byte) (this.value[2] >>> 56),
            (byte) this.value[3],
            (byte) (this.value[3] >>> 8),
            (byte) (this.value[3] >>> 16),
            (byte) (this.value[3] >>> 24),
            (byte) (this.value[3] >>> 32),
            (byte) (this.value[3] >>> 40),
            (byte) (this.value[3] >>> 48),
            (byte) (this.value[3] >>> 56),
        };
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return 256;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Hash256
            && this.value[0] == Hash256.class.cast(other).value[0]
            && this.value[1] == Hash256.class.cast(other).value[1]
            && this.value[2] == Hash256.class.cast(other).value[2]
            && this.value[3] == Hash256.class.cast(other).value[3];
    }

    @Override
    public int hashCode() {
        long code = 0;
        code ^= this.value[0] + 0x9e3779b97f4a7c15L;
        code ^= this.value[1] + 0x9e3779b97f4a7c15L + (code << 6)
            + (code >>> 2);
        code ^= this.value[2] + 0x9e3779b97f4a7c15L + (code << 6)
            + (code >>> 2);
        code ^= this.value[3] + 0x9e3779b97f4a7c15L + (code << 6)
            + (code >>> 2);
        return Long.hashCode(code);
    }
}
