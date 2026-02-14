/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Hashes.
 *
 * <p>A collection of hash outputs.</p>
 *
 * @since 0.0.1
 */
public interface Hashes extends Iterable<Hash> {
    /**
     * Add a hash to the collection.
     * @param hash The hash to add
     */
    void add(Hash hash);

    /**
     * Get the number of hashes in the collection.
     * @return The number of hashes
     */
    int count();

    /**
     * Get the hash at the specified index.
     * @param index The index of the hash to retrieve
     * @return The hash at the specified index
     */
    Hash item(int index);
}
