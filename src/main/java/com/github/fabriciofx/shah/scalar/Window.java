/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;

/**
 * Window.
 *
 * <p>Extract bits from a byte array at a circular bit offset. Reads up to 16
 * bits starting at the given bit position, wrapping around if necessary.</p>
 * @since 0.0.1
 */
public final class Window implements Scalar<Integer> {
    /**
     * Hash.
     */
    private final Hash hash;

    /**
     * A bit start position.
     */
    private final int start;

    /**
     * Ctor.
     * @param hash A hash
     * @param start A bit start position
     */
    public Window(final Hash hash, final int start) {
        this.hash = hash;
        this.start = start;
    }

    @Override
    public Integer value() {
        final byte[] bytes = this.hash.asBytes();
        int bits = 0;
        for (int idx = 0; idx < 16; ++idx) {
            final int pos = (this.start + idx) % this.hash.bits();
            final int bit = (bytes[pos >> 3] >> (pos & 7)) & 1;
            bits |= bit << idx;
        }
        return bits;
    }
}
