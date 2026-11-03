/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.measure;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Measure;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.seed.Seed32;
import java.util.function.BiFunction;

/**
 * Verification measure from SMHasher.
 *
 * <p>Computes a verification code that uniquely identifies a hash
 * function implementation. This measure is used to confirm that a hash
 * function is correctly implemented on a given platform.</p>
 *
 * <p>Hashes keys of the form {0}, {0,1}, {0,1,2}, ..., {0,1,...,254}
 * using seed {@code 256 - i} for each key of length {@code i}.
 * Then hashes all the concatenated results with seed 0. The first
 * four bytes of the final hash, interpreted as a little-endian
 * integer, form the verification value.</p>
 *
 * <p>Returns the verification code as a long. The JUnit measure
 * should compare this against the known expected value for each
 * hash function (e.g., {@code 0xB0F57EE3L} for Murmur3-32).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class Verification implements Measure<Long> {

    /**
     * Number of keys to hash (0 through 255).
     */
    private static final int KEY_COUNT = 256;

    /**
     * Byte mask.
     */
    private static final int BYTE_MASK = 0xFF;

    /**
     * The hash under measure.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     */
    public Verification(final BiFunction<Key, Seed, Hash> func) {
        this(func, new Seed32(0));
    }

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     * @param seed The hash function seed
     */
    public Verification(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed
    ) {
        this.func = func;
        this.seed = seed;
    }

    @Override
    public Long metric() {
        final byte[] keybuf = new byte[Verification.KEY_COUNT];
        final Hash probe = this.func.apply(new KeyOf(new byte[0]), this.seed);
        final int hashbytes = probe.bits() / 8;
        final byte[] hashes =
            new byte[hashbytes * Verification.KEY_COUNT];
        for (int idx = 0; idx < Verification.KEY_COUNT; ++idx) {
            keybuf[idx] = (byte) idx;
            final byte[] slice = new byte[idx];
            System.arraycopy(keybuf, 0, slice, 0, idx);
            System.arraycopy(
                this.func.apply(
                    new KeyOf(slice),
                    new Seed32(Verification.KEY_COUNT - idx)
                ).asBytes(), 0,
                hashes, idx * hashbytes,
                hashbytes
            );
        }
        return Verification.littleEndian(
            this.func.apply(
                new KeyOf(hashes),
                new Seed32(0)
            ).asBytes()
        );
    }

    private static long littleEndian(final byte[] bytes) {
        return Integer.toUnsignedLong(
            Verification.loWord(bytes)
                | Verification.hiWord(bytes)
        );
    }

    private static int loWord(final byte[] bytes) {
        return (bytes[0] & Verification.BYTE_MASK)
            | ((bytes[1] & Verification.BYTE_MASK) << 8);
    }

    private static int hiWord(final byte[] bytes) {
        return ((bytes[2] & Verification.BYTE_MASK) << 16)
            | ((bytes[3] & Verification.BYTE_MASK) << 24);
    }
}
