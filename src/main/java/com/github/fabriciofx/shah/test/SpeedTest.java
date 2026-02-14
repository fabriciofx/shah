/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.collection.Filtered;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.key.Randomized;
import com.github.fabriciofx.shah.stat.Mean;
import com.github.fabriciofx.shah.stat.Stdv;
import com.github.fabriciofx.shah.stat.Variance;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Speed test from SMHasher.
 *
 * <p>Measures the throughput of a hash function by timing repeated
 * hashing of random keys of a given size. The result is reported in
 * nanoseconds per hash operation.</p>
 *
 * <p>Like SMHasher's SpeedTest.cpp, the trial iteration index is
 * passed as the hash seed. For small keys (at most 255 bytes),
 * 200 inner iterations are run per trial with the seed mutated by
 * the first byte of the previous hash output, serializing the calls
 * (matching {@code timehash_small}). For large keys, each trial
 * hashes once with the trial index as seed (matching
 * {@code timehash}).</p>
 *
 * <p>Multiple trials are run, outliers beyond three standard
 * deviations from the mean are filtered out, and the mean of the
 * remaining measurements is returned.</p>
 *
 * <p>Accepts either a seedless {@code Function} or a seeded
 * {@code BiFunction}. The seedless variant ignores trial-based
 * seed variation, suitable for hash functions without a seed
 * parameter (e.g. OAAT).</p>
 *
 * <p>Returns nanoseconds per hash operation (lower is better).
 * Unlike the statistical quality tests, this is a performance
 * benchmark with no pass/fail threshold.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class SpeedTest implements Test<Double> {
    /**
     * Default number of trials.
     */
    private static final int DEFAULT_TRIALS = 999;

    /**
     * Default key size in bytes (256 KB bulk).
     */
    private static final int DEFAULT_KEY_SIZE = 256 * 1_024;

    /**
     * Maximum key size for small-key timing (matching
     * SMHasher's TIMEHASH_SMALL_LEN_MAX).
     */
    private static final int SMALL_MAX = 255;

    /**
     * Inner iterations for small-key timing (matching
     * SMHasher's NUM_TRIALS in timehash_small).
     */
    private static final int INNER = 200;

    /**
     * Standard deviation multiplier for outlier filtering.
     */
    private static final double SIGMA = 3.0;

    /**
     * The seeded hash function under test.
     */
    private final BiFunction<Key, Integer, Hash> func;

    /**
     * Number of timing trials.
     */
    private final int trials;

    /**
     * Key size in bytes.
     */
    private final int size;

    /**
     * Ctor with defaults (256 KB bulk key, 999 trials, seedless).
     * @param func The hash function under test (seedless)
     */
    public SpeedTest(final Function<Key, Hash> func) {
        this(func, SpeedTest.DEFAULT_TRIALS, SpeedTest.DEFAULT_KEY_SIZE);
    }

    /**
     * Ctor with specified key size (seedless).
     * @param func The hash function under test (seedless)
     * @param size Key size in bytes
     */
    public SpeedTest(
        final Function<Key, Hash> func,
        final int size
    ) {
        this(func, SpeedTest.DEFAULT_TRIALS, size);
    }

    /**
     * Ctor (seedless).
     * @param func The hash function under test (seedless)
     * @param trials Number of timing trials
     * @param size Key size in bytes
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SpeedTest(
        final Function<Key, Hash> func,
        final int trials,
        final int size
    ) {
        this((key, seed) -> func.apply(key), trials, size);
    }

    /**
     * Ctor with defaults (256 KB bulk key, 999 trials, seeded).
     * @param func The hash function under test, accepting (key, seed)
     */
    public SpeedTest(final BiFunction<Key, Integer, Hash> func) {
        this(func, SpeedTest.DEFAULT_TRIALS, SpeedTest.DEFAULT_KEY_SIZE);
    }

    /**
     * Ctor with specified key size (seeded).
     * @param func The hash function under test, accepting (key, seed)
     * @param size Key size in bytes
     */
    public SpeedTest(
        final BiFunction<Key, Integer, Hash> func,
        final int size
    ) {
        this(func, SpeedTest.DEFAULT_TRIALS, size);
    }

    /**
     * Ctor (seeded).
     * @param func The hash function under test, accepting (key, seed)
     * @param trials Number of timing trials
     * @param size Key size in bytes
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SpeedTest(
        final BiFunction<Key, Integer, Hash> func,
        final int trials,
        final int size
    ) {
        this.func = func;
        this.trials = trials;
        this.size = size;
    }

    @Override
    public Double value() {
        final Key key = new Randomized(new KeyOf(this.size));
        this.func.apply(key, 0);
        final List<Long> times = new ArrayList<>(this.trials);
        for (int idx = 0; idx < this.trials; ++idx) {
            final long elapsed = this.measure(key, idx);
            if (elapsed > 0) {
                times.add(elapsed);
            }
        }
        final double mean = new Mean(times).value();
        final double stdv = new Stdv(new Variance(times, mean)).value();
        final double cutoff = mean + stdv * SpeedTest.SIGMA;
        return new Mean(new Filtered<>(times, time -> time > cutoff)).value();
    }

    /**
     * Measure a single trial. For small keys, runs 200 inner
     * iterations with seed serialization (matching SMHasher's
     * timehash_small). For large keys, times a single hash call
     * (matching timehash).
     * @param key The key to hash
     * @param trial The trial index (used as initial seed)
     * @return Elapsed nanoseconds (per hash for small keys)
     * @checkstyle NestedIfDepthCheck (20 lines)
     */
    private long measure(final Key key, final int trial) {
        final long elapsed;
        if (this.size <= SpeedTest.SMALL_MAX) {
            int seed = trial;
            final long start = System.nanoTime();
            for (int inner = 0; inner < SpeedTest.INNER; ++inner) {
                final Hash hash = this.func.apply(key, seed);
                seed += hash.asBytes()[0] & 0xFF;
            }
            elapsed = (System.nanoTime() - start) / SpeedTest.INNER;
        } else {
            final long start = System.nanoTime();
            this.func.apply(key, trial);
            elapsed = System.nanoTime() - start;
        }
        return elapsed;
    }
}
