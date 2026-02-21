/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Metric;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Ratios.
 *
 * <p>Compute collision ratios among hashes.</p>
 *
 * @since 0.0.1
 */
public final class Ratios implements Metric<List<Double>> {
    /**
     * List of Hashes.
     */
    private final List<Hashes> items;

    /**
     * Ctor.
     */
    public Ratios() {
        this(new LinkedList<>());
    }

    /**
     * Ctor.
     * @param items A list of hashes
     */
    public Ratios(final List<Hashes> items) {
        this.items = items;
    }

    @Override
    public List<Double> value() {
        final List<Double> values = new ArrayList<>(this.items.size());
        for (final Hashes item : this.items) {
            values.add(new Collisions(item).ratio());
        }
        return values;
    }

    /**
     * Add a hashes.
     * @param hashes A hashes
     */
    public void add(final Hashes hashes) {
        this.items.add(hashes);
    }

    /**
     * Compute the worst ratio among hashes collision ratios.
     * @return The worst ratio
     */
    public double worst() {
        return this.value()
            .stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0.0);
    }
}
