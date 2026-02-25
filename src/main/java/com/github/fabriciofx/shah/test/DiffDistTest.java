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
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.DistributionScore;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Differential distribution test from SMHasher.
 *
 * <p>Compute the worst distribution score of hash differences.</p>
 *
 * <p>Similar to the differential test, but instead of checking
 * collisions in the XOR of original and flipped shah, this test
 * checks the distribution quality of those XOR values using the
 * same bucket-based scoring as {@link DistributionScore}.</p>
 *
 * <p>For each bit flip position, the distribution score of the
 * hash differences is computed. The worst score across all positions
 * is returned.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class DiffDistTest implements Test<Double> {
    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
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
     * Number of keys to test.
     */
    private final int count;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param size Key's size in byte
     * @param initial Key's seed
     * @param count Number of keys
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DiffDistTest(
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
    public Double metric() {
        final Random random = this.initial.random();
        final Key probe = new Randomized(new KeyOf(this.size), random);
        double worst = 0.0;
        for (int bit = 0; bit < probe.bits(); ++bit) {
            final Hashes diffs = new HashesOf();
            for (int idx = 0; idx < this.count; ++idx) {
                final Key key = new Randomized(new KeyOf(this.size), random);
                final Hash original = this.func.apply(key, this.seed);
                final Hash flipped = this.func.apply(
                    new Flipped(key, bit),
                    this.seed
                );
                diffs.add(original.diff(flipped));
            }
            final double score = new DistributionScore(diffs).value();
            if (score > worst) {
                worst = score;
            }
        }
        return worst;
    }
}
