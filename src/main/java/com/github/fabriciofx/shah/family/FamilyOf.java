/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.family;

import com.github.fabriciofx.shah.Family;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.metric.Collisions;
import com.github.fabriciofx.shah.metric.Ratios;
import java.util.LinkedList;
import java.util.List;

/**
 * FamilyOf.
 *
 * <p>Represents a set of hashes.</p>
 *
 * @since 0.0.1
 */
public final class FamilyOf implements Family {
    /**
     * List of hashes.
     */
    private final List<Hashes> members;

    /**
     * Ctor.
     */
    public FamilyOf() {
        this(new LinkedList<>());
    }

    /**
     * Ctor.
     * @param members A list of hashes
     */
    public FamilyOf(final List<Hashes> members) {
        this.members = members;
    }

    @Override
    public void add(final Hashes hashes) {
        this.members.add(hashes);
    }

    @Override
    public int count() {
        return this.members.size();
    }

    @Override
    public Ratios ratios() {
        final Ratios ratios = new Ratios();
        for (final Hashes hashes : this.members) {
            ratios.add(new Collisions(hashes).ratio());
        }
        return ratios;
    }
}
