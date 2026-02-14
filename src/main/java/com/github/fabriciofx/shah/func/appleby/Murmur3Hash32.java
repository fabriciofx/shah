/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.appleby;

import com.github.fabriciofx.shah.Func;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.hash.Hash32;

/**
 * Murmur3 32-bit hash.
 *
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (100 lines)
 */
public final class Murmur3Hash32 implements Func {
    /**
     * The key to be hashed.
     */
    private final Key key;

    /**
     * Random seed for reproducibility.
     */
    private final int seed;

    /**
     * Ctor.
     * @param key The key to be hashed
     * @param seed Random seed for reproducibility
     */
    public Murmur3Hash32(final Key key, final int seed) {
        this.key = key;
        this.seed = seed;
    }

    @Override
    public Hash hash() {
        final byte[] bytes = this.key.asBytes();
        final int length = bytes.length;
        int hash = this.seed;
        int offset = 0;
        int data;
        for (int idx = length >> 2; idx != 0; --idx) {
            data = littleEndian(bytes, offset);
            offset += 4;
            hash ^= scramble(data);
            hash = (hash << 13) | (hash >>> 19);
            hash = hash * 5 + 0xe6546b64;
        }
        data = 0;
        for (int idx = length & 3; idx != 0; --idx) {
            data <<= 8;
            data |= bytes[offset + idx - 1] & 0xFF;
        }
        hash ^= scramble(data);
        hash ^= length;
        hash ^= hash >>> 16;
        hash *= 0x85ebca6b;
        hash ^= hash >>> 13;
        hash *= 0xc2b2ae35;
        hash ^= hash >>> 16;
        return new Hash32(hash);
    }

    private static int scramble(final int value) {
        int scrambled = value;
        scrambled *= 0xcc9e2d51;
        scrambled = (scrambled << 15) | (scrambled >>> 17);
        scrambled *= 0x1b873593;
        return scrambled;
    }

    private static int littleEndian(final byte[] bytes, final int offset) {
        return (bytes[offset] & 0xFF)
            | ((bytes[1 + offset] & 0xFF) << 8)
            | ((bytes[2 + offset] & 0xFF) << 16)
            | ((bytes[3 + offset] & 0xFF) << 24);
    }
}
