/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.appleby;

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
 * Tests for {@link Murmur3Hash32}.
 *
 * <p>These tests adapt the SMHasher test suite to measure the
 * distribution quality of the MurmurHash3 32-bit implementation.
 * MurmurHash3 is expected to pass all SMHasher tests with strict
 * thresholds.</p>
 *
 * <p>For keyset collision tests with small expected collision counts
 * (around 1), ratios can appear high due to Poisson variance even
 * for a perfect hash. Thresholds of 10.0 allow for this natural
 * statistical variation.</p>
 *
 * @since 0.0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.TooManyMethods"})
final class Murmur3Hash32Test {
    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the murmur3 hash of an empty string",
            () -> new Murmur3Hash32(new KeyOf(""), 0).hash().asString(),
            new IsText("00000000")
        ).affirm();
    }

    @Test
    void evaluateCharacterA() {
        new Assertion<>(
            "must evaluate the murmur3 hash of character 'a'",
            () -> new Murmur3Hash32(new KeyOf("a"), 0).hash().asString(),
            new IsText("b269253c")
        ).affirm();
    }

    @Test
    void evaluateAllCharacters() {
        new Assertion<>(
            "must evaluate the murmur3 hash of all characters",
            () -> new Murmur3Hash32(
                new KeyOf(
                    "The quick brown fox jumps over the lazy dog"
                ),
                0
            ).hash().asString(),
            new IsText("23f74f2e")
        ).affirm();
    }

    @Test
    void evaluateHello() {
        new Assertion<>(
            "must evaluate the murmur3 hash of hello string",
            () -> new Murmur3Hash32(new KeyOf("hello"), 0).hash().asString(),
            new IsText("47fa8b24")
        ).affirm();
    }

    @Test
    void evaluateWithSeed() {
        new Assertion<>(
            "must evaluate the murmur3 hash with a non-zero seed",
            () -> new Murmur3Hash32(new KeyOf("hello"), 42).hash().asString(),
            new IsText("e1d2dbe2")
        ).affirm();
    }

    @Test
    void evaluateExactlyFourBytes() {
        new Assertion<>(
            "must evaluate the murmur3 hash of a 4-byte string",
            () -> new Murmur3Hash32(new KeyOf("test"), 0).hash().asString(),
            new IsText("13d26bba")
        ).affirm();
    }

    @Test
    void evaluateEightBytes() {
        new Assertion<>(
            "must evaluate the murmur3 hash of an 8-byte string",
            () -> new Murmur3Hash32(new KeyOf("testtest"), 0).hash().asString(),
            new IsText("f3b1232b")
        ).affirm();
    }

    @Test
    void passesAvalancheWithFourBytesKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche test with 4-byte keys",
            new AvalancheTest(
                (key, seed) -> new Murmur3Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                4,
                12_345L,
                54_321L,
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithEightByteKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche test with 8-byte keys",
            new AvalancheTest(
                (key, seed) -> new Murmur3Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                8,
                12_345L,
                54_321L,
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithSixteenBytesKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche test with 16-byte keys",
            new AvalancheTest(
                (key, seed) -> new Murmur3Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                16,
                12_345L,
                54_321L,
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "murmur3 must pass collision test",
            new CollisionTest(
                (key, seed) -> new Murmur3Hash32(
                    key,
                    Long.hashCode(seed)
                ).hash(),
                16,
                67_890L,
                12_345L,
                1_000_000
            ).metric(),
            new IsLessThan(2.0, "collision ratio")
        ).affirm();
    }

    @Test
    void passesDistributionTest() {
        new Assertion<>(
            "murmur3 must pass distribution test",
            new DistributionTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                1_000_000,
                16,
                67_890L
            ).metric(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void passesBicWithFourBytesKeys() {
        new Assertion<>(
            "murmur3 must pass BIC test with 4-byte keys",
            new BicTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4,
                100_000,
                11_111L
            ).metric(),
            new IsLessThan(0.05, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "murmur3 must pass sanity test",
            new SanityTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                32
            ).metric(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "murmur3 must pass zeroes test",
            new ZeroesTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                204_800
            ).metric(),
            new IsLessThan(2.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "murmur3 must pass cyclic key test",
            new CyclicKeyTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4,
                8
            ).metric(),
            new IsLessThan(2.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "murmur3 must pass two-bytes test",
            new TwoBytesTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4
            ).metric(),
            new IsLessThan(2.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "murmur3 must pass sparse key test",
            new SparseKeyTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                32,
                3
            ).metric(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "murmur3 must pass permutation test",
            new PermutationTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).metric(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "murmur3 must pass windowed key test",
            new WindowedKeyTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4,
                12
            ).metric(),
            new IsLessThan(10.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "murmur3 must pass text test",
            new TextTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                "Foo".getBytes(StandardCharsets.UTF_8),
                "Bar".getBytes(StandardCharsets.UTF_8),
                4
            ).metric(),
            new IsLessThan(10.0, "text collision ratio")
        ).affirm();
    }

    @Test
    void passesDifferentialTest() {
        new Assertion<>(
            "murmur3 must pass differential test",
            new DifferentialTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4,
                100_000,
                54_321L
            ).metric(),
            new IsLessThan(10.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "murmur3 must pass diff distribution test",
            new DiffDistTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                4,
                100_000,
                54_321L
            ).metric(),
            new IsLessThan(0.01, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesSeedTest() {
        new Assertion<>(
            "murmur3 must pass seed test",
            new SeedTest(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                100_000
            ).metric(),
            new IsLessThan(2.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "murmur3 must pass appended zeroes test",
            new AppendedZeroesTest(
                key -> new Murmur3Hash32(key, 0).hash()
            ).metric(),
            new IsLessThan(0.01, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesVerificationTest() {
        new Assertion<>(
            "murmur3 must produce correct verification code",
            new VerificationTest(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric(),
            new IsEqual<>(0xB0F57EE3L)
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "murmur3 must pass perlin noise test",
            new PerlinNoiseTest(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "murmur3 must pass PRNG test",
            new PrngTest(
                key -> new Murmur3Hash32(key, 0).hash()
            ).metric(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "murmur3 must pass words test",
            new WordsTest(
                key -> new Murmur3Hash32(key, 0).hash(),
                100_000,
                2,
                10
            ).metric(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "murmur3 must pass moment chi-squared test",
            new MomentChi2Test(
                key -> new Murmur3Hash32(key, 0).hash()
            ).metric(),
            new IsLessThan(500.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "murmur3 must pass bad seeds test",
            new BadSeedsTest(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }

    @Test
    void computeSpeedBenchmark() {
        new Assertion<>(
            "must compute murmur32 speed benchmark",
            new SpeedBenchmark(
                (key, seed) -> new Murmur3Hash32(
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
            "must compute murmur32 small keys speed benchmark",
            new SpeedBenchmark(
                (key, seed) -> new Murmur3Hash32(
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
            "must perform murmur3 hashmap benchmark",
            new HashmapBenchmark(
                key -> new Murmur3Hash32(key, 0).hash()
            ).run(),
            new IsLessThan(100_000.0, "hashmap ns/op")
        ).affirm();
    }
}
