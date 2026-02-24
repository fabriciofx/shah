/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.seed.Seed64;
import java.util.Random;
import java.util.function.BiFunction;

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
 * @checkstyle ParameterNumberCheck (200 lines)
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
    private static final int MIN_SIZE = 4;

    /**
     * Default number of repetitions.
     */
    private static final int DEFAULT_REPS = 10;

    /**
     * Default key's seed.
     */
    private static final Seed DEFAULT_INITIAL = new Seed64(883_741L);

    /**
     * The hash under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Maximum key length to test.
     */
    private final int max;

    /**
     * Random seed for reproducibility.
     */
    private final Seed initial;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param max Maximum key length to test
     */
    public SanityTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int max
    ) {
        this(
            func,
            seed,
            max,
            SanityTest.DEFAULT_INITIAL,
            SanityTest.DEFAULT_REPS
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param max Maximum key length to test
     * @param initial Random seed for reproducibility
     * @param repetitions Number of repetitions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SanityTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int max,
        final Seed initial,
        final int repetitions
    ) {
        this.func = func;
        this.seed = seed;
        this.max = max;
        this.initial = initial;
        this.repetitions = repetitions;
    }

    @Override
    public Double metric() {
        final Random random = new Random(this.initial.asLong());
        int checks = 0;
        int failures = 0;
        for (int rep = 0; rep < this.repetitions; ++rep) {
            for (int len = SanityTest.MIN_SIZE; len <= this.max; ++len) {
                final Key key = new Randomized(new KeyOf(len), random);
                final Hash original = this.func.apply(key, this.seed);
                for (int bit = 0; bit < key.bits(); ++bit) {
                    final Key flip = new Flipped(key, bit);
                    final Hash flipped = this.func.apply(flip, this.seed);
                    ++checks;
                    if (original.equals(flipped)) {
                        ++failures;
                    }
                    final Hash restored = this.func.apply(
                        new Flipped(flip, bit),
                        this.seed
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
