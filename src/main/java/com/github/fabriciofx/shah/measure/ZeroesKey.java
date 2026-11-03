/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.measure;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Measure;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.seed.Seed32;
import java.util.function.BiFunction;

/**
 * Zeroes key measure from SMHasher.
 *
 * <p>Compute the collision ratio for all-zero keys.</p>
 *
 * <p>Hashes keys consisting entirely of zero bytes, with sizes
 * ranging from 0 to max. This tests whether the hash function
 * can distinguish keys that differ only in size and contain no
 * non-zero content.</p>
 *
 * <p>SMHasher's default is {@code 200 * 1024 = 204_800} keys.</p>
 *
 * <p>Unlike SanityTest, this focuses on collision detection among
 * all-zero keys and computes the collision ratio against the birthday
 * paradox expectation.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits, etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class ZeroesKey implements Measure<Collisions> {

    /**
     * The hash under measure.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Maximum key size to measure.
     */
    private final int max;

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     * @param max Maximum key size to measure
     */
    public ZeroesKey(final BiFunction<Key, Seed, Hash> func, final int max) {
        this(func, new Seed32(0), max);
    }

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     * @param seed The hash function seed
     * @param max Maximum key size to measure
     */
    public ZeroesKey(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final int max
    ) {
        this.func = func;
        this.seed = seed;
        this.max = max;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        for (int size = 0; size <= this.max; ++size) {
            hashes.add(this.func.apply(new KeyOf(size), this.seed));
        }
        return new Collisions(hashes);
    }
}
