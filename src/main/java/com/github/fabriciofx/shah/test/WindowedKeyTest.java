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
 * Windowed key test from SMHasher.
 *
 * <p>Compute the collision ratio for windowed keys.</p>
 *
 * <p>For each starting bit position in a key, assigns a contiguous
 * window of bits to all possible values (0 to 2^width - 1), keeping
 * all other bits zero, then rotates the result leftward to the
 * starting position. This matches SMHasher's approach using
 * {@code lrot} (left rotation).</p>
 *
 * <p>The window width is automatically increased if necessary so
 * that the expected number of birthday-paradox collisions is at
 * least 0.5, matching SMHasher's dynamic sizing behavior. The
 * maximum window width is capped at 25 bits (2^25 keys).</p>
 *
 * <p>Returns the worst (maximum) collision ratio across all
 * window starting positions.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle MagicNumberCheck (200 lines)
 * @checkstyle NestedForDepthCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class WindowedKeyTest implements Test<Double> {
    /**
     * Maximum window width (2^25 keys cap).
     */
    private static final int MAX_WINDOW = 25;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Key length in bytes.
     */
    private final int size;

    /**
     * Initial window width in bits.
     */
    private final int width;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param size Key length in bytes
     * @param width Window width in bits
     */
    public WindowedKeyTest(
        final Function<Key, Hash> func,
        final int size,
        final int width
    ) {
        this.func = func;
        this.size = size;
        this.width = width;
    }

    @Override
    public Double metric() {
        int window = this.width;
        int keycount = 1 << window;
        while (WindowedKeyTest.estimate(
            keycount,
            this.func.apply(new KeyOf(this.size)).bits()
        ) < 0.5
            && window < WindowedKeyTest.MAX_WINDOW) {
            keycount *= 2;
            window += 1;
        }
        final int keybits = this.size * 8;
        double worst = 0.0;
        for (int start = 0; start <= keybits; ++start) {
            final Hashes hashes = new HashesOf();
            for (int val = 0; val < keycount; ++val) {
                final byte[] bytes = new byte[this.size];
                WindowedKeyTest.setAndRotate(
                    bytes, val, window, start
                );
                hashes.add(this.func.apply(new KeyOf(bytes)));
            }
            final double ratio = new CollisionRatio(hashes).value();
            if (ratio > worst) {
                worst = ratio;
            }
        }
        return worst;
    }

    /**
     * Set the low {@code width} bits of the byte array to {@code val},
     * then left-rotate the entire array by {@code rotate} bits.
     * Matches SMHasher's approach: {@code key = i; lrot(key, size, j)}.
     * @param bytes The byte buffer (zeroed)
     * @param val Value to set in low bits
     * @param width Width in bits
     * @param rotate Number of bits to rotate left
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static void setAndRotate(
        final byte[] bytes,
        final int val,
        final int width,
        final int rotate
    ) {
        for (int bit = 0; bit < width; ++bit) {
            if (((val >> bit) & 1) == 1) {
                final int pos = (bit + rotate) % (bytes.length * 8);
                bytes[pos >> 3] |= (byte) (1 << (pos & 7));
            }
        }
    }

    /**
     * Estimate expected collisions (birthday paradox, sparse regime).
     * Matches SMHasher's fwojcik estimator for sparse cases.
     * @param balls Number of keys (balls)
     * @param bits Hash width in bits
     * @return Expected number of collisions
     */
    private static double estimate(final int balls, final int bits) {
        return (double) balls * (balls - 1)
            / (2.0 * Math.pow(2.0, bits));
    }
}
