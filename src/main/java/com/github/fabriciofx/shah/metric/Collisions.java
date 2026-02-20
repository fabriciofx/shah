/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Metric;
import com.github.fabriciofx.shah.Scalar;
import com.github.fabriciofx.shah.hashes.Sorted;
import com.github.fabriciofx.shah.scalar.Cached;

/**
 * Collisions metric from SMHasher.
 *
 * <p>Compute the number of collisions and the collision ratio
 * (actual / expected) or 0.0 if no collisions expected. Uses the birthday
 * paradox formula for expected collisions.</p>
 *
 * <p>Given an array of hash values (as byte arrays of any width),
 * sorts them lexicographically and counts adjacent duplicates.
 * The collision ratio is computed against the expected number from
 * the birthday paradox.</p>
 *
 * <p>Expected collisions formula (sparse regime):
 * {@code expected = n * (n - 1) / (2 * 2^bits)}.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class Collisions implements Metric<Integer> {
    /**
     * Threshold for expected collisions to avoid division by small
     * numbers.
     */
    private static final double EPSILON = 0.001;

    /**
     * The hashes.
     */
    private final Hashes hashes;

    /**
     * Number of collisions.
     */
    private final Scalar<Integer> colls;

    /**
     * Ctor.
     * @param hashes The hashes
     */
    public Collisions(final Hashes hashes) {
        this.hashes = new Sorted(hashes);
        this.colls = new Cached<>(
            () -> {
                int collisions = 0;
                for (int idx = 1; idx < this.hashes.count(); ++idx) {
                    if (
                        this.hashes.item(idx).equals(this.hashes.item(idx - 1))
                    ) {
                        ++collisions;
                    }
                }
                return collisions;
            }
        );
    }

    @Override
    public Integer value() {
        return this.colls.value();
    }

    /**
     * Compute the ratio between the number of collisions and the expected
     * number of collisions.
     * @return The ratio {@code actual / expected} is returned. SMHasher
     *  considers a ratio above 2.0 as a failure
     */
    public double ratio() {
        final double ratio;
        final double expected = (double) this.hashes.count()
            * (this.hashes.count() - 1)
            / (2.0 * Math.pow(2.0, this.hashes.item(0).bits()));
        if (expected < Collisions.EPSILON) {
            ratio = this.colls.value();
        } else {
            ratio = this.colls.value() / expected;
        }
        return ratio;
    }
}
