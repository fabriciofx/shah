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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * HashBit test.
 * @since 0.0.1
 * @checkstyle UnnecessaryParenthesesCheck (100 lines)
 */
@SuppressWarnings({
    "PMD.UnitTestShouldIncludeAssert",
    "PMD.UnnecessaryLocalRule"
})
final class HashBitTest {
    @Test
    void compareTwoHashesBitToBit() {
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
            final long input = ThreadLocalRandom.current().nextLong();
            final byte[] bytes = ByteBuffer
                .allocate(Long.BYTES)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putLong(input)
                .array();
            final long first = murmur.apply(input);
            final Hash second = new MurmurLike(new KeyOf(bytes)).hash();
            for (int idx = 0; idx < 64; ++idx) {
                final long one = (first >>> idx) & 1L;
                final int two = new HashBit(second, idx).value();
                if (one != Integer.toUnsignedLong(two)) {
                    failed = true;
                    break;
                }
            }
            if (failed) {
                break;
            }
        }
        new Assertion<>(
            "must compare two hashes bit to bit",
            !failed,
            new IsTrue()
        ).affirm();
    }
}
