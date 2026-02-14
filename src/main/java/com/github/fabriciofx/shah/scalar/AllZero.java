/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;

/**
 * AllZero.
 *
 * <p>Checks if all bytes of a hash are zero.</p>
 *
 * @since 0.0.1
 */
public final class AllZero implements Scalar<Boolean> {
    /**
     * Hash to be checked.
     */
    private final Hash hash;

    /**
     * Ctor.
     *
     * @param hash Hash to be checked
     */
    public AllZero(final Hash hash) {
        this.hash = hash;
    }

    @Override
    public Boolean value() {
        boolean zero = true;
        for (final byte bte : this.hash.asBytes()) {
            if (bte != 0) {
                zero = false;
                break;
            }
        }
        return zero;
    }
}
