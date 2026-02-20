/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.collection;

import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Words.
 *
 * <p>A collection of random words.</p>
 *
 * @since 0.0.1
 * @checkstyle ParameterNumberCheck (200 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Words implements Collection<String> {
    /**
     * Default character set (alphanumeric).
     */
    private static final String CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Default word count.
     */
    private static final int DEFAULT_COUNT = 100_000;

    /**
     * Default minimum word length.
     */
    private static final int DEFAULT_MIN_LEN = 2;

    /**
     * Default maximum word length.
     */
    private static final int DEFAULT_MAX_LEN = 20;

    /**
     * Default random seed.
     */
    private static final long DEFAULT_SEED = 82_762L;

    /**
     * The set of words.
     */
    private final Scalar<Set<String>> items;

    /**
     * Ctor with defaults.
     */
    public Words() {
        this(
            Words.DEFAULT_COUNT,
            Words.DEFAULT_MIN_LEN,
            Words.DEFAULT_MAX_LEN,
            new Random(Words.DEFAULT_SEED)
        );
    }

    /**
     * Ctor.
     * @param count Number of words
     * @param min Minimum word length
     * @param max Maximum word length
     */
    public Words(final int count, final int min, final int max) {
        this(count, min, max, new Random(Words.DEFAULT_SEED));
    }

    /**
     * Ctor.
     * @param count Number of words
     * @param min Minimum word length
     * @param max Maximum word length
     * @param random Random generator for reproducibility
     */
    public Words(
        final int count,
        final int min,
        final int max,
        final Random random
    ) {
        this(
            new Cached<>(
                () -> {
                    final Set<String> seen = new HashSet<>();
                    while (seen.size() <= count) {
                        final int length = min + random.nextInt(max - min);
                        final char[] letters = new char[length];
                        for (int idx = 0; idx < length; ++idx) {
                            letters[idx] = Words.CHARS.charAt(
                                random.nextInt(Words.CHARS.length())
                            );
                        }
                        seen.add(new String(letters));
                    }
                    return seen;
                }
            )
        );
    }

    /**
     * Ctor.
     * @param items The set of words
     */
    public Words(final Scalar<Set<String>> items) {
        this.items = items;
    }

    @Override
    public int size() {
        return this.items.value().size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.value().isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return this.items.value().contains(obj);
    }

    @Override
    public Iterator<String> iterator() {
        return this.items.value().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.items.value().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] array) {
        return this.items.value().toArray(array);
    }

    @Override
    public boolean add(final String word) {
        return this.items.value().add(word);
    }

    @Override
    public boolean remove(final Object obj) {
        return this.items.value().remove(obj);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.items.value().containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends String> collection) {
        return this.items.value().addAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return this.items.value().removeAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return this.items.value().retainAll(collection);
    }

    @Override
    public void clear() {
        this.items.value().clear();
    }
}
