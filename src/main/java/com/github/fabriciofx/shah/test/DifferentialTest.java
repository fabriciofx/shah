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
import com.github.fabriciofx.shah.key.Flipped;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.metric.Ratios;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Differential test from SMHasher.
 *
 * <p>Compute the worst differential collision ratio.</p>
 *
 * <p>For a set of random keys, flips each individual bit and checks
 * whether the resulting hash collision rate exceeds the birthday
 * paradox expectation. This tests whether single-bit changes in the
 * input produce sufficiently different hash values.</p>
 *
 * <p>The worst collision ratio across all bit flip positions is returned.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class DifferentialTest implements Test<Ratios> {
    /**
     * The hash under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Hash function seed.
     */
    private final long seed;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Key's seed.
     */
    private final long initial;

    /**
     * Number of keys to test.
     */
    private final int count;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed Hash function seed
     * @param size Key's size in bytes
     * @param initial Key's seed
     * @param count Number of keys
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DifferentialTest(
        final BiFunction<Key, Long, Hash> func,
        final long seed,
        final int size,
        final long initial,
        final int count
    ) {
        this.func = func;
        this.seed = seed;
        this.size = size;
        this.initial = initial;
        this.count = count;
    }

    @Override
    public Ratios metric() {
        final Random random = new Random(this.initial);
        final Key probe = new Randomized(new KeyOf(this.size), random);
        final Ratios ratios = new Ratios();
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
            ratios.add(diffs);
        }
        return ratios;
    }
}
