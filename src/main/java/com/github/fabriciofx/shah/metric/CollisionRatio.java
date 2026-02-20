/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Metric;
import com.github.fabriciofx.shah.hashes.Sorted;

/**
 * Collision ratio metric from SMHasher.
 *
 * <p>Compute the collision ratio (actual / expected) or 0.0 if no collisions
 * expected. Uses the birthday paradox formula for expected collisions.</p>
 *
 * <p>Given an array of hash values (as byte arrays of any width),
 * sorts them lexicographically and counts adjacent duplicates.
 * The collision ratio is computed against the expected number from
 * the birthday paradox.</p>
 *
 * <p>Expected collisions formula (sparse regime):
 * {@code expected = n * (n - 1) / (2 * 2^bits)}.</p>
 *
 * <p>The ratio {@code actual / expected} is returned. SMHasher
 * considers a ratio above 2.0 as a failure.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class CollisionRatio implements Metric<Double> {
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
     * Ctor.
     * @param hashes The hashes
     */
    public CollisionRatio(final Hashes hashes) {
        this.hashes = new Sorted(hashes);
    }

    @Override
    public Double value() {
        final double ratio;
        int collisions = 0;
        for (int idx = 1; idx < this.hashes.count(); ++idx) {
            if (this.hashes.item(idx).equals(this.hashes.item(idx - 1))) {
                ++collisions;
            }
        }
        final double expected = (double) this.hashes.count()
            * (this.hashes.count() - 1)
            / (2.0 * Math.pow(2.0, this.hashes.item(0).bits()));
        if (expected < CollisionRatio.EPSILON) {
            ratio = collisions;
        } else {
            ratio = collisions / expected;
        }
        return ratio;
    }
}
