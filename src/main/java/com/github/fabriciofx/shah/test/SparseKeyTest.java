/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.scalar.LongAsBytes;
import java.util.function.BiFunction;

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
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings({
    "PMD.TestClassWithoutTestCases",
    "PMD.UnnecessaryLocalRule"
})
public final class SparseKeyTest implements Test<Collisions> {
    /**
     * The hash under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Total key length in bits.
     */
    private final int total;

    /**
     * Maximum number of bits set to 1.
     */
    private final int max;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param total Total key length in bits
     * @param max Maximum number of bits set to 1
     */
    public SparseKeyTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int total,
        final int max
    ) {
        this.func = func;
        this.seed = seed;
        this.total = total;
        this.max = max;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        final long mask;
        if (this.total == 64) {
            mask = -1L;
        } else {
            mask = (1L << this.total) - 1;
        }
        for (int bits = 0; bits <= this.max; ++bits) {
            long combination = (1L << bits) - 1;
            while ((combination & ~mask) == 0) {
                final Key key = new KeyOf(new LongAsBytes(combination).value());
                hashes.add(this.func.apply(key, this.seed));
                final long smallest = combination & -combination;
                final long ripple = combination + smallest;
                if (ripple == 0) {
                    break;
                }
                long ones = combination ^ ripple;
                ones = (ones >>> 2) / smallest;
                combination = ripple | ones;
            }
        }
        return new Collisions(hashes);
    }
}
