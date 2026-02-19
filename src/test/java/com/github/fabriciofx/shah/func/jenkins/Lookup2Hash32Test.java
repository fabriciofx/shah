/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.benchmark.HashmapBenchmark;
import com.github.fabriciofx.shah.benchmark.SpeedBenchmark;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.test.AppendedZeroesTest;
import com.github.fabriciofx.shah.test.AvalancheTest;
import com.github.fabriciofx.shah.test.BadSeedsTest;
import com.github.fabriciofx.shah.test.BicTest;
import com.github.fabriciofx.shah.test.CollisionTest;
import com.github.fabriciofx.shah.test.CyclicKeyTest;
import com.github.fabriciofx.shah.test.DiffDistTest;
import com.github.fabriciofx.shah.test.DifferentialTest;
import com.github.fabriciofx.shah.test.DistributionTest;
import com.github.fabriciofx.shah.test.MomentChi2Test;
import com.github.fabriciofx.shah.test.PerlinNoiseTest;
import com.github.fabriciofx.shah.test.PermutationTest;
import com.github.fabriciofx.shah.test.PrngTest;
import com.github.fabriciofx.shah.test.SanityTest;
import com.github.fabriciofx.shah.test.SeedTest;
import com.github.fabriciofx.shah.test.SparseKeyTest;
import com.github.fabriciofx.shah.test.TextTest;
import com.github.fabriciofx.shah.test.TwoBytesTest;
import com.github.fabriciofx.shah.test.VerificationTest;
import com.github.fabriciofx.shah.test.WindowedKeyTest;
import com.github.fabriciofx.shah.test.WordsTest;
import com.github.fabriciofx.shah.test.ZeroesTest;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link Lookup2Hash32}.
 *
 * <p>Note: Lookup2 is an older hash function that fails SMHasher's
 * strict avalanche test (bias threshold 1%) for small key sizes and
 * the BIC test (bias threshold 5%). The thresholds here are relaxed
 * to reflect the known limitations of this func.</p>
 *
 * @since 0.0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.TooManyMethods"})
