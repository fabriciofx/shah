/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

import java.util.Random;

/**
 * Seed.
 * @since 0.0.1
 */
public interface Seed {
    /**
     * Retrieve the seed as an array of bytes.
     * @return The array of bytes that represents the seed
     */
    byte[] asBytes();

    /**
     * Retrieve the seed as an integer.
     * @return The integer that represents the seed
     */
    int asInt();

    /**
     * Retrieve the seed as a long.
     * @return The long that represents the seed
     */
    long asLong();

    /**
     * Create a {@link Random} from seed.
     * @return A {@link Random}
     */
    Random random();
}
