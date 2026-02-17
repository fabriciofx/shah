/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.metric.AvalancheBias;
import com.github.fabriciofx.shah.metric.AvalancheMatrix;
import java.util.function.BiFunction;

/**
 * Avalanche test from SMHasher.
 *
 * <p>Generate flip-count bins and compute the worst avalanche bias. The worst
 * bias as a value between 0.0 and 1.0.<p>
 *
 * <p>For each input bit, flips it and measures how many output bits
 * change. A good hash function should flip approximately 50% of
 * output bits when any single input bit is changed. The worst bias
 * across all (input bit, output bit) pairs is reported via
 * {@link AvalancheBias}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.). The hash bit count is derived from the byte array length
 * of the first hash output.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings({
    "PMD.TestClassWithoutTestCases",
    "PMD.UnnecessaryLocalRule"
})
public final class AvalancheTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Key size.
     */
    private final int size;

    /**
     * Random seed for reproducibility.
     */
    private final long seed;

    /**
     * Number of repetitions.
     */
    private final int repetitions;

    /**
     * Ctor.
     * @param func The hash under test
     * @param size Size in key
     * @param seed Random seed for reproducibility
     * @param repetitions Number of repetitions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public AvalancheTest(
        final BiFunction<Key, Long, Hash> func,
        final int size,
        final long seed,
        final int repetitions
    ) {
        this.func = func;
        this.size = size;
        this.seed = seed;
        this.repetitions = repetitions;
    }

    @Override
    public Double value() {
        final double[][] probs = new AvalancheMatrix(
            this.func,
            this.size,
            this.seed,
            this.repetitions
        ).value();
        return new AvalancheBias(probs).value();
    }
}
