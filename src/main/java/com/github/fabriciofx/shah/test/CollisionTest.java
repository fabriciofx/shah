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
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.Collisions;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Collision test from SMHasher.
 *
 * <p>Generate shah and compute the collision ratio (actual / expected).</p>
 *
 * <p>Generates random keys, shah them, and delegates to {@link Collisions}
 * to compute the collision ratio against the birthday-paradox expectation.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class CollisionTest implements Test<Collisions> {
    /**
     * The hash under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key size.
     */
    private final int size;

    /**
     * Key seed.
     */
    private final long seed;

    /**
     * Test seed.
     */
    private final long initial;

    /**
     * Number of keys to hash.
     */
    private final int count;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Key size
     * @param seed Key seed
     * @param initial Test seed
     * @param count Number of keys to hash
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public CollisionTest(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final long initial,
        final int count
    ) {
        this.func = func;
        this.size = size;
        this.seed = seed;
        this.initial = initial;
        this.count = count;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        final Random random = new Random(this.initial);
        for (int idx = 0; idx < this.count; ++idx) {
            final Key key = new Randomized(new KeyOf(this.size), random);
            hashes.add(this.func.apply(key, this.seed));
        }
        return new Collisions(hashes);
    }
}
