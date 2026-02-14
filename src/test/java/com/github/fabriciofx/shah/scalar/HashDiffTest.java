/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.MurmurLike;
import com.github.fabriciofx.shah.key.KeyOf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * HashDiff tests.
 * @since 0.0.1
 * @checkstyle UnnecessaryParenthesesCheck (100 lines)
 */
@SuppressWarnings({
    "PMD.UnitTestShouldIncludeAssert",
    "PMD.UnnecessaryLocalRule"
})
final class HashDiffTest {
    @Test
    void compareTwoHashDiff() {
        final Function<Long, Long> murmur = key -> {
            long hash = key;
            hash ^= (hash >>> 33);
            hash *= 0xff51afd7ed558ccdL;
            hash ^= (hash >>> 33);
            hash *= 0xc4ceb9fe1a85ec53L;
            hash ^= (hash >>> 33);
            return hash;
        };
        boolean failed = false;
        for (int rep = 0; rep < 100_000; ++rep) {
            final long first = ThreadLocalRandom.current().nextLong();
            final long left = murmur.apply(first);
            final long second = ThreadLocalRandom.current().nextLong();
            final long right = murmur.apply(second);
            final long one = left ^ right;
            final byte[] hot = ByteBuffer
                .allocate(Long.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(first)
                .array();
            final byte[] cold = ByteBuffer
                .allocate(Long.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(second)
                .array();
            final Hash two = new HashDiff(
                new MurmurLike(new KeyOf(hot)).hash(),
                new MurmurLike(new KeyOf(cold)).hash()
            ).value();
            final byte[] low = ByteBuffer
                .allocate(Long.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(one)
                .array();
            final byte[] high = two.asBytes();
            if (!Arrays.equals(low, high)) {
                failed = true;
                break;
            }
        }
        new Assertion<>(
            "must compare two diff hashes",
            !failed,
            new IsTrue()
        ).affirm();
    }
}
