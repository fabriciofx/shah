/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.Appended;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import java.util.Random;
import java.util.function.BiFunction;

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
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class AppendedZeroesTest implements Test<Double> {
    /**
     * Minimum key size.
     */
    private static final int MIN_SIZE = 32;

    /**
     * Number of zero bytes to append (0..MAX_ZEROES-1).
     */
    private static final int MAX_ZEROES = 32;

    /**
     * Default number of repetitions.
     */
    private static final int DEFAULT_REPS = 100;

    /**
     * Default key seed.
     */
    private static final long DEFAULT_SEED = 173_994L;

    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Key's seed.
     */
    private final long seed;

    /**
     * Max zeroes.
     */
    private final int max;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func The hash function under test
     */
    public AppendedZeroesTest(final BiFunction<Key, Long, Hash> func) {
        this(
            func,
            AppendedZeroesTest.MIN_SIZE,
            AppendedZeroesTest.DEFAULT_SEED,
            AppendedZeroesTest.MAX_ZEROES,
            AppendedZeroesTest.DEFAULT_REPS
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size The key's size
     * @param seed The key's seed
     * @param max The max number of zeroes
     * @param repetitions Number of repetitions
     */
    public AppendedZeroesTest(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final int max,
        final int repetitions
    ) {
        this.func = func;
        this.size = size;
        this.seed = seed;
        this.max = max;
        this.repetitions = repetitions;
    }

    @Override
    public Double metric() {
        final Random random = new Random(this.seed);
        int checks = 0;
        int failures = 0;
        for (int rep = 0; rep < this.repetitions; ++rep) {
            final Key key = new Randomized(new KeyOf(this.size), random);
            Hash previous = this.func.apply(new KeyOf(), this.seed);
            for (int zeroes = 0; zeroes < this.max; ++zeroes) {
                final Key expanded = new Appended(key, new byte[zeroes]);
                final Hash current = this.func.apply(expanded, this.seed);
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
