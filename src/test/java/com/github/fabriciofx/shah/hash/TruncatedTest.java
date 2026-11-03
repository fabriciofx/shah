/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hash;

import java.util.concurrent.ThreadLocalRandom;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link Truncated}.
 *
 * @since 0.0.1
 */
final class TruncatedTest {

    @Test
    void keepsOnlyTheLowBits() {
        final long low = ThreadLocalRandom.current().nextLong();
        new Assertion<>(
            "must keep only the low 32 bits of a 128-bit hash",
            () -> new Truncated(
                new Hash128(low, ThreadLocalRandom.current().nextLong()),
                32
            ).asString(),
            new IsText(new Hash32((int) low).asString())
        ).affirm();
    }

    @Test
    void collidesWhenOnlyTheHighBitsDiffer() {
        final long low = ThreadLocalRandom.current().nextLong();
        final long high = ThreadLocalRandom.current().nextLong();
        new Assertion<>(
            "must consider equal the hashes that differ only in high bits",
            new Truncated(new Hash128(low, high), 64),
            new IsEqual<>(new Truncated(new Hash128(low, ~high), 64))
        ).affirm();
    }

    @Test
    void diffsOnlyTheLowBits() {
        final long first = ThreadLocalRandom.current().nextLong();
        final long second = ThreadLocalRandom.current().nextLong();
        new Assertion<>(
            "must compute the difference of the low 64 bits only",
            () -> new Truncated(
                new Hash128(first, ThreadLocalRandom.current().nextLong()),
                64
            ).diff(
                new Truncated(
                    new Hash128(second, ThreadLocalRandom.current().nextLong()),
                    64
                )
            ).asString(),
            new IsText(new Hash64(first ^ second).asString())
        ).affirm();
    }
}
