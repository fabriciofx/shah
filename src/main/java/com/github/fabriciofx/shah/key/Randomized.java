/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Random;

/**
 * Randomized.
 *
 * <p>Randomize a key with random bytes.</p>
 *
 * @since 0.0.1
 */
public final class Randomized implements Key {
    /**
    * Bytes.
    */
    private final Scalar<byte[]> bytes;

    /**
     * Ctor.
     *
     * @param key Key
     */
    public Randomized(final Key key) {
        this(key, new SecureRandom().nextLong());
    }

    /**
    * Ctor.
    *
    * @param key Key
    * @param seed Seed for randomization
    */
    public Randomized(final Key key, final long seed) {
        this(key, new Random(seed));
    }

    /**
     * Ctor.
     *
     * @param key Key
     * @param random Random for randomization
     */
    public Randomized(final Key key, final Random random) {
        this.bytes = new Cached<>(
            () -> {
                final byte[] bts = key.asBytes();
                random.nextBytes(bts);
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
