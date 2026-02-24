/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.seed;

import com.github.fabriciofx.shah.Seed;

/**
 * Seed32.
 * @since 0.0.1
 */
public final class Seed32 implements Seed {
    /**
     * The seed value.
     */
    private final int value;

    /**
     * Ctor.
     * @param value The seed value
     */
    public Seed32(final int value) {
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
    public int asInt() {
        return this.value;
    }

    @Override
    public long asLong() {
        return this.value;
    }
}
