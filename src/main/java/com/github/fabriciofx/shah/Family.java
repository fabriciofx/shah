/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

import com.github.fabriciofx.shah.metric.Ratios;

/**
 * Family.
 *
 * <p>Represents a set of hashes.</p>
 *
 * @since 0.0.1
 */
public interface Family {
    /**
     * Add hashes to the family.
     * @param hashes A hashes
     */
    void add(Hashes hashes);

    /**
     * Retrieve an amount of hashes the family has.
     * @return The amount of hashes the family has
     */
    int count();

    /**
     * Retrieve the ratios of all family.
     * @return The ratios of all family
     */
    Ratios ratios();
}
