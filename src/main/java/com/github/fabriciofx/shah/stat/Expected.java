/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.stat;

import com.github.fabriciofx.shah.Scalar;

/**
 * Expected.
 *
 * <p>Compute the expected value according birthday paradox. The formula is
 * {@code expected = count * (count - 1) / (2 * 2^bits)} where n is the number
 * of hashes and bits is the size of hash in bits.</p>
 * @since 0.0.1
 */
public final class Expected implements Scalar<Double> {
    /**
     * The amount of hashes.
     */
    private final int count;

    /**
     * The size of the hash in bits.
     */
    private final int bits;

    /**
     * Ctor.
     * @param count The amount of hashes
     * @param bits The size of the hash in bits
     */
    public Expected(final int count, final int bits) {
        this.count = count;
        this.bits = bits;
    }

    @Override
    public Double value() {
        return (double) this.count * (this.count - 1)
            / (2.0 * Math.pow(2.0, this.bits));
    }
}
