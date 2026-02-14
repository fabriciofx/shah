/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.hash.Hash128;
import com.github.fabriciofx.shah.hash.Hash256;
import com.github.fabriciofx.shah.hash.Hash32;
import com.github.fabriciofx.shah.hash.Hash64;

/**
 * Hash diff.
 *
 * <p>Computes the bitwise XOR (exclusive OR) between two hash outputs,
 * resulting in a new hash that represents the differences between the two
 * inputs. This is useful for analyzing how small changes in the input affect
 * the hash output, which is a key property of cryptographic hash functions
 * known as the avalanche effect.</p>
 *
 * @since 0.0.1
 */
public final class HashDiff implements Scalar<Hash> {
    /**
     * First hash.
     */
    private final Hash first;

    /**
     * Second hash.
     */
    private final Hash second;

    /**
    * Ctor.
    * @param first First hash
    * @param second Second hash
    */
    public HashDiff(final Hash first, final Hash second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Hash value() {
        final byte[] fst = this.first.asBytes();
        final byte[] snd = this.second.asBytes();
        final int length = Math.min(fst.length, snd.length);
        final byte[] diffs = new byte[length];
        for (int idx = 0; idx < length; ++idx) {
            diffs[idx] = (byte) (fst[idx] ^ snd[idx]);
        }
        final Hash hash;
        switch (length) {
            case 4:
                hash = new Hash32(diffs);
                break;
            case 8:
                hash = new Hash64(diffs);
                break;
            case 16:
                hash = new Hash128(diffs);
                break;
            case 32:
                hash = new Hash256(diffs);
                break;
            default:
                throw new IllegalStateException(
                    String.format("Unsupported hash length: %d", length)
                );
        }
        return hash;
    }
}
