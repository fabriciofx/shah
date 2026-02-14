/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import java.util.Random;
import java.util.function.Function;

/**
 * Sanity test from SMHasher.
 *
 * <p>Basic sanity checks for a hash function.</p>
 *
 * <p>For each repetition, generates a random key and verifies two
 * fundamental properties:</p>
 * <ol>
 *   <li><b>Bit sensitivity</b>: flipping any single input bit must
 *       produce a different hash value.</li>
 *   <li><b>Determinism</b>: flipping the bit back must restore the
 *       original hash value.</li>
 * </ol>
 *
 * <p>Returns the proportion of failed checks (0.0 means all checks
 * passed). A good hash function should always return 0.0.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings({
    "PMD.TestClassWithoutTestCases",
    "PMD.UnnecessaryLocalRule",
    "PMD.CognitiveComplexity"
})
public final class SanityTest implements Test<Double> {
    /**
     * Minimum key length in bytes.
     */
    private static final int MIN_LEN = 4;

    /**
     * Default number of repetitions.
     */
    private static final int DEFAULT_REPS = 10;

    /**
     * Default random seed.
     */
    private static final long DEFAULT_SEED = 883_741L;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Maximum key length to test.
     */
    private final int max;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Random seed for reproducibility.
     */
    private final long seed;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param max Maximum key length to test
     */
    public SanityTest(final Function<Key, Hash> func, final int max) {
        this(func, max, SanityTest.DEFAULT_REPS, SanityTest.DEFAULT_SEED);
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param max Maximum key length to test
     * @param repetitions Number of repetitions
     * @param seed Random seed for reproducibility
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SanityTest(
        final Function<Key, Hash> func,
        final int max,
        final int repetitions,
        final long seed
    ) {
        this.func = func;
        this.max = max;
        this.repetitions = repetitions;
        this.seed = seed;
    }

    @Override
    public Double value() {
        final Random random = new Random(this.seed);
        int checks = 0;
        int failures = 0;
        for (int rep = 0; rep < this.repetitions; ++rep) {
            for (int len = SanityTest.MIN_LEN; len <= this.max; ++len) {
                final Key key = new Randomized(new KeyOf(len), random);
                final Hash original = this.func.apply(key);
                for (int bit = 0; bit < key.bits(); ++bit) {
                    final Key flip = new Flipped(key, bit);
                    final Hash flipped = this.func.apply(flip);
                    ++checks;
                    if (original.equals(flipped)) {
                        ++failures;
                    }
                    final Hash restored = this.func.apply(
                        new Flipped(flip, bit)
                    );
                    ++checks;
                    if (!original.equals(restored)) {
                        ++failures;
                    }
                }
            }
        }
        final double ratio;
        if (checks == 0) {
            ratio = 0.0;
        } else {
            ratio = (double) failures / checks;
        }
        return ratio;
    }
}
