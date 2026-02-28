/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.donothing;

import com.github.fabriciofx.shah.Func;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.hash.Hash128;
import com.github.fabriciofx.shah.hash.Hash64;

/**
 * DoNothing128.
 *
 * <p>A hash function that does nothing. It's just used as reference.</p>
 *
 * @since 0.0.1
 */
public final class DoNothing128 implements Func {
    @Override
    public Hash hash() {
        return new Hash128(0L, 0L);
    }
}
