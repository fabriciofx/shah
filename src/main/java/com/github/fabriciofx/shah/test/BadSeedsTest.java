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
import com.github.fabriciofx.shah.key.Filled;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.scalar.AllZero;
import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * Bad seeds test from SMHasher.
 *
 * <p>Tests a list of seed values for broken behavior. For each seed, the hash
 * function is evaluated with multiple key lengths and fill values to detect:
 * </p>
 * <ul>
 *   <li>Broken seeds: zero-filled key produces a zero hash</li>
 *   <li>Collision clusters: different fill values at the same key
 *       length produce colliding hashes</li>
 * </ul>
 *
 * <p>The key lengths tested are 1, 2, 4, 8, 12, 16, 32, 64, and 128 bytes.
 * The fill values tested are 0, 32, '0' (48), 127, 128, and 255, matching
 * SMHasher's TestSecret function.</p>
 *
 * <p>Returns the proportion of seeds that fail (0.0 means all seeds pass). A
 * seed fails if any key length produces a collision among the six fill values,
 * or if a zero-filled key hashes to all-zero bytes.</p>
 *
 * <p>If no seeds are provided, seed 0 is tested by default, matching
 * SMHasher's behavior.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class BadSeedsTest implements Test<Double> {
    /**
     * Key lengths to test, matching SMHasher's TestSecret.
     */
    private static final int[] SIZES = {
        1, 2, 4, 8, 12, 16, 32, 64, 128,
    };

    /**
     * Fill values to test, matching SMHasher's TestSecret.
     */
    private static final int[] FILLS = {
        0, 32, '0', 127, 128, 255,
    };

    /**
     * The hash under test, accepting (key, seed).
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Known bad seeds to test.
     */
    private final long[] seeds;

    /**
     * Ctor with default seed 0.
     * @param func The hash function under test, accepting (key, seed)
     */
    public BadSeedsTest(final BiFunction<Key, Long, Hash> func) {
        this(func, 0L);
    }

    /**
     * Ctor.
     * @param func The hash function under test, accepting (key, seed)
     * @param seeds Known seed values to test
     */
    public BadSeedsTest(
        final BiFunction<Key, Long, Hash> func,
        final long... seeds
    ) {
        this.func = func;
        this.seeds = Arrays.copyOf(seeds, seeds.length);
    }

    @Override
    public Double metric() {
        int failures = 0;
        for (final long seed : this.seeds) {
            if (!this.testSeed(seed)) {
                ++failures;
            }
        }
        final double ratio;
        if (this.seeds.length == 0) {
            ratio = 0.0;
        } else {
            ratio = (double) failures / this.seeds.length;
        }
        return ratio;
    }

    /**
     * Test a single seed value.
     * @param seed The seed to test
     * @return True if the seed passes all checks
     */
    private boolean testSeed(final long seed) {
        boolean passed = true;
        for (final int size : BadSeedsTest.SIZES) {
            if (!this.testSize(seed, size)) {
                passed = false;
                break;
            }
        }
        return passed;
    }

    /**
     * Test a single seed with a single key size.
     * @param seed The seed to test
     * @param size The key size
     * @return True if the seed passes for this size (no zero hash for
     *  zero-filled key and no collisions)
     */
    private boolean testSize(final long seed, final int size) {
        final Hashes hashes = new HashesOf();
        boolean passed = true;
        for (final int fill : BadSeedsTest.FILLS) {
            final Hash hash = this.func.apply(
                new Filled(new KeyOf(size), (byte) fill),
                seed
            );
            if (fill == 0 && new AllZero(hash).value()) {
                passed = false;
            }
            hashes.add(hash);
        }
        if (passed && new Collisions(hashes).ratio() > 0.0) {
            passed = false;
        }
        return passed;
    }
}
