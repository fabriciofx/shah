/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hash;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.BytesDiff;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Truncated hash.
 *
 * <p>A hash reduced to its low bits. Two truncated hashes are equal when
 * their low bits are equal, even if the original hashes differ in the high
 * bits. SMHasher3 measures collisions this way on wide hashes, since
 * finding collisions on all their bits would need too many keys.</p>
 *
 * <p>A hash narrower than the requested width keeps all its bits.</p>
 *
 * <pre>{@code
 * new Truncated(new Hash128(0x0123456789ABCDEFL, 0L), 32).asString();
 * }</pre>
 *
 * @since 0.0.1
 */
public final class Truncated implements Hash {

    /**
     * The bytes of the original hash.
     */
    private final Scalar<byte[]> bytes;

    /**
     * The number of low bits to keep.
     */
    private final int width;

    /**
     * Ctor.
     *
     * @param origin The original hash
     * @param width The number of low bits to keep, a multiple of 8
     */
    public Truncated(final Hash origin, final int width) {
        this(new Cached<>(origin::asBytes), width);
    }

    /**
     * Ctor.
     *
     * @param bytes The bytes of the original hash
     * @param width The number of low bits to keep, a multiple of 8
     */
    public Truncated(final Scalar<byte[]> bytes, final int width) {
        this.bytes = bytes;
        this.width = width;
    }

    @Override
    public byte[] asBytes() {
        return Arrays.copyOf(this.bytes.value(), this.bits() / Byte.SIZE);
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return Math.min(this.width, this.bytes.value().length * Byte.SIZE);
    }

    @Override
    public Hash diff(final Hash other) {
        return new Truncated(
            new BytesDiff(this.asBytes(), other.asBytes()),
            this.width
        );
    }

    @Override
    public byte byteAt(final int index) {
        return this.asBytes()[index];
    }

    @Override
    public int bitAt(final int index) {
        return (this.byteAt(index >>> 3) >>> (index & 7)) & 1;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Truncated
            && Arrays.equals(
                this.asBytes(),
                Truncated.class.cast(other).asBytes()
            );
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.asBytes());
    }

    @Override
    public int compareTo(final Hash other) {
        return Arrays.compareUnsigned(this.asBytes(), other.asBytes());
    }
}
