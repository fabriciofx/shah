/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.KeyOf;
import java.util.Random;
import java.util.function.Function;

/**
 * Appended zeroes test from SMHasher.
 *
 * <p>Appending zero bytes to a key should always cause it to produce
 * a different hash value.</p>
 *
 * <p>For each repetition, generates a random 32-byte key and then
 * hashes it with 0, 1, 2, ... up to 31 appended zero bytes. Each
 * successive hash must differ from the previous one.</p>
 *
 * <p>Returns the proportion of failed comparisons (0.0 means all
 * checks passed). A good hash function should always return 0.0.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class AppendedZeroesTest implements Test<Double> {
    /**
     * Base key length in bytes.
     */
    private static final int BASE_LEN = 32;

    /**
     * Number of zero bytes to append (0..MAX_ZEROES-1).
     */
    private static final int MAX_ZEROES = 32;

    /**
     * Default number of repetitions.
     */
    private static final int DEFAULT_REPS = 100;

    /**
     * Default random seed.
     */
    private static final long DEFAULT_SEED = 173_994L;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

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
     */
    public AppendedZeroesTest(final Function<Key, Hash> func) {
        this(
            func,
            AppendedZeroesTest.DEFAULT_REPS,
            AppendedZeroesTest.DEFAULT_SEED
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param repetitions Number of repetitions
     * @param seed Random seed for reproducibility
     */
    public AppendedZeroesTest(
        final Function<Key, Hash> func,
        final int repetitions,
        final long seed
    ) {
        this.func = func;
        this.repetitions = repetitions;
        this.seed = seed;
    }

    @Override
    public Double metric() {
        final Random random = new Random(this.seed);
        int checks = 0;
        int failures = 0;
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final byte[] base = new byte[AppendedZeroesTest.BASE_LEN];
            random.nextBytes(base);
            Hash previous = this.func.apply(
                new KeyOf(new byte[0])
            );
            for (int zeroes = 0;
                zeroes < AppendedZeroesTest.MAX_ZEROES; ++zeroes) {
                final byte[] extended =
                    new byte[AppendedZeroesTest.BASE_LEN + zeroes];
                System.arraycopy(
                    base, 0, extended, 0,
                    AppendedZeroesTest.BASE_LEN
                );
                final Hash current = this.func.apply(new KeyOf(extended));
                ++checks;
                if (current.equals(previous)) {
                    ++failures;
                }
                previous = current;
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
