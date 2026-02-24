/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.test;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.Test;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.seed.Seed64;
import java.util.function.BiFunction;

/**
 * Perlin noise test from SMHasher.
 *
 * <p>Tests hash quality with coordinate-based keys typical of Perlin
 * noise applications, where X coordinates form the key content and
 * Y coordinates are used as seeds.</p>
 *
 * <p>Generates all combinations of X in [0, 2^xBits) and Y in
 * [0, 2^yBits), where X is stored as a little-endian byte array
 * of the given input length and Y is passed as the hash seed.
 * This produces {@code 2^(xBits+yBits)} total hashes.</p>
 *
 * <p>Returns the collisions among all generated hashes.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class PerlinNoiseTest implements Test<Collisions> {
    /**
     * Default X bits.
     */
    private static final int DEFAULT_XBITS = 10;

    /**
     * Default Y bits.
     */
    private static final int DEFAULT_YBITS = 10;

    /**
     * Default key size.
     */
    private static final int DEFAULT_SIZE = 4;

    /**
     * The hash under test.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Key's size.
     */
    private final int size;

    /**
     * Number of bits for X coordinate.
     */
    private final int xbits;

    /**
     * Number of bits for Y coordinate (seed).
     */
    private final int ybits;

    /**
     * Ctor with defaults (10-bit X, 10-bit Y, 4-byte keys).
     * @param func The hash function under test, accepting (key, seed)
     */
    public PerlinNoiseTest(final BiFunction<Key, Seed, Hash> func) {
        this(
            func,
            PerlinNoiseTest.DEFAULT_SIZE,
            PerlinNoiseTest.DEFAULT_XBITS,
            PerlinNoiseTest.DEFAULT_YBITS
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test, accepting (key, seed)
     * @param size Input key length in bytes
     * @param xbits Number of bits for X coordinate
     * @param ybits Number of bits for Y coordinate (seed)
     */
    public PerlinNoiseTest(
        final BiFunction<Key, Seed, Hash> func,
        final int size,
        final int xbits,
        final int ybits
    ) {
        this.func = func;
        this.size = size;
        this.xbits = xbits;
        this.ybits = ybits;
    }

    @Override
    public Collisions metric() {
        final int xmax = 1 << this.xbits;
        final int ymax = 1 << this.ybits;
        final Hashes hashes = new HashesOf();
        for (int xcoord = 0; xcoord < xmax; ++xcoord) {
            final byte[] bytes = new byte[this.size];
            bytes[0] = (byte) xcoord;
            if (this.size > 1) {
                bytes[1] = (byte) (xcoord >>> 8);
            }
            if (this.size > 2) {
                bytes[2] = (byte) (xcoord >>> 16);
            }
            if (this.size > 3) {
                bytes[3] = (byte) (xcoord >>> 24);
            }
            for (long ycoord = 0; ycoord < ymax; ++ycoord) {
                hashes.add(
                    this.func.apply(
                        new KeyOf(bytes),
                        new Seed64(ycoord)
                    )
                );
            }
        }
        return new Collisions(hashes);
    }
}
