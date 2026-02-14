/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.key;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Flipped tests.
 * @since 0.0.1
 */
@SuppressWarnings({
    "PMD.UnitTestShouldIncludeAssert",
    "PMD.UnnecessaryLocalRule"
})
final class FlippedTest {
    @Test
    void compareTwoFlippedKeys() {
        boolean failed = false;
        for (int rep = 0; rep < 100_000; ++rep) {
            for (int bit = 0; bit < 64; ++bit) {
                final long input = ThreadLocalRandom.current().nextLong();
                final byte[] bytes = ByteBuffer
                    .allocate(Long.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putLong(input)
                    .array();
                final long changed = input ^ (1L << bit);
                final byte[] first = ByteBuffer
                    .allocate(Long.BYTES)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putLong(changed)
                    .array();
                final byte[] second = new Flipped(new KeyOf(bytes), bit)
                    .asBytes();
                if (!Arrays.equals(first, second)) {
                    failed = true;
                    break;
                }
            }
        }
        new Assertion<>(
            "must compare two flipped keys",
            !failed,
            new IsTrue()
        ).affirm();
    }
}
