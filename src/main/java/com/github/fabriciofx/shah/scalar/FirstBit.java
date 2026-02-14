/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;

/**
 * First bit.
 *
 * @since 0.0.1
 */
public final class FirstBit implements Scalar<Integer> {
    /**
     * Value.
     */
    private final int val;

    /**
     * Bit index (0-7).
     */
    private final int index;

    /**
     * Ctor.
     * @param diff Byte difference (XOR) between two bytes
     * @param index Bit index (0-7)
     */
    public FirstBit(final ByteDiff diff, final int index) {
        this(diff.value(), index);
    }

    /**
     * Ctor.
     * @param value Byte value
     * @param index Bit index (0-7)
     */
    public FirstBit(final int value, final int index) {
        this.val = value;
        this.index = index;
    }

    @Override
    public Integer value() {
        return (this.val >> (this.index & 7)) & 1;
    }
}
