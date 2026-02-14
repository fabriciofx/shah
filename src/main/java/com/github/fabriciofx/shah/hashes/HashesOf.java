/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.hashes;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * HashesOf.
 *
 * <p>It represents a collection of hashes.</p>
 *
 * @since 0.0.1
 */
public final class HashesOf implements Hashes {
    /**
     * Items.
     */
    private final List<Hash> items;

    /**
     * Ctor.
     */
    public HashesOf() {
        this(new LinkedList<>());
    }

    /**
     * Ctor.
     * @param items Items
     */
    public HashesOf(final List<Hash> items) {
        this.items = items;
    }

    @Override
    public void add(final Hash hash) {
        this.items.add(hash);
    }

    @Override
    public int count() {
        return this.items.size();
    }

    @Override
    public Hash item(final int index) {
        return this.items.get(index);
    }

    @Override
    public Iterator<Hash> iterator() {
        return this.items.iterator();
    }
}
