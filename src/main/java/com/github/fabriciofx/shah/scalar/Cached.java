/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.scalar;

import com.github.fabriciofx.shah.Scalar;
import java.util.ArrayList;
import java.util.List;

/**
 * Cached.
 *
 * <p>Cache the value of a scalar for subsequent calls.</p>
 *
 * @param <T> Type of the scalar value
 * @since 0.0.1
 */
public final class Cached<T> implements Scalar<T> {
    /**
     * Cache.
     */
    private final List<T> cache;

    /**
     * Scalar to be cached.
     */
    private final Scalar<T> scalar;

    /**
     * Ctor.
     *
     * @param scalar Scalar to be cached
     */
    public Cached(final Scalar<T> scalar) {
        this.cache = new ArrayList<>(1);
        this.scalar = scalar;
    }

    @Override
    public T value() {
        if (this.cache.isEmpty()) {
            this.cache.add(this.scalar.value());
        }
        return this.cache.get(0);
    }
}
