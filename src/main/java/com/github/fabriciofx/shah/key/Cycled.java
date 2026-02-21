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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class Cycled implements Key {
    /**
     * Default random seed (matches SMHasher).
     */
    private static final long DEFAULT_SEED = 483_723L;

    /**
     * Mix constant from SMHasher f3mix.
     */
    private static final int MIX_XOR = 0x746a94f1;

    /**
     * First mix multiplier.
     */
    private static final int MIX_MUL1 = 0x85ebca6b;

    /**
     * Second mix multiplier.
     */
    private static final int MIX_MUL2 = 0xc2b2ae35;

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
                final Random random = new Random(Cycled.DEFAULT_SEED);
                final byte[] cycle = new byte[length];
                random.nextBytes(cycle);
                final int mixed = Cycled.finalMix(index ^ Cycled.MIX_XOR);
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
        mixed *= Cycled.MIX_MUL1;
        mixed ^= mixed >>> 13;
        mixed *= Cycled.MIX_MUL2;
        mixed ^= mixed >>> 16;
        return mixed;
    }
}
