/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;

/**
 * Byte difference (XOR) between two bytes.
 *
 * <p>Computes the bitwise exclusive OR (XOR) of two bytes, which
 * indicates the positions where their bits differ. This is useful for
 * measuring how many bits change in a hash output when the input key
 * changes, which is a key aspect of hash function quality.</p>
 *
 * @since 0.0.1
 */
public final class ByteDiff implements Scalar<Integer> {
    /**
     * First byte.
     */
    private final byte first;

    /**
     * Second byte.
     */
    private final byte second;

    /**
     * Ctor.
     *
     * @param first First HashByte
     * @param second Second HashByte
     */
    public ByteDiff(final HashByte first, final HashByte second) {
        this(first.value(), second.value());
    }

    /**
     * Ctor.
     *
     * @param first First byte
     * @param second Second byte
     */
    public ByteDiff(final byte first, final byte second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Integer value() {
        return this.first ^ this.second;
    }
}
