/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.collection;

import com.github.fabriciofx.shah.Scalar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filtered collection of values.
 * @param <E> Type of the values
 * @since 0.0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Filtered<E extends Number> implements Collection<E> {
    /**
     * Filtered items.
     */
    private final Scalar<List<E>> items;

    /**
     * Ctor.
     * @param items Items to be filtered
     * @param criterion Filter criterion
     */
    public Filtered(final Collection<E> items, final Predicate<E> criterion) {
        this(
            () -> items.stream().filter(criterion).collect(Collectors.toList())
        );
    }

    /**
     * Ctor.
     * @param items Filtered items
     */
    public Filtered(final Scalar<List<E>> items) {
        this.items = items;
    }

    @Override
    public int size() {
        return this.items.value().size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.value().isEmpty();
    }

    @Override
    public boolean contains(final Object obj) {
        return this.items.value().contains(obj);
    }

    @Override
    public Iterator<E> iterator() {
        return this.items.value().iterator();
    }

    @Override
    public Object[] toArray() {
        return this.items.value().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] array) {
        return this.items.value().toArray(array);
    }

    @Override
    public boolean add(final E element) {
        return this.items.value().add(element);
    }

    @Override
    public boolean remove(final Object obj) {
        return this.items.value().remove(obj);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.items.value().containsAll(collection);
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.items.value().addAll(collection);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return this.items.value().removeAll(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return this.items.value().retainAll(collection);
    }

    @Override
    public void clear() {
        this.items.value().clear();
    }
}
