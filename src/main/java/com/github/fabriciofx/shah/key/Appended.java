/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;

/**
 * Appended.
 *
 * <p>Append bytes to a key.</p>
 *
 * @since 0.0.1
 */
public final class Appended implements Key {
    /**
     * Expanded key.
     */
    private final Scalar<Key> expanded;

    /**
     * Ctor.
     * @param key The key
     * @param bytes The bytes do be appended
     */
    public Appended(final Key key, final byte[] bytes) {
        this.expanded = new Cached<>(
            () -> {
                final byte[] dest = new byte[key.size() + bytes.length];
                System.arraycopy(key.asBytes(), 0, dest, 0, key.size());
                System.arraycopy(bytes, 0, dest, key.size(), bytes.length);
                return new KeyOf(dest);
            }
        );
    }

    @Override
    public byte[] asBytes() {
        return this.expanded.value().asBytes();
    }

    @Override
    public String asString() {
        return this.expanded.value().asString();
    }

    @Override
    public int bits() {
        return this.expanded.value().bits();
    }

    @Override
    public int size() {
        return this.expanded.value().size();
    }
}
