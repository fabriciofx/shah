/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.IsGreaterThanOrEqualTo;
import com.github.fabriciofx.shah.func.appleby.Murmur3Hash32;
import com.github.fabriciofx.shah.seed.Seed32;
import com.github.fabriciofx.shah.test.AvalancheTest;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Matrix tests.
 * @since 0.0.1
 */
@SuppressWarnings({
    "PMD.UnitTestShouldIncludeAssert",
    "PMD.UnnecessaryLocalRule"
})
final class MatrixTest {
    @Test
    void twoMatricesBeEqualsUnderTheSameParameters() {
        final Matrix first = new AvalancheTest(
            (key, seed) -> new Murmur3Hash32(key, seed).hash(),
            new Seed32(12_345),
            8,
            new Seed32(54_321),
            100_000
        ).metric();
        final Matrix second = new AvalancheTest(
            (key, seed) -> new Murmur3Hash32(key, seed).hash(),
            new Seed32(12_345),
            8,
            new Seed32(54_321),
            100_000
        ).metric();
        new Assertion<>(
            "must two avalanche matrix be equals under the same parameters",
            Arrays.deepEquals(first.value(), second.value()),
            new IsTrue()
        ).affirm();
    }

    @Test
    void computeProbabilityOfFiftyPercent() {
        final Matrix matrix = new AvalancheTest(
            (key, seed) -> new Murmur3Hash32(key, seed).hash(),
            new Seed32(12_345),
            8,
            new Seed32(54_321),
            100_000
        ).metric();
        new Assertion<>(
            "must compute a probability of 50%",
            Math.ceil(matrix.probability()),
            new IsGreaterThanOrEqualTo(0.50, "probability")
        ).affirm();
    }
}
