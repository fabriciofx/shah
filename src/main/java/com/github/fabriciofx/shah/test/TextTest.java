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
 * Text key test from SMHasher.
 *
 * <p>Compute the collision ratio for text-like keys.</p>
 *
 * <p>Generates keys resembling text patterns: a fixed prefix and
 * suffix with one varying byte position in between. For each
 * position, all 256 byte values are tried at that position.</p>
 *
 * <p>This tests the hash function's sensitivity to single-byte
 * changes within a text-like context, simulating scenarios like
 * hashing URLs, identifiers, or other structured text. Only
 * non-zero byte values (1-255) are used at each position to avoid
 * generating duplicate keys.</p>
 *
 * <p>Supports hash outputs of any width (32, 64, 128, 256 bits,
 * etc.).</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TextTest implements Test<Double> {
    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Prefix bytes.
     */
    private final byte[] prefix;

    /**
     * Suffix bytes.
     */
    private final byte[] suffix;

    /**
     * Number of varying positions between prefix and suffix.
     */
    private final int varying;

    /**
     * Ctor.
     * @param func The hash function under test
     * @param prefix Prefix bytes
     * @param suffix Suffix bytes
     * @param varying Number of varying positions
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public TextTest(
        final Function<Key, Hash> func,
        final byte[] prefix,
        final byte[] suffix,
        final int varying
    ) {
        this.func = func;
        this.prefix = prefix.clone();
        this.suffix = suffix.clone();
        this.varying = varying;
    }

    @Override
    public Double metric() {
        final Hashes hashes = new HashesOf();
        final int size = this.prefix.length + this.varying
            + this.suffix.length;
        final byte[] base = new byte[size];
        System.arraycopy(this.prefix, 0, base, 0, this.prefix.length);
        System.arraycopy(
            this.suffix,
            0,
            base,
            this.prefix.length + this.varying,
            this.suffix.length
        );
        hashes.add(this.func.apply(new KeyOf(base)));
        for (int pos = 0; pos < this.varying; ++pos) {
            for (int val = 1; val < 256; ++val) {
                final byte[] bytes = base.clone();
                bytes[this.prefix.length + pos] = (byte) val;
                hashes.add(this.func.apply(new KeyOf(bytes)));
            }
        }
        return new CollisionRatio(hashes).value();
    }
}
