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
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.seed.Seed32;
import java.util.function.BiFunction;

/**
 * Permutation key measure from SMHasher.
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
public final class PermutationKey implements Measure<Collisions> {

    /**
     * The hash under measure.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

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
     *
     * @param func The hash function under measure
     * @param values Byte values to permute
     * @param positions Number of positions in the key
     */
    public PermutationKey(
        final BiFunction<Key, Seed, Hash> func,
        final byte[] values,
        final int positions
    ) {
        this(func, new Seed32(0), values, positions);
    }

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     * @param seed The hash function seed
     * @param values Byte values to permute
     * @param positions Number of positions in the key
     */
    public PermutationKey(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final byte[] values,
        final int positions
    ) {
        this.func = func;
        this.seed = seed;
        this.values = values.clone();
        this.positions = positions;
    }

    @Override
    public Collisions metric() {
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
            hashes.add(this.func.apply(new KeyOf(bytes), this.seed));
        }
        return new Collisions(hashes);
    }
}
