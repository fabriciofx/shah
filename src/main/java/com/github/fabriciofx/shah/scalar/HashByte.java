/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;

/**
 * Hash byte.
 *
 * <p>Extracts a specific byte from a hash output, allowing us to analyze
 * the distribution of individual bytes across different hash outputs. This is
 * useful for assessing the randomness and uniformity of the hash function's
 * output at the byte level.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.UnnecessaryLocalRule")
public final class HashByte implements Scalar<Byte> {
    /**
     * Hash.
     */
    private final Hash hash;

    /**
     * Byte index (0-based).
     */
    private final int index;

    /**
     * Ctor.
     * @param hash Hash to extract the byte from
     * @param index Byte index (0-based)
     */
    public HashByte(final Hash hash, final int index) {
        this.hash = hash;
        this.index = index;
    }

    @Override
    public Byte value() {
        final byte[] bytes = this.hash.asBytes();
        return bytes[this.index];
    }
}
