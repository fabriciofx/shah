/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;

/**
 * BytesDiff.
 *
 * <p>Compute a difference between two byte arrays.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
public final class BytesDiff implements Scalar<byte[]> {
    /**
     * First byte array.
     */
    private final byte[] first;

    /**
     * Second byte array.
     */
    private final byte[] second;

    /**
     * Ctor.
     * @param first First byte array
     * @param second Second byte array
     */
    public BytesDiff(final byte[] first, final byte[] second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public byte[] value() {
        final int length = Math.min(this.first.length, this.second.length);
        final byte[] diffs = new byte[length];
        for (int idx = 0; idx < length; ++idx) {
            diffs[idx] = (byte) (this.first[idx] ^ this.second[idx]);
        }
        return diffs;
    }
}
