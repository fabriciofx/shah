/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric.avalanche;

import com.github.fabriciofx.shah.func.appleby.Murmur3Hash32;
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
        final double[][] first = new Matrix(
            (key, seed) -> new Murmur3Hash32(key, Long.hashCode(seed)).hash(),
            8,
            12_345L,
            54_321L,
            100_000
        ).value();
        final double[][] second = new Matrix(
            (key, seed) -> new Murmur3Hash32(key, Long.hashCode(seed)).hash(),
            8,
            12_345L,
            54_321L,
            100_000
        ).value();
        new Assertion<>(
            "must two avalanche matrix be equals under the same parameters",
            Arrays.deepEquals(first, second),
            new IsTrue()
        ).affirm();
    }
}
