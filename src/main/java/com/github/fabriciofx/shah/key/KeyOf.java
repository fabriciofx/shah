/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

/**
 * KeyOf.
 *
 * <p>It represents a hash input.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings({
    "PMD.ArrayIsStoredDirectly",
    "PMD.MethodReturnsInternalArray"
})
public final class KeyOf implements Key {
    /**
     * Bytes.
     */
    private final byte[] bytes;

    /**
     * Ctor.
     * @param string The string
     */
    public KeyOf(final String string) {
        this(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Ctor.
     * @param size The size in bytes
     */
    public KeyOf(final int size) {
        this(new byte[size]);
    }

    /**
     * Ctor.
     * @param bytes The bytes
     */
    public KeyOf(final byte... bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] asBytes() {
        return this.bytes;
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.bytes);
    }

    @Override
    public int bits() {
        return this.bytes.length * Byte.SIZE;
    }
}
