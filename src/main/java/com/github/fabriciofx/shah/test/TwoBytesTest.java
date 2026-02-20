/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.CollisionRatio;
import java.util.function.Function;

/**
 * Two-bytes keyset test from SMHasher.
 *
 * <p>Compute the collision ratio for two-byte keyset.</p>
 *
 * <p>Generates all possible keys of a given length where at most two
 * bytes are non-zero. This tests how the hash function handles sparse
 * input data, which is common in real-world applications.</p>
 *
 * <p>For a key of length N, the number of keys generated is:
 * {@code 1 + N*255 + N*(N-1)/2 * 255*255} (zero key + one-byte
 * variations + two-byte variations).</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TwoBytesTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Key length.
     */
    private final int size;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Key length
     */
    public TwoBytesTest(final Function<Key, Hash> func, final int size) {
        this.func = func;
        this.size = size;
    }

    @Override
    public Double metric() {
        final Hashes hashes = new HashesOf();
        final byte[] bytes = new byte[this.size];
        hashes.add(this.func.apply(new KeyOf(bytes)));
        for (int pos = 0; pos < this.size; ++pos) {
            for (int val = 1; val < 256; ++val) {
                bytes[pos] = (byte) val;
                hashes.add(this.func.apply(new KeyOf(bytes)));
                bytes[pos] = 0;
            }
        }
        for (int first = 0; first < this.size; ++first) {
            for (int second = first + 1; second < this.size; ++second) {
                for (int one = 1; one < 256; ++one) {
                    bytes[first] = (byte) one;
                    for (int two = 1; two < 256; ++two) {
                        bytes[second] = (byte) two;
                        hashes.add(this.func.apply(new KeyOf(bytes)));
                    }
                    bytes[second] = 0;
                }
                bytes[first] = 0;
            }
        }
        return new CollisionRatio(hashes).value();
    }
}
