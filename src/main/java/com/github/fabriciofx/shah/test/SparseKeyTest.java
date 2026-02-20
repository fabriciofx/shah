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
 * Sparse key test from SMHasher.
 *
 * <p>Compute the collision ratio for sparse keys.</p>
 *
 * <p>Generates keys of a given bit-length where only a small number of bits
 * (up to {@code max}) are set to 1. This creates a highly structured keyset
 * that tests the hash function's ability to distinguish inputs that are very
 * similar.</p>
 *
 * <p>The number of keys is the sum of C(bits, k) for k from 0 to max. For
 * typical parameters (e.g. 256 bits, 3 bits set), this produces a manageable
 * number of keys.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({
    "PMD.TestClassWithoutTestCases",
    "PMD.UnnecessaryLocalRule"
})
public final class SparseKeyTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Total key length in bits.
     */
    private final int bits;

    /**
     * Maximum number of bits set to 1.
     */
    private final int max;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param bits Total key length in bits
     * @param max Maximum number of bits set to 1
     */
    public SparseKeyTest(
        final Function<Key, Hash> func,
        final int bits,
        final int max
    ) {
        this.func = func;
        this.bits = bits;
        this.max = max;
    }

    @Override
    public Double metric() {
        final int size = (this.bits + 7) / 8;
        final Hashes hashes = new HashesOf();
        final byte[] bytes = new byte[size];
        SparseKeyTest.generate(
            this.func,
            bytes,
            this.bits,
            this.max,
            0,
            0,
            hashes
        );
        return new CollisionRatio(hashes).value();
    }

    /**
     * Recursively generate sparse keys with at most maxBits set.
     * @param func Hash function
     * @param bytes Key buffer
     * @param total Total bits in bytes
     * @param remaining Remaining bits that can be set
     * @param start Next bit position to consider
     * @param depth Current recursion depth
     * @param hashes List to collect hash values
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    private static void generate(
        final Function<Key, Hash> func,
        final byte[] bytes,
        final int total,
        final int remaining,
        final int start,
        final int depth,
        final Hashes hashes
    ) {
        hashes.add(func.apply(new KeyOf(bytes)));
        if (remaining > 0) {
            for (int bit = start; bit < total; ++bit) {
                bytes[bit >> 3] |= (byte) (1 << (bit & 7));
                SparseKeyTest.generate(
                    func, bytes, total, remaining - 1,
                    bit + 1, depth + 1, hashes
                );
                bytes[bit >> 3] &= (byte) ~(1 << (bit & 7));
            }
        }
    }
}
