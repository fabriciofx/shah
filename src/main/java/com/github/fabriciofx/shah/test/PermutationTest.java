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
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.CollisionRatio;
import java.util.function.Function;

/**
 * Permutation key test from SMHasher.
 *
 * <p>Compute the collision ratio for permutation keys.</p>
 *
 * <p>Generates keys by permuting a small set of byte values across
 * a fixed number of positions. For example, with values {0, 1, 2, 3}
 * and 4 positions, all 4! = 24 permutations are generated.</p>
 *
 * <p>This tests the hash function's ability to distinguish keys
 * that contain the same byte values in different orders.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class PermutationTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Byte values to permute.
     */
    private final byte[] values;

    /**
     * Number of positions in the key.
     */
    private final int positions;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param values Byte values to permute
     * @param positions Number of positions in the key
     */
    public PermutationTest(
        final Function<Key, Hash> func,
        final byte[] values,
        final int positions
    ) {
        this.func = func;
        this.values = values.clone();
        this.positions = positions;
    }

    @Override
    public Double metric() {
        int total = 1;
        for (int idx = 0; idx < this.positions; ++idx) {
            total *= this.values.length;
        }
        final Hashes hashes = new HashesOf();
        final byte[] bytes = new byte[this.positions];
        for (int idx = 0; idx < total; ++idx) {
            int num = idx;
            for (int pos = 0; pos < this.positions; ++pos) {
                bytes[pos] = this.values[num % this.values.length];
                num /= this.values.length;
            }
            hashes.add(this.func.apply(new KeyOf(bytes)));
        }
        return new CollisionRatio(hashes).value();
    }
}
