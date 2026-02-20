/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import java.util.function.BiFunction;

/**
 * Seed test from SMHasher.
 *
 * <p>Compute the collision ratio across different seeds.</p>
 *
 * <p>Hashes the same fixed key with many different seed values and
 * checks for collisions among the resulting hashes. A good hash
 * function should produce different outputs when only the seed
 * changes.</p>
 *
 * <p>Matches SMHasher's approach which uses the fixed text
 * "The quick brown fox jumps over the lazy dog" as the key,
 * iterating seeds from 0 to {@code count - 1}.</p>
 *
 * <p>This test uses a BiFunction that accepts both the key and
 * the seed, since it needs to vary the seed independently of the
 * key.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class SeedTest implements Test<Collisions> {
    /**
     * Default key text from SMHasher.
     */
    private static final String DEFAULT_TEXT =
        "The quick brown fox jumps over the lazy dog";

    /**
     * The hash under test, accepting (key, seed).
     */
    private final BiFunction<Key, Integer, Hash> func;

    /**
     * Number of seeds to test.
     */
    private final int count;

    /**
     * The fixed key to hash.
     */
    private final Key key;

    /**
     * Ctor with SMHasher default text.
     * @param func The hash function under test, accepting (key, seed)
     * @param count Number of seeds to test
     */
    public SeedTest(
        final BiFunction<Key, Integer, Hash> func,
        final int count
    ) {
        this(func, count, new KeyOf(SeedTest.DEFAULT_TEXT));
    }

    /**
     * Ctor.
     * @param func The hash function under test, accepting (key, seed)
     * @param count Number of seeds to test
     * @param key The fixed key to hash with varying seeds
     */
    public SeedTest(
        final BiFunction<Key, Integer, Hash> func,
        final int count,
        final Key key
    ) {
        this.func = func;
        this.count = count;
        this.key = key;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        for (int seed = 0; seed < this.count; ++seed) {
            hashes.add(this.func.apply(this.key, seed));
        }
        return new Collisions(hashes);
    }
}
