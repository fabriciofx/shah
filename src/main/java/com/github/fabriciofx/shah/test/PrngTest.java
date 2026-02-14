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
import com.github.fabriciofx.shah.metric.CollisionRatio;
import java.util.function.Function;

/**
 * PRNG test from SMHasher.
 *
 * <p>Tests whether a hash function can serve as a passable PRNG by
 * using each hash output as the input for the next hash. Starting
 * from an all-zero key of the same width as the hash output, each
 * successive hash is computed from the previous hash value.</p>
 *
 * <p>A good hash function should produce a pseudo-random sequence
 * with no collisions over a reasonable number of iterations.</p>
 *
 * <p>Returns the collision ratio among all generated hashes.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class PrngTest implements Test<Double> {
    /**
     * Default count.
     */
    private static final int DEFAULT_COUNT = 100_000;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Number of PRNG iterations.
     */
    private final int count;

    /**
     * Ctor with default count.
     * @param func The hash function under test
     */
    public PrngTest(final Function<Key, Hash> func) {
        this(func, PrngTest.DEFAULT_COUNT);
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param count Number of PRNG iterations
     */
    public PrngTest(final Function<Key, Hash> func, final int count) {
        this.func = func;
        this.count = count;
    }

    @Override
    public Double value() {
        final Hash first = this.func.apply(new KeyOf(new byte[0]));
        final int hashbytes = first.bits() / 8;
        byte[] input = new byte[hashbytes];
        final Hashes hashes = new HashesOf();
        for (int idx = 0; idx < this.count; ++idx) {
            final Hash result = this.func.apply(new KeyOf(input));
            hashes.add(result);
            input = result.asBytes().clone();
        }
        return new CollisionRatio(hashes).value();
    }
}
