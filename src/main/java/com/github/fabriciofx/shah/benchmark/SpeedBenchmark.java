/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.benchmark;

import com.github.fabriciofx.shah.Benchmark;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
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
 * Speed benchmark from SMHasher.
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
 * This is a performance benchmark with no pass/fail threshold.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 */
public final class SpeedBenchmark implements Benchmark<Double> {
    /**
     * Default number of trials.
     */
    private static final int DEFAULT_TRIALS = 999;

    /**
     * Default key size in bytes (256 KB bulk).
     */
    private static final int DEFAULT_KEY_SIZE = 256 * 1_024;

    /**
     * Standard deviation multiplier for outlier filtering.
     */
    private static final double SIGMA = 3.0;

    /**
     * The seeded hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key size in bytes.
     */
    private final int size;

    /**
     * Number of timing trials.
     */
    private final int trials;

    /**
     * Ctor with defaults (256 KB bulk key, 999 trials, seedless).
     * @param func The hash function under test (seedless)
     */
    public SpeedBenchmark(final Function<Key, Hash> func) {
        this(
            func,
            SpeedBenchmark.DEFAULT_KEY_SIZE,
            SpeedBenchmark.DEFAULT_TRIALS
        );
    }

    /**
     * Ctor with specified key size (seedless).
     * @param func The hash function under test (seedless)
     * @param size Key size in bytes
     */
    public SpeedBenchmark(
        final Function<Key, Hash> func,
        final int size
    ) {
        this(func, size, SpeedBenchmark.DEFAULT_TRIALS);
    }

    /**
     * Ctor (seedless).
     * @param func The hash function under test (seedless)
     * @param size Key size in bytes
     * @param trials Number of timing trials
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SpeedBenchmark(
        final Function<Key, Hash> func,
        final int size,
        final int trials
    ) {
        this((key, seed) -> func.apply(key), trials, size);
    }

    /**
     * Ctor with defaults (256 KB bulk key, 999 trials, seeded).
     * @param func The hash function under test, accepting (key, seed)
     */
    public SpeedBenchmark(final BiFunction<Key, Long, Hash> func) {
        this(
            func,
            SpeedBenchmark.DEFAULT_KEY_SIZE,
            SpeedBenchmark.DEFAULT_TRIALS
        );
    }

    /**
     * Ctor with specified key size (seeded).
     * @param func The hash function under test, accepting (key, seed)
     * @param size Key size in bytes
     */
    public SpeedBenchmark(
        final BiFunction<Key, Long, Hash> func,
        final int size
    ) {
        this(func, size, SpeedBenchmark.DEFAULT_TRIALS);
    }

    /**
     * Ctor (seeded).
     * @param func The hash function under test, accepting (key, seed)
     * @param size Key size in bytes
     * @param trials Number of timing trials
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public SpeedBenchmark(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final int trials
    ) {
        this.func = func;
        this.trials = trials;
        this.size = size;
    }

    @Override
    public Double run() {
        final Key key = new Randomized(new KeyOf(this.size));
        this.func.apply(key, 0L);
        final List<Long> times = new ArrayList<>(this.trials);
        for (int idx = 0; idx < this.trials; ++idx) {
            final long elapsed = new SpeedMeasure(
                this.func,
                key,
                this.size
            ).value();
            if (elapsed > 0) {
                times.add(elapsed);
            }
        }
        final double mean = new Mean(times).value();
        final double stdv = new Stdv(new Variance(times, mean)).value();
        final double cutoff = mean + stdv * SpeedBenchmark.SIGMA;
        return new Mean(
            new Filtered<>(times, time -> time > cutoff)
        ).value();
    }
}
