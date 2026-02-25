/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.DistributionScore;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Distribution test from SMHasher.
 *
 * <p>Generate hash and compute the worst distribution score, where 0.0 is
 * perfect.</p>
 *
 * <p>Generates random keys, shah them, and delegates to
 * {@link DistributionScore} to check if the hash output is
 * uniformly distributed across all N-bit window positions.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class DistributionTest implements Test<DistributionScore> {
    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Seed for hash function.
     */
    private final Seed seed;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Key's seed.
     */
    private final Seed initial;

    /**
     * Number of keys to hash.
     */
    private final int count;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param size Key's size
     * @param initial Key's seed
     * @param count Number of keys to hash
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DistributionTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int size,
        final Seed initial,
        final int count
    ) {
        this.func = func;
        this.seed = seed;
        this.size = size;
        this.initial = initial;
        this.count = count;
    }

    @Override
    public DistributionScore metric() {
        final Hashes hashes = new HashesOf();
        final Random random = this.initial.random();
        for (int idx = 0; idx < this.count; ++idx) {
            final Key key = new Randomized(new KeyOf(this.size), random);
            hashes.add(this.func.apply(key, this.seed));
        }
        return new DistributionScore(hashes);
    }
}
