/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Filled.
 *
 * <p>Fill a key with a byte value.</p>
 *
 * @since 0.0.1
 */
public final class Filled implements Key {
    /**
     * Bytes.
     */
    private final Scalar<byte[]> bytes;

    /**
     * Ctor.
     *
     * @param key Key
     * @param fill Byte value to fill the key
     */
    public Filled(final Key key, final int fill) {
        this.bytes = new Cached<>(
            () -> {
                final byte[] bts = key.asBytes();
                Arrays.fill(bts, (byte) fill);
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
