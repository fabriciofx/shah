/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Metric;

/**
 * Distribution (window) score metric from SMHasher.
 *
 * <p>Compute the worst distribution score.</p>
 *
 * <p>Given an array of hash values (as byte arrays of any width),
 * buckets them by every N-bit window position and checks if the
 * distribution is uniform. It uses a normalized score based on the
 * root-mean-square of bucket counts.</p>
 *
 * <p>Score formula:
 * {@code score = 1.0 - ((k^2 - 1) / (n * rms^2 - k)) / n}
 * where {@code k} is the number of keys, {@code n} is the number of
 * bins, and {@code rms} is the root-mean-square of bin counts.</p>
 *
 * <p>A score of 0.0 means perfect distribution. SMHasher considers
 * a score above 1% (0.01) as a failure. The worst score across all
 * window positions and widths is returned.</p>
 *
 * @see <a href="https://github.com/aappleby/smhasher">SMHasher</a>
 * @since 0.0.1
 * @checkstyle NestedForDepthCheck (200 lines)
 */
public final class DistributionScore implements Metric {
    /**
     * Small value to check error.
     */
    private static final double EPSILON = 1e-10;

    /**
     * Hashes to analyze.
     */
    private final Hashes hashes;

    /**
     * Ctor.
     * @param hashes Hash to analyze
     */
    public DistributionScore(final Hashes hashes) {
        this.hashes = hashes;
    }

    @Override
    public double value() {
        final int bits = this.hashes.item(0).bits();
        double worst = 0.0;
        for (int start = 0; start < bits; ++start) {
            final int max = Math.min(
                16,
                DistributionScore.maxWidth(this.hashes.count())
            );
            for (int width = max; width >= 8; --width) {
                final int nbins = 1 << width;
                final int mask = nbins - 1;
                final int[] bins = new int[nbins];
                for (final Hash hash : this.hashes) {
                    final int window = DistributionScore.extract(
                        hash, start, bits
                    );
                    bins[window & mask] += 1;
                }
                final double score = DistributionScore.score(
                    bins, nbins, this.hashes.count()
                );
                if (score > worst) {
                    worst = score;
                }
            }
        }
        return worst;
    }

    /**
     * Extract bits from a byte array at a circular bit offset.
     * Reads up to 16 bits starting at the given bit position,
     * wrapping around if necessary.
     * @param hash The hash value as byte array
     * @param start Starting bit position
     * @param total Total number of bits in the hash
     * @return The extracted bits as an int
     */
    private static int extract(
        final Hash hash,
        final int start,
        final int total
    ) {
        final byte[] bytes = hash.asBytes();
        int result = 0;
        for (int bit = 0; bit < 16; ++bit) {
            final int pos = (start + bit) % total;
            final int bte = (bytes[pos >> 3] >> (pos & 7)) & 1;
            result |= bte << bit;
        }
        return result;
    }

    /**
     * Calculate the maximum window width for the given key count.
     * The number of bins must not exceed nkeys / 5.
     * @param nkeys Number of keys
     * @return The maximum window width
     */
    private static int maxWidth(final int nkeys) {
        int width = 0;
        while ((1 << (width + 1)) <= nkeys / 5) {
            width += 1;
        }
        return width;
    }

    /**
     * Calculate the distribution score for the given bins.
     *
     * @param bins The bin counts
     * @param bincount Number of bins
     * @param keycount Number of keys
     * @return Score where 0.0 is perfect, 1.0 is worst
     */
    private static double score(
        final int[] bins,
        final int bincount,
        final int keycount
    ) {
        final double score;
        final double nbins = bincount;
        final double nkeys = keycount;
        double sumsq = 0.0;
        for (int idx = 0; idx < bincount; ++idx) {
            sumsq += (double) bins[idx] * bins[idx];
        }
        final double rms = Math.sqrt(sumsq / nbins);
        final double denominator = nbins * rms * rms - nkeys;
        if (Math.abs(denominator) < DistributionScore.EPSILON) {
            score = 0.0;
        } else {
            final double factor = (nkeys * nkeys - 1.0) / denominator;
            score = 1.0 - factor / nbins;
        }
        return score;
    }
}
