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
 * <p>Returns the collision ratio among all generated hashes.</p>
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
     * Default input length.
     */
    private static final int DEFAULT_INPUT_LEN = 4;

    /**
     * The hash under test, accepting (key, seed).
     */
    private final BiFunction<Key, Integer, Hash> func;

    /**
     * Number of bits for X coordinate.
     */
    private final int xbits;

    /**
     * Number of bits for Y coordinate (seed).
     */
    private final int ybits;

    /**
     * Input key length in bytes.
     */
    private final int inputlen;

    /**
     * Ctor with defaults (10-bit X, 10-bit Y, 4-byte keys).
     * @param func The hash function under test, accepting (key, seed)
     */
    public PerlinNoiseTest(final BiFunction<Key, Integer, Hash> func) {
        this(
            func,
            PerlinNoiseTest.DEFAULT_XBITS,
            PerlinNoiseTest.DEFAULT_YBITS,
            PerlinNoiseTest.DEFAULT_INPUT_LEN
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test, accepting (key, seed)
     * @param xbits Number of bits for X coordinate
     * @param ybits Number of bits for Y coordinate (seed)
     * @param inputlen Input key length in bytes
     */
    public PerlinNoiseTest(
        final BiFunction<Key, Integer, Hash> func,
        final int xbits,
        final int ybits,
        final int inputlen
    ) {
        this.func = func;
        this.xbits = xbits;
        this.ybits = ybits;
        this.inputlen = inputlen;
    }

    @Override
    public Collisions metric() {
        final int xmax = 1 << this.xbits;
        final int ymax = 1 << this.ybits;
        final Hashes hashes = new HashesOf();
        for (int xval = 0; xval < xmax; ++xval) {
            final byte[] keybuf = new byte[this.inputlen];
            keybuf[0] = (byte) xval;
            if (this.inputlen > 1) {
                keybuf[1] = (byte) (xval >>> 8);
            }
            if (this.inputlen > 2) {
                keybuf[2] = (byte) (xval >>> 16);
            }
            if (this.inputlen > 3) {
                keybuf[3] = (byte) (xval >>> 24);
            }
            for (int yval = 0; yval < ymax; ++yval) {
                hashes.add(
                    this.func.apply(new KeyOf(keybuf), yval)
                );
            }
        }
        return new Collisions(hashes);
    }
}
