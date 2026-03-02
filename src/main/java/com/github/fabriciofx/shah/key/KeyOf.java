/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import java.nio.charset.StandardCharsets;

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
        final StringBuilder out = new StringBuilder();
        for (final byte bte : this.bytes) {
            out.append(String.format("%02X ", bte));
        }
        out.append('(');
        for (final byte bte : this.bytes) {
            for (int bit = 7; bit >= 0; --bit) {
                out.append(((bte & 0xFF) >>> bit) & 1);
            }
            out.append(' ');
        }
        out.setLength(out.length() - 1);
        out.append(')');
        return out.toString();
    }

    @Override
    public int bits() {
        return this.bytes.length * Byte.SIZE;
    }

    @Override
    public int size() {
        return this.bytes.length;
    }
}
