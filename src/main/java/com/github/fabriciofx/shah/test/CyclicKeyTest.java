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
import com.github.fabriciofx.shah.metric.Collisions;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Cyclic key test from SMHasher.
 *
 * <p>Compute the collision ratio for cyclic keys.</p>
 *
 * <p>Generates keys that consist of N repetitions of a short byte
 * pattern. For each key, random bytes fill the cycle pattern, then
 * the first 4 bytes are overwritten with a deterministic mix of the
 * key index (using the f3mix finalizer from MurmurHash3). The cycle
 * is then repeated to form the full key.</p>
 *
 * <p>This matches the SMHasher rurban approach which generates
 * {@code keycount} random cyclic keys using seed 483723 and the
 * f3mix function {@code f3mix(i ^ 0x746a94f1)}.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 * @checkstyle NestedForDepthCheck (200 lines)
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings({"PMD.TestClassWithoutTestCases", "PMD.AvoidArrayLoops"})
public final class CyclicKeyTest implements Test<Collisions> {
    /**
     * Default random seed (matches SMHasher).
     */
    private static final long DEFAULT_SEED = 483_723L;

    /**
     * Mix constant from SMHasher f3mix.
     */
    private static final int MIX_XOR = 0x746a94f1;

    /**
     * First mix multiplier.
     */
    private static final int MIX_MUL1 = 0x85ebca6b;

    /**
     * Second mix multiplier.
     */
    private static final int MIX_MUL2 = 0xc2b2ae35;

    /**
     * Default number of keys.
     */
    private static final int DEFAULT_COUNT = 100_000;

    /**
     * The hash function under test.
     */
    private final BiFunction<Key, Long, Hash> func;

    /**
     * Seed for hash function.
     */
    private final long seed;

    /**
     * Cycle length (pattern length in bytes).
     */
    private final int length;

    /**
     * Number of cycle repetitions.
     */
    private final int repetitions;

    /**
     * Number of keys to generate.
     */
    private final int count;

    /**
     * Ctor with default key count.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param length Cycle pattern length in bytes
     * @param repetitions Number of cycle repetitions
     */
    public CyclicKeyTest(
        final BiFunction<Key, Long, Hash> func,
        final long seed,
        final int length,
        final int repetitions
    ) {
        this(func, seed, length, CyclicKeyTest.DEFAULT_COUNT, repetitions);
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     * @param length Cycle pattern length in bytes
     * @param count Number of keys to generate
     * @param repetitions Number of cycle repetitions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public CyclicKeyTest(
        final BiFunction<Key, Long, Hash> func,
        final long seed,
        final int length,
        final int count,
        final int repetitions
    ) {
        this.func = func;
        this.seed = seed;
        this.length = length;
        this.count = count;
        this.repetitions = repetitions;
    }

    @Override
    public Collisions metric() {
        final Random random = new Random(CyclicKeyTest.DEFAULT_SEED);
        final int size = this.length * this.repetitions;
        final Hashes hashes = new HashesOf();
        for (int idx = 0; idx < this.count; ++idx) {
            final byte[] cycle = new byte[this.length];
            random.nextBytes(cycle);
            final int mixed = CyclicKeyTest.finalMix(
                idx ^ CyclicKeyTest.MIX_XOR
            );
            cycle[0] = (byte) mixed;
            if (this.length > 1) {
                cycle[1] = (byte) (mixed >>> 8);
            }
            if (this.length > 2) {
                cycle[2] = (byte) (mixed >>> 16);
            }
            if (this.length > 3) {
                cycle[3] = (byte) (mixed >>> 24);
            }
            final byte[] bytes = new byte[size];
            for (int pos = 0; pos < size; ++pos) {
                bytes[pos] = cycle[pos % this.length];
            }
            hashes.add(this.func.apply(new KeyOf(bytes), this.seed));
        }
        return new Collisions(hashes);
    }

    /**
     * MurmurHash3 32-bit finalizer (f3mix from SMHasher Stats.h).
     * @param value Input value
     * @return Mixed value
     */
    private static int finalMix(final int value) {
        int mixed = value;
        mixed ^= mixed >>> 16;
        mixed *= CyclicKeyTest.MIX_MUL1;
        mixed ^= mixed >>> 13;
        mixed *= CyclicKeyTest.MIX_MUL2;
        mixed ^= mixed >>> 16;
        return mixed;
    }
}
