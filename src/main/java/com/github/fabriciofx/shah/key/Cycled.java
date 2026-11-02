/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Random;

/**
 * Cycled.
 * @since 0.0.1
 */
public final class Cycled implements Key {
    /**
     * Cycled key.
     */
    private final Scalar<Key> key;

    /**
     * Ctor.
     * @param key Key to be cycled
     * @param length The length of cycle
     * @param index The index
     */
    public Cycled(final Key key, final int length, final int index) {
        this.key = new Cached<>(
            () -> {
                final Random random = new Random(483_723L);
                final byte[] cycle = new byte[length];
                random.nextBytes(cycle);
                final int mixed = Cycled.finalMix(index ^ 0x746A94F1);
                cycle[0] = (byte) mixed;
                if (length > 1) {
                    cycle[1] = (byte) (mixed >>> 8);
                }
                if (length > 2) {
                    cycle[2] = (byte) (mixed >>> 16);
                }
                if (length > 3) {
                    cycle[3] = (byte) (mixed >>> 24);
                }
                final byte[] bytes = key.asBytes();
                for (int pos = 0; pos < bytes.length; ++pos) {
                    bytes[pos] = cycle[pos % length];
                }
                return new KeyOf(bytes);
            }
        );
    }

    @Override
    public byte[] asBytes() {
        return this.key.value().asBytes();
    }

    @Override
    public String asString() {
        return this.key.value().asString();
    }

    @Override
    public int bits() {
        return this.key.value().bits();
    }

    @Override
    public int size() {
        return this.key.value().size();
    }

    /**
     * MurmurHash3 32-bit finalizer (f3mix from SMHasher).
     * @param value Input value
     * @return Mixed value
     */
    private static int finalMix(final int value) {
        int mixed = value;
        mixed ^= mixed >>> 16;
        mixed *= 0x85EBCA6B;
        mixed ^= mixed >>> 13;
        mixed *= 0xC2B2AE35;
        mixed ^= mixed >>> 16;
        return mixed;
    }
}
