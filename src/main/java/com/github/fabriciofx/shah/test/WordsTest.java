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
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Words test from SMHasher.
 *
 * <p>Generates random words from a character set and measures the
 * collision ratio of their hashes. Each word has a random length
 * between {@code minlen} and {@code maxlen}, and is composed of
 * characters randomly chosen from the given charset.</p>
 *
 * <p>Only unique words are hashed, matching SMHasher behavior which
 * skips duplicate words to avoid false collision reports.</p>
 *
 * <p>Returns the collision ratio among all generated hashes.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class WordsTest implements Test<Collisions> {
    /**
     * Default character set (alphanumeric).
     */
    private static final String DEFAULT_CHARSET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Default random seed.
     */
    private static final long DEFAULT_SEED = 483_723L;

    /**
     * The hash under test.
     */
    private final Function<Key, Hash> func;

    /**
     * Number of words to generate.
     */
    private final int count;

    /**
     * Minimum word length.
     */
    private final int minlen;

    /**
     * Maximum word length.
     */
    private final int maxlen;

    /**
     * Character set to draw from.
     */
    private final String charset;

    /**
     * Random seed.
     */
    private final long seed;

    /**
     * Ctor with defaults.
     * @param func The hash function under test
     * @param count Number of words to generate
     * @param minlen Minimum word length
     * @param maxlen Maximum word length
     */
    public WordsTest(
        final Function<Key, Hash> func,
        final int count,
        final int minlen,
        final int maxlen
    ) {
        this(
            func, count, minlen, maxlen,
            WordsTest.DEFAULT_CHARSET,
            WordsTest.DEFAULT_SEED
        );
    }

    /**
     * Ctor.
     * @param func The hash function under test
     * @param count Number of words to generate
     * @param minlen Minimum word length
     * @param maxlen Maximum word length
     * @param charset Character set to draw from
     * @param seed Random seed for reproducibility
     */
    public WordsTest(
        final Function<Key, Hash> func,
        final int count,
        final int minlen,
        final int maxlen,
        final String charset,
        final long seed
    ) {
        this.func = func;
        this.count = count;
        this.minlen = minlen;
        this.maxlen = maxlen;
        this.charset = charset;
        this.seed = seed;
    }

    @Override
    public Collisions metric() {
        final Random random = new Random(this.seed);
        final Hashes hashes = new HashesOf();
        final Set<String> seen = new HashSet<>();
        int generated = 0;
        while (generated < this.count) {
            final int len = this.minlen
                + random.nextInt(this.maxlen - this.minlen);
            final char[] word = new char[len];
            for (int idx = 0; idx < len; ++idx) {
                word[idx] = this.charset.charAt(
                    random.nextInt(this.charset.length())
                );
            }
            final String wordstr = new String(word);
            if (seen.add(wordstr)) {
                hashes.add(this.func.apply(new KeyOf(wordstr)));
                generated += 1;
            }
        }
        return new Collisions(hashes);
    }
}
