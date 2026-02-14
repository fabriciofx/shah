/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;

/**
 * Bit difference (XOR) between two integers.
 *
 * <p>Computes the bitwise exclusive OR (XOR) of two integers, which
 * indicates the positions where their bits differ. This is useful for
 * measuring how many bits change in a hash output when the input key
 * changes, which is a key aspect of hash function quality.</p>
 *
 * @since 0.0.1
 */
public final class BitDiff implements Scalar<Integer> {
    /**
     * First.
     */
    private final int first;

    /**
     * Second.
     */
    private final int second;

    /**
     * Ctor.
     * @param first First integer
     * @param second Second integer
     */
    public BitDiff(final int first, final int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Integer value() {
        return this.first ^ this.second;
    }
}
