/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import java.util.HexFormat;

/**
 * Noised.
 * @since 0.0.1
 */
public final class Noised implements Key {
    /**
     * Key.
     */
    private final Key origin;

    /**
     * Noise value.
     */
    private final int noise;

    /**
     * Ctor.
     * @param key The key
     * @param noise The noise
     */
    public Noised(final Key key, final int noise) {
        this.origin = key;
        this.noise = noise;
    }

    @Override
    public byte[] asBytes() {
        final byte[] bytes = this.origin.asBytes();
        for (int idx = 0; idx < bytes.length; ++idx) {
            bytes[idx] = (byte) (this.noise >>> (8 * idx));
        }
        return bytes;
    }

    @Override
    public String asString() {
        return HexFormat.of().formatHex(this.asBytes());
    }

    @Override
    public int bits() {
        return this.origin.bits();
    }

    @Override
    public int size() {
        return this.origin.size();
    }
}
