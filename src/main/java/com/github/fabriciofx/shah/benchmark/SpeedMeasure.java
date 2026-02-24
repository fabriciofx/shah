/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.benchmark;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.seed.Seed64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

/**
 * SpeedMeasure.
 *
 * <p>Measure a single trial. For small keys, runs 200 inner iterations with
 * seed serialization (matching SMHasher's timehash_small). For large keys,
 * times a single hash call (matching timehash).</p>
 *
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
public final class SpeedMeasure implements Scalar<Long> {
    /**
     * Maximum key size for small-key timing.
     */
    private static final int DEFAULT_MINIMUM = 255;

    /**
     * Inner iterations for small-key timing.
     */
    private static final int INNER = 200;

    /**
     * Hash function under measurement.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Key.
     */
    private final Key key;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Minimum key size.
     */
    private final int minimum;

    /**
     * Number of interactions for small keys.
     */
    private final int inner;

    /**
     * Ctor.
     * @param func Hash function under measurement
     * @param key The key
     * @param size The key's size
     */
    public SpeedMeasure(
        final BiFunction<Key, Seed, Hash> func,
        final Key key,
        final int size
    ) {
        this(func, key, size, SpeedMeasure.DEFAULT_MINIMUM);
    }

    /**
     * Ctor.
     * @param func Hash function under measurement
     * @param key The key
     * @param size The key's size
     * @param minimum Minimum key size
     */
    public SpeedMeasure(
        final BiFunction<Key, Seed, Hash> func,
        final Key key,
        final int size,
        final int minimum
    ) {
        this(func, key, size, minimum, SpeedMeasure.INNER);
    }

    /**
     * Ctor.
     * @param func Hash function under measurement
     * @param key The key
     * @param size The key's size
     * @param minimum Minimum key size
     * @param inner Number of interactions for small keys
     */
    public SpeedMeasure(
        final BiFunction<Key, Seed, Hash> func,
        final Key key,
        final int size,
        final int minimum,
        final int inner
    ) {
        this.func = func;
        this.key = key;
        this.size = size;
        this.minimum = minimum;
        this.inner = inner;
    }

    @Override
    public Long value() {
        final long elapsed;
        long value = ThreadLocalRandom.current().nextLong();
        if (this.size <= this.minimum) {
            final long start = System.nanoTime();
            for (int idx = 0; idx < this.inner; ++idx) {
                final Hash hash = this.func.apply(this.key, new Seed64(value));
                value += hash.byteAt(0);
            }
            elapsed = (System.nanoTime() - start) / this.inner;
        } else {
            final long start = System.nanoTime();
            this.func.apply(this.key, new Seed64(value));
            elapsed = System.nanoTime() - start;
        }
        return elapsed;
    }
}
