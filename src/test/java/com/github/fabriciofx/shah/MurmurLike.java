/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

import com.github.fabriciofx.shah.hash.Hash64;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * MurmurLike.
 * @since 0.0.1
 */
public final class MurmurLike implements Func {

    /**
     * Key.
     */
    private final Key key;

    /**
     * Ctor.
     * @param key The key
     */
    public MurmurLike(final Key key) {
        this.key = key;
    }

    @Override
    public Hash hash() {
        long hash = ByteBuffer
            .wrap(this.key.asBytes())
            .order(ByteOrder.LITTLE_ENDIAN)
            .getLong();
        hash ^= hash >>> 33;
        hash *= 0xFF51AFD7ED558CCDL;
        hash ^= hash >>> 33;
        hash *= 0xC4CEB9FE1A85EC53L;
        hash ^= hash >>> 33;
        return new Hash64(hash);
    }
}
