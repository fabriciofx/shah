/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;

/**
 * Hash bit.
 *
 * <p>Extracts a specific bit from a hash output, allowing us to analyze
 * the distribution of individual bits across different hash outputs. This is
 * useful for assessing the randomness and uniformity of the hash function's
 * output at the bit level.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class HashBit implements Scalar<Integer> {
    /**
     * Hash.
     */
    private final Hash hash;

    /**
     * Bit index (0-based).
     */
    private final int index;

    /**
     * Ctor.
     * @param hash Hash to extract the bit from
     * @param index Bit index (0-based)
     */
    public HashBit(final Hash hash, final int index) {
        this.hash = hash;
        this.index = index;
    }

    @Override
    public Integer value() {
        final byte[] bytes = this.hash.asBytes();
        return (bytes[this.index >> 3] >> (this.index & 7)) & 1;
    }
}