final class Lookup2Hash32Test {
    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the lookup2 hash of an empty string",
            () -> new Lookup2Hash32(new KeyOf(""), 0).hash().asString(),
            new IsText("0dd149bd")
        ).affirm();
    }

    @Test
    void evaluateCharacterA() {
        new Assertion<>(
            "must evaluate the lookup2 hash of character 'a'",
            () -> new Lookup2Hash32(new KeyOf("a"), 0).hash().asString(),
            new IsText("18c8ee29")
        ).affirm();
    }

    @Test
    void evaluateHello() {
        new Assertion<>(
            "must evaluate the lookup2 hash of hello string",
            () -> new Lookup2Hash32(new KeyOf("hello"), 0).hash().asString(),
            new IsText("9e3906b7")
        ).affirm();
    }

    @Test
    void evaluateAllCharacters() {
        new Assertion<>(
            "must evaluate the lookup2 hash of all characters",
            () -> new Lookup2Hash32(
                new KeyOf(
                    "The quick brown fox jumps over the lazy dog"
                ),
                0
            ).hash().asString(),
            new IsText("de5815fc")
        ).affirm();
    }

    @Test
    void avalancheWithFourBytesKeys() {
        new Assertion<>(
            "lookup2 avalanche bias with 4-byte keys must be below 30%",
            new AvalancheTest(
                (key, seed) -> new Lookup2Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                4,
                12_345L,
                54_321L,
                500_000
            ).value().bias().max(),
            new IsLessThan(0.30, "avalanche bias")
        ).affirm();
    }

    @Test
    void avalancheWithEightBytesKeys() {
        new Assertion<>(
            "lookup2 avalanche bias with 8-byte keys must be below 40%",
            new AvalancheTest(
                (key, seed) -> new Lookup2Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                8,
                12_345L,
                54_321L,
                500_000
            ).value().bias().max(),
            new IsLessThan(0.40, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "lookup2 must pass collision test",
            new CollisionTest(
                (key, seed) -> new Lookup2Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                16,
                67_890L,
                12_345L,
                1_000_000
            ).value(),
            new IsLessThan(2.0, "collision ratio")
        ).affirm();
    }

    @Test
    void passesDistributionTest() {
        new Assertion<>(
            "lookup2 must pass distribution test",
            new DistributionTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                1_000_000,
                16,
                67_890L
            ).value(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void bicWithFourBytesKeys() {
        new Assertion<>(
            "lookup2 BIC bias with 4-byte keys must be below 60%",
            new BicTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4,
                100_000,
                11_111L
            ).value(),
            new IsLessThan(0.60, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "lookup2 must pass sanity test",
            new SanityTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                32
            ).value(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "lookup2 must pass zeroes test",
            new ZeroesTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                204_800
            ).value(),
            new IsLessThan(2.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "lookup2 must pass cyclic key test",
            new CyclicKeyTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4,
                8
            ).value(),
            new IsLessThan(2.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "lookup2 must pass two-bytes test",
            new TwoBytesTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4
            ).value(),
            new IsLessThan(2.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "lookup2 must pass sparse key test",
            new SparseKeyTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                32,
                3
            ).value(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "lookup2 must pass permutation test",
            new PermutationTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).value(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "lookup2 must pass windowed key test",
            new WindowedKeyTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4,
                12
            ).value(),
            new IsLessThan(10.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "lookup2 must pass text test",
            new TextTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                "Foo".getBytes(StandardCharsets.UTF_8),
                "Bar".getBytes(StandardCharsets.UTF_8),
                4
            ).value(),
            new IsLessThan(10.0, "text collision ratio")
        ).affirm();
    }

    @Test
    void passesDifferentialTest() {
        new Assertion<>(
            "lookup2 must pass differential test",
            new DifferentialTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4,
                100_000,
                54_321L
            ).value(),
            new IsLessThan(10.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "lookup2 diff dist test (known weakness with small keys)",
            new DiffDistTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                4,
                100_000,
                54_321L
            ).value(),
            new IsLessThan(0.20, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesSeedTest() {
        new Assertion<>(
            "lookup2 must pass seed test",
            new SeedTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                100_000
            ).value(),
            new IsLessThan(3.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "lookup2 must pass appended zeroes test",
            new AppendedZeroesTest(
                key -> new Lookup2Hash32(key, 0).hash()
            ).value(),
            new IsLessThan(0.01, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesVerificationTest() {
        new Assertion<>(
            "lookup2 must produce correct verification code",
            new VerificationTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).value(),
            new IsEqual<>(0x8B7FB2D2L)
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "lookup2 must pass perlin noise test",
            new PerlinNoiseTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).value(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "lookup2 must pass PRNG test",
            new PrngTest(
                key -> new Lookup2Hash32(key, 0).hash()
            ).value(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "lookup2 must pass words test",
            new WordsTest(
                key -> new Lookup2Hash32(key, 0).hash(),
                100_000,
                2,
                10
            ).value(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "lookup2 moment chi-squared (known weakness, fails SMHasher)",
            new MomentChi2Test(
                key -> new Lookup2Hash32(key, 0).hash()
            ).value(),
            new IsLessThan(1000.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "lookup2 must pass bad seeds test",
            new BadSeedsTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).value(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }

    @Test
    void computeSpeedBenchmark() {
        new Assertion<>(
            "must compute lookup2 speed benchmark",
            new SpeedBenchmark(
                (key, seed) -> new Lookup2Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash()
            ).run(),
            new IsLessThan(100_000_000.0, "bulk ns/op")
        ).affirm();
    }

    @Test
    void computeSmallKeysSpeedBenchmark() {
        new Assertion<>(
            "must compute lookup2 small keys speed benchmark",
            new SpeedBenchmark(
                (key, seed) -> new Lookup2Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                4
            ).run(),
            new IsLessThan(100_000.0, "small key ns/op")
        ).affirm();
    }

    @Test
    void performHashmapBenchmark() {
        new Assertion<>(
            "must perform lookup2 hashmap benchmark",
            new HashmapBenchmark(
                key -> new Lookup2Hash32(key, 0).hash()
            ).run(),
            new IsLessThan(100_000.0, "hashmap ns/op")
        ).affirm();
    }
}
