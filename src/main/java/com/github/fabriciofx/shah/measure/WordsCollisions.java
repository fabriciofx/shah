/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.measure;

import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Hashes;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Measure;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.hashes.HashesOf;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.metric.Collisions;
import java.util.function.BiFunction;

/**
 * Words measure from SMHasher.
 *
 * <p>Measures the collisions ratio of random words hashes. Returns the
 * collisions metric among all generated hashes.</p>
 *
 * @see <a href="https://github.com/rurban/smhasher">SMHasher</a>
 * @since 0.0.1
 */
public final class WordsCollisions implements Measure<Collisions> {

    /**
     * The hash function under measure.
     */
    private final BiFunction<Key, Seed, Hash> func;

    /**
     * Key's seed.
     */
    private final Seed seed;

    /**
     * Words.
     */
    private final Words words;

    /**
     * Ctor.
     *
     * @param func The hash function under measure
     * @param seed The key's seed
     * @param words The words
     */
    public WordsCollisions(
        final BiFunction<Key, Seed, Hash> func,
        final Seed seed,
        final Words words
    ) {
        this.func = func;
        this.seed = seed;
        this.words = words;
    }

    @Override
    public Collisions metric() {
        final Hashes hashes = new HashesOf();
        for (final String word : this.words) {
            hashes.add(this.func.apply(new KeyOf(word), this.seed));
        }
        return new Collisions(hashes);
    }
}
