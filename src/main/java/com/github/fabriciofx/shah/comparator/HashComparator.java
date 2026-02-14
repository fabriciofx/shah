/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.comparator;

import com.github.fabriciofx.shah.Hash;
import java.util.Comparator;

/**
 * Comparator for {@link Hash} objects.
 * @since 0.0.1
 */
public final class HashComparator implements Comparator<Hash> {
    @Override
    public int compare(final Hash first, final Hash second) {
        final byte[] fst = first.asBytes();
        final byte[] snd = second.asBytes();
        final int len = Math.min(fst.length, snd.length);
        int cmp = 0;
        for (int idx = 0; idx < len; ++idx) {
            cmp = (fst[idx] & 0xFF) - (snd[idx] & 0xFF);
            if (cmp != 0) {
                break;
            }
        }
        if (cmp == 0) {
            cmp = fst.length - snd.length;
        }
        return cmp;
    }
}
