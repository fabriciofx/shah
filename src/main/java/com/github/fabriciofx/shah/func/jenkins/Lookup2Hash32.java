/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.Func;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.hash.Hash32;

/**
 * Jenkins Lookup2 hash.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Jenkins_hash_function#Lookup2">
 * Jenkins Lookup2 Hash</a>
 * @since 0.0.1
 * @checkstyle FallThroughCheck (300 lines)
 * @checkstyle UnnecessaryParenthesesCheck (300 lines)
 * @checkstyle NcssCountCheck (300 lines)
 * @checkstyle CyclomaticComplexityCheck (300 lines)
 * @checkstyle JavaNCSSCheck (300 lines)
 * @checkstyle ExecutableStatementCountCheck (300 lines)
 * @checkstyle BooleanExpressionComplexityCheck (300 lines)
 */
@SuppressWarnings({
    "PMD.ImplicitSwitchFallThrough",
    "PMD.NcssCount"
})
public final class Lookup2Hash32 implements Func {
    /**
     * The golden ratio, an arbitrary value.
     */
    private static final int GOLDEN_RATIO = 0x9e3779b9;

    /**
     * The key to be hashed.
     */
    private final Key key;

    /**
     * The seed.
     */
    private final Seed seed;

    /**
     * Ctor.
     *
     * @param key The key to be hashed
     * @param seed The seed
     */
    public Lookup2Hash32(final Key key, final Seed seed) {
        this.key = key;
        this.seed = seed;
    }

    @SuppressWarnings("fallthrough")
    @Override
    public Hash hash() {
        final byte[] bytes = this.key.asBytes();
        int length = bytes.length;
        int first = Lookup2Hash32.GOLDEN_RATIO;
        int second = Lookup2Hash32.GOLDEN_RATIO;
        int third = this.seed.asInt();
        int idx = 0;
        while (length >= 12) {
            first += (bytes[idx] & 0xff)
                | ((bytes[idx + 1] & 0xff) << 8)
                | ((bytes[idx + 2] & 0xff) << 16)
                | ((bytes[idx + 3] & 0xff) << 24);
            second += (bytes[idx + 4] & 0xff)
                | ((bytes[idx + 5] & 0xff) << 8)
                | ((bytes[idx + 6] & 0xff) << 16)
                | ((bytes[idx + 7] & 0xff) << 24);
            third += (bytes[idx + 8] & 0xff)
                | ((bytes[idx + 9] & 0xff) << 8)
                | ((bytes[idx + 10] & 0xff) << 16)
                | ((bytes[idx + 11] & 0xff) << 24);
            first -= second;
            first -= third;
            first ^= (third >>> 13);
            second -= third;
            second -= first;
            second ^= (first << 8);
            third -= first;
            third -= second;
            third ^= (second >>> 13);
            first -= second;
            first -= third;
            first ^= (third >>> 12);
            second -= third;
            second -= first;
            second ^= (first << 16);
            third -= first;
            third -= second;
            third ^= (second >>> 5);
            first -= second;
            first -= third;
            first ^= (third >>> 3);
            second -= third;
            second -= first;
            second ^= (first << 10);
            third -= first;
            third -= second;
            third ^= (second >>> 15);
            idx += 12;
            length -= 12;
        }
        third += bytes.length;
        switch (length) {
            case 11:
                third += (bytes[idx + 10] & 0xff) << 24;
            case 10:
                third += (bytes[idx + 9] & 0xff) << 16;
            case 9:
                third += (bytes[idx + 8] & 0xff) << 8;
            case 8:
                second += (bytes[idx + 7] & 0xff) << 24;
            case 7:
                second += (bytes[idx + 6] & 0xff) << 16;
            case 6:
                second += (bytes[idx + 5] & 0xff) << 8;
            case 5:
                second += (bytes[idx + 4] & 0xff);
            case 4:
                first += (bytes[idx + 3] & 0xff) << 24;
            case 3:
                first += (bytes[idx + 2] & 0xff) << 16;
            case 2:
                first += (bytes[idx + 1] & 0xff) << 8;
            case 1:
                first += (bytes[idx] & 0xff);
            default:
                break;
        }
        first -= second;
        first -= third;
        first ^= (third >>> 13);
        second -= third;
        second -= first;
        second ^= (first << 8);
        third -= first;
        third -= second;
        third ^= (second >>> 13);
        first -= second;
        first -= third;
        first ^= (third >>> 12);
        second -= third;
        second -= first;
        second ^= (first << 16);
        third -= first;
        third -= second;
        third ^= (second >>> 5);
        first -= second;
        first -= third;
        first ^= (third >>> 3);
        second -= third;
        second -= first;
        second ^= (first << 10);
        third -= first;
        third -= second;
        third ^= (second >>> 15);
        return new Hash32(third);
    }
}
