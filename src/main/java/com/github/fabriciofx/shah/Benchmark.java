/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah;

/**
 * Benchmark.
 * @param <T> Type of result after run the benchmark
 * @since 0.0.1
 */
@FunctionalInterface
public interface Benchmark<T> {
    /**
     * Run the benchmark.
     * @return The result
     */
    T run();
}
