/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * BenchmarkReport.
 *
 * @since 0.0.1
 */
public final class BenchmarkReport {
    public static void main(String[] args) throws IOException {
        final Path root = Path.of(System.getProperty("user.dir"))
            .resolve("target");
        final Path input = root.resolve("benchmark-results.csv");
        final Path output = root.resolve("benchmark-report.md");
        final StringBuilder markdown = new StringBuilder();
        markdown.append("## Benchmark Results\n\n");
        markdown.append("| Hash function | Size (in KiB) | MiB/s ± error | hash/s ± error |\n");
        markdown.append("|:--------------|:-------------:|:-------------:|:--------------:|\n");
        try (Stream<String> stream = Files.lines(input)) {
            final Iterator<String> iter = stream
                .skip(1)
                .map(String::trim)
                .filter(line -> !line.startsWith("#"))
                .iterator();
            while (iter.hasNext()) {
                final String first = iter.next();
                if (!iter.hasNext()) {
                    break;
                }
                final String second = iter.next();
                final String joined = String.join(",", first, second);
                final String[] parts = joined.split(
                    ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"
                );
                final String name = parts[0].replaceAll(
                    ".*\\.([A-Za-z0-9]+)Benchmark.*",
                    "$1"
                );
                final int size = 128;
                final String mibsec = String.format(
                    "%.2f ± %.2f",
                    Double.parseDouble(parts[4]) / (1024 * 1024),
                    Double.parseDouble(parts[5]) / (1024 * 1024)
                );
                final String hashsec = String.format(
                    "%.2f ± %.2f",
                    Double.parseDouble(parts[11]),
                    Double.parseDouble(parts[12])
                );
                markdown.append("| ")
                    .append(name)
                    .append(" | ")
                    .append(size)
                    .append(" | ")
                    .append(mibsec)
                    .append(" | ")
                    .append(hashsec)
                    .append(" |\n");
            }
        }
        Files.writeString(output, markdown.toString());
    }
}
