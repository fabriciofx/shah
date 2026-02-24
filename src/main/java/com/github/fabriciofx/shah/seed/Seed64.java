/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.seed;

import com.github.fabriciofx.shah.Seed;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Seed64.
 * @since 0.0.1
 */
public final class Seed64 implements Seed {
    /**
     * The seed value.
     */
    private final long value;

    /**
     * Ctor.
     */
    public Seed64() {
        this(ThreadLocalRandom.current().nextLong());
    }

    /**
     * Ctor.
     * @param value The seed value
     */
    public Seed64(final long value) {
        this.value = value;
    }

    @Override
    public byte[] asBytes() {
        return new byte[]{
            (byte) this.value,
            (byte) (this.value >>> 8),
            (byte) (this.value >>> 16),
            (byte) (this.value >>> 24),
            (byte) (this.value >>> 32),
            (byte) (this.value >>> 40),
            (byte) (this.value >>> 48),
            (byte) (this.value >>> 56),
        };
    }

    @Override
    public int asInt() {
        return Long.hashCode(this.value);
    }

    @Override
    public long asLong() {
        return this.value;
    }
}
