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
import com.github.fabriciofx.shah.metric.CollisionRatio;
import com.github.fabriciofx.shah.scalar.HashDiff;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

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
 * <p>The worst collision ratio across all bit flip positions is
 * returned.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.UnnecessaryLocalRule"})
public final class DifferentialTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Key size.
     */
    private final int size;

    /**
     * Number of keys to test.
     */
    private final int count;

    /**
     * Random seed for reproducibility.
     */
    private final long seed;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Key size in bytes
     * @param count Number of keys
     * @param seed Random seed for reproducibility
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public DifferentialTest(
        final Function<Key, Hash> func,
        final int size,
        final int count,
        final long seed
    ) {
        this.func = func;
        this.size = size;
        this.count = count;
        this.seed = seed;
    }

    @Override
    public Double value() {
        final int nbits = this.size * 8;
        final Random random = new Random(this.seed);
        final List<Key> keys = new ArrayList<>(this.count);
        for (int idx = 0; idx < this.count; ++idx) {
            keys.add(new Randomized(new KeyOf(this.size), random));
        }
        double worst = 0.0;
        for (int bit = 0; bit < nbits; ++bit) {
            final Hashes diffs = new HashesOf();
            for (int idx = 0; idx < this.count; ++idx) {
                final Key key = keys.get(idx);
                final Hash original = this.func.apply(key);
                final Hash flipped = this.func.apply(new Flipped(key, bit));
                diffs.add(new HashDiff(original, flipped).value());
            }
            final double ratio = new CollisionRatio(diffs).value();
            if (ratio > worst) {
                worst = ratio;
            }
        }
        return worst;
    }
}
