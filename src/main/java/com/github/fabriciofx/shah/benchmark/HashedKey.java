/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.benchmark;

/**
 * HashedKey.
 *
 * <p>A wrapper key that delegates {@code hashCode()} to the hash function
 * under test, so that {@link java.util.HashMap} uses our hash function instead
 * of Java's default.</p>
 *
 * @since 0.0.1
 */
final class HashedKey {
    /**
     * The computed hash code.
     */
    private final int code;

    /**
     * The original string value.
     */
    private final String text;

    /**
     * Ctor.
     * @param code The pre-computed hash code
     * @param text The key text
     */
    HashedKey(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof HashedKey
            && this.text.equals(HashedKey.class.cast(other).text);
    }

    @Override
    public int hashCode() {
        return this.code;
    }
}
