/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.donothing;

import com.github.fabriciofx.shah.Func;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * DoNothing32Benchmark.
 * @since 0.0.1
 */
@State(Scope.Thread)
public class DoNothing32Benchmark {
    private static final int SIZE = 128 * 1024;
    private Func func;

    @Setup(Level.Trial)
    public void setup() {
        this.func = new DoNothing32();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void hashesPerSecond(final Blackhole blackhole) {
        blackhole.consume(this.func.hash());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @OperationsPerInvocation(DoNothing32Benchmark.SIZE)
    public void bytesPerSecond(final Blackhole blackhole) {
        blackhole.consume(this.func.hash());
    }
}
