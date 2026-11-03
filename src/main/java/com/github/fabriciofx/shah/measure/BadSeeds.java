/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.measure;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Measure;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.Filled;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.metric.Ratio;
import com.github.fabriciofx.shah.scalar.AllZero;
import com.github.fabriciofx.shah.seed.Seed64;
import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * Bad seeds measure from SMHasher.
 *
 * <p>Tests a list of seed values for broken behavior. For each seed, the hash
 * function is evaluated with multiple key lengths and fill values to
 * detect:</p>
 * <ul>
 * <li>Broken seeds: zero-filled key produces a zero hash</li>
 * <li>Collision clusters: different fill values at the same key
 * length produce colliding hashes</li>
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
 */
public final class BadSeeds implements Measure<Ratio> {

    /**
     * Key lengths to measure, matching SMHasher's TestSecret.
     */
    private static final int[] SIZES = {
        1, 2, 4, 8, 12, 16, 32, 64, 128,
    };

    /**
     * Fill values to measure, matching SMHasher's TestSecret.
     */
    private static final int[] FILLS = {
        0, 32, '0', 127, 128, 255,
    };

    /**
     * The hash under measure, accepting (key, seed).
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Known bad seeds to measure.
     */
    private final Seed[] seeds;

    /**
     * Ctor with default seed 0.
     *
     * @param func The hash function under measure, accepting (key, seed)
     */
    public BadSeeds(final BiFunction<Key, Seed, Hash> func) {
        this(func, new Seed64(0L));
    }

    /**
     * Ctor.
     *
     * @param func The hash function under measure, accepting (key, seed)
     * @param seeds Known seed values to measure
     */
    public BadSeeds(
        final BiFunction<Key, Seed, Hash> func,
        final Seed... seeds
    ) {
        this.func = func;
        this.seeds = Arrays.copyOf(seeds, seeds.length);
    }

    @Override
    public Ratio metric() {
        int failures = 0;
        for (final Seed seed : this.seeds) {
            if (!this.testSeed(seed)) {
                ++failures;
            }
        }
        return new Ratio(failures, this.seeds.length);
    }

    private boolean testSeed(final Seed seed) {
        boolean passed = true;
        for (final int size : BadSeeds.SIZES) {
            if (!this.testSize(seed, size)) {
                passed = false;
                break;
            }
        }
        return passed;
    }

    private boolean testSize(final Seed seed, final int size) {
        final Hashes hashes = new HashesOf();
        boolean passed = true;
        for (final int fill : BadSeeds.FILLS) {
            final Hash hash = this.func.apply(
                new Filled(new KeyOf(size), (byte) fill),
                seed
            );
            if (fill == 0 && new AllZero(hash).value()) {
                passed = false;
            }
            hashes.add(hash);
        }
        if (passed && new Collisions(hashes).ratio().value() > 0.0) {
            passed = false;
        }
        return passed;
    }
}
