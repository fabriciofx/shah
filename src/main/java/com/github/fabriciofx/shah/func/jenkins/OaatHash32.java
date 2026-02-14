/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.Func;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.hash.Hash32;

/**
 * One At A Time Hash (OAAT).
 *
 * @see <a
 * href="https://en.wikipedia.org/wiki/Jenkins_hash_function#One_at_a_time">
 * Jenkins One At A Time Hash</a>
 *
 * @since 0.0.1
 * @checkstyle UnnecessaryParenthesesCheck (100 lines)
 */
public final class OaatHash32 implements Func {
    /**
     * The key to be hashed.
     */
    private final Key key;

    /**
     * Ctor.
     * @param key Key to be hashed
     */
    public OaatHash32(final Key key) {
        this.key = key;
    }

    @Override
    public Hash hash() {
        int hash = 0;
        for (final byte datum : this.key.asBytes()) {
            hash += (datum & 0xff);
            hash += (hash << 10);
            hash ^= (hash >>> 6);
        }
        hash += (hash << 3);
        hash ^= (hash >>> 11);
        hash += (hash << 15);
        return new Hash32(hash);
    }
}
