/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.seed.Seed32;
import java.util.function.BiFunction;

/**
 * Verification test from SMHasher.
 *
 * <p>Computes a verification code that uniquely identifies a hash
 * function implementation. This test is used to confirm that a hash
 * function is correctly implemented on a given platform.</p>
 *
 * <p>Hashes keys of the form {0}, {0,1}, {0,1,2}, ..., {0,1,...,254}
 * using seed {@code 256 - i} for each key of length {@code i}.
 * Then hashes all the concatenated results with seed 0. The first
 * four bytes of the final hash, interpreted as a little-endian
 * integer, form the verification value.</p>
 *
 * <p>Returns the verification code as a long. The JUnit test
 * should compare this against the known expected value for each
 * hash function (e.g., {@code 0xB0F57EE3L} for Murmur3-32).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class VerificationTest implements Test<Long> {
    /**
     * Number of keys to hash (0 through 255).
     */
    private static final int KEY_COUNT = 256;

    /**
     * Byte mask.
     */
    private static final int BYTE_MASK = 0xFF;

    /**
     * The hash under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Hash function seed.
     */
    private final Seed seed;

    /**
     * Ctor.
     * @param func The hash function under test
     */
    public VerificationTest(final BiFunction<Key, Seed, Hash> func) {
        this(func, new Seed32(0));
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param seed The hash function seed
     */
    public VerificationTest(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed
    ) {
        this.func = func;
        this.seed = seed;
    }

    @Override
    public Long metric() {
        final byte[] keybuf = new byte[VerificationTest.KEY_COUNT];
        final Hash probe = this.func.apply(new KeyOf(new byte[0]), this.seed);
        final int hashbytes = probe.bits() / 8;
        final byte[] hashes =
            new byte[hashbytes * VerificationTest.KEY_COUNT];
        for (int idx = 0; idx < VerificationTest.KEY_COUNT; ++idx) {
            keybuf[idx] = (byte) idx;
            final byte[] slice = new byte[idx];
            System.arraycopy(keybuf, 0, slice, 0, idx);
            System.arraycopy(
                this.func.apply(
                    new KeyOf(slice),
                    new Seed32(VerificationTest.KEY_COUNT - idx)
                ).asBytes(), 0,
                hashes, idx * hashbytes,
                hashbytes
            );
        }
        return VerificationTest.littleEndian(
            this.func.apply(
                new KeyOf(hashes),
                new Seed32(0)
            ).asBytes()
        );
    }

    /**
     * Convert the first 4 bytes to a little-endian integer.
     * @param bytes The byte array
     * @return Little-endian unsigned 32-bit integer as long
     */
    private static long littleEndian(final byte[] bytes) {
        return Integer.toUnsignedLong(
            VerificationTest.loWord(bytes)
                | VerificationTest.hiWord(bytes)
        );
    }

    /**
     * Extract low 16 bits from first 2 bytes.
     * @param bytes The byte array
     * @return Low word as int
     */
    private static int loWord(final byte[] bytes) {
        return (bytes[0] & VerificationTest.BYTE_MASK)
            | ((bytes[1] & VerificationTest.BYTE_MASK) << 8);
    }

    /**
     * Extract high 16 bits from bytes 2 and 3.
     * @param bytes The byte array
     * @return High word as int
     */
    private static int hiWord(final byte[] bytes) {
        return ((bytes[2] & VerificationTest.BYTE_MASK) << 16)
            | ((bytes[3] & VerificationTest.BYTE_MASK) << 24);
    }
}
