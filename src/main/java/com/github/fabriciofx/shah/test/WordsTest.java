/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import java.util.function.BiFunction;

/**
 * Words test from SMHasher.
 *
 * <p>Measures the collisions ratio of random words hashes. Returns the
 * collisions metric among all generated hashes.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class WordsTest implements Test<Collisions> {
    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key's seed.
     */
    private final long seed;

    /**
     * Words.
     */
    private final Words words;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The key's seed
     * @param words The words
     */
    public WordsTest(
        final BiFunction<Key, Long, Hash> func,
        final long seed,
        final Words words
    ) {
        this.func = func;
        this.seed = seed;
        this.words = words;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        for (final String word : this.words) {
            hashes.add(this.func.apply(new KeyOf(word), this.seed));
        }
        return new Collisions(hashes);
    }
}
