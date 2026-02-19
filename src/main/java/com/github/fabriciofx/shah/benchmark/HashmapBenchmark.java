/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.benchmark;

import com.github.fabriciofx.shah.Benchmark;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.collection.Filtered;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.stat.Mean;
import com.github.fabriciofx.shah.stat.Stdv;
import com.github.fabriciofx.shah.stat.Variance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Hash map benchmark from SMHasher.
 *
 * <p>Measures the performance of a hash function when used as the
 * backing hash for a {@link java.util.HashMap}. Random words are
 * inserted into the map, then lookup performance is measured over
 * multiple trials.</p>
 *
 * <p>SMHasher's original test inserts words into
 * {@code std::unordered_map} using the hash function, then
 * measures lookup cycles/operation. This Java adaptation uses
 * {@code java.util.HashMap} with a wrapper key that delegates
 * {@code hashCode()} to the hash function under test.</p>
 *
 * <p>Outlier filtering (3 sigma) is applied to the timing trials,
 * matching SMHasher's approach.</p>
 *
 * <p>Returns nanoseconds per lookup operation (lower is better).
 * This is a performance benchmark with no pass/fail threshold.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (300 lines)
 * @checkstyle ParameterNumberCheck (300 lines)
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class HashmapBenchmark implements Benchmark<Double> {
    /**
     * Default number of timing trials.
     */
    private static final int DEFAULT_TRIALS = 99;

    /**
     * Standard deviation multiplier for outlier filtering.
     */
    private static final double SIGMA = 3.0;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Words to insert and lookup.
     */
    private final Words words;

    /**
     * Number of timing trials for lookups.
     */
    private final int trials;

    /**
     * Ctor with defaults.
     * @param func The hash function under test
     */
    public HashmapBenchmark(final Function<Key, Hash> func) {
        this(func, new Words(), HashmapBenchmark.DEFAULT_TRIALS);
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param words Words to use for the benchmark
     * @param trials Number of timing trials
     */
    public HashmapBenchmark(
        final Function<Key, Hash> func,
        final Words words,
        final int trials
    ) {
        this.func = func;
        this.words = words;
        this.trials = trials;
    }

    @Override
    public Double run() {
        final Map<HashedKey, Boolean> map = new HashMap<>(this.words.size());
        for (final String word : this.words) {
            final int code = this.func.apply(new KeyOf(word)).hashCode();
            map.put(new HashedKey(code, word), true);
        }
        final List<Double> times = new ArrayList<>(this.trials);
        for (int trial = 0; trial < this.trials; ++trial) {
            boolean found = false;
            final long start = System.nanoTime();
            for (final String word : this.words) {
                final int code = this.func.apply(new KeyOf(word)).hashCode();
                final Boolean value = map.getOrDefault(
                    new HashedKey(code, word),
                    false
                );
                if (value) {
                    found = true;
                }
            }
            final long elapsed = System.nanoTime() - start;
            if (found && elapsed > 0) {
                times.add((double) elapsed / this.words.size());
            }
        }
        final double mean = new Mean(times).value();
        final double stdv = new Stdv(new Variance(times, mean)).value();
        final double cutoff = mean + stdv * HashmapBenchmark.SIGMA;
        return new Mean(
            new Filtered<>(times, time -> time > cutoff)
        ).value();
    }
}
