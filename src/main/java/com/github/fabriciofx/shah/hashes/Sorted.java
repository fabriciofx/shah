/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hashes;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.comparator.HashComparator;
import com.github.fabriciofx.shah.scalar.Cached;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Sorted.
 *
 * <p>It represents a collection of hashes sorted by their value byte to byte.
 * </p>
 *
 * @since 0.0.1
 */
public final class Sorted implements Hashes {
    /**
     * Sorted items.
     */
    private final Scalar<List<Hash>> items;

    /**
     * Ctor.
     * @param hashes Hashes to be sorted
     */
    public Sorted(final Hashes hashes) {
        this.items = new Cached<>(
            () -> StreamSupport
                .stream(hashes.spliterator(), false)
                .sorted(new HashComparator())
                .collect(Collectors.toList())
        );
    }

    @Override
    public void add(final Hash hash) {
        this.items.value().add(hash);
        this.items.value().sort(new HashComparator());
    }

    @Override
    public int count() {
        return this.items.value().size();
    }

    @Override
    public Hash item(final int index) {
        return this.items.value().get(index);
    }

    @Override
    public Iterator<Hash> iterator() {
        return this.items.value().iterator();
    }
}
