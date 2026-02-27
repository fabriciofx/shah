/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.metric;

import com.github.fabriciofx.shah.Metric;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Ratios.
 * @since 0.0.1
 */
public final class Ratios implements Metric<List<Ratio>> {
    /**
     * List of Ratio.
     */
    private final List<Ratio> items;

    /**
     * Ctor.
     */
    public Ratios() {
        this(new LinkedList<>());
    }

    /**
     * Ctor.
     * @param items A list of ratio
     */
    public Ratios(final List<Ratio> items) {
        this.items = items;
    }

    /**
     * Add a ratio to ratios.
     * @param ratio A ratio
     */
    public void add(final Ratio ratio) {
        this.items.add(ratio);
    }

    @Override
    public List<Ratio> value() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * Compute the worst ratio.
     * @return The worst ratio
     */
    public Ratio worst() {
        Ratio worst = this.items.get(0);
        for (int idx = 1; idx < this.items.size(); ++idx) {
            final Ratio item = this.items.get(idx);
            if (item.value() > worst.value()) {
                worst = item;
            }
        }
        return worst;
    }
}
