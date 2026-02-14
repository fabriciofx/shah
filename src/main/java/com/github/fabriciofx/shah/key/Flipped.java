/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.HexFormat;

/**
 * Flipped.
 *
 * <p>Flip a single bit of a key.</p>
 *
 * @since 0.0.1
 */
public final class Flipped implements Key {
    /**
     * Bytes.
     */
    private final Scalar<byte[]> bytes;

    /**
     * Ctor.
     *
     * @param key Key
     * @param index Index of the bit to be flipped
     */
    public Flipped(final Key key, final int index) {
        this.bytes = new Cached<>(
            () -> {
                final byte[] bts = key.asBytes().clone();
                bts[index >> 3] ^= (byte) (1 << (index & 7));
                return bts;
            }
        );
    }

    @Override
    public byte[] asBytes() {
        return this.bytes.value();
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return this.bytes.value().length * Byte.SIZE;
    }
}
