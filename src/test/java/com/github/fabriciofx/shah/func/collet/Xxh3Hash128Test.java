/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.collet;

import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.measure.AppendedZeroes;
import com.github.fabriciofx.shah.measure.Avalanche;
import com.github.fabriciofx.shah.measure.BadSeeds;
import com.github.fabriciofx.shah.measure.Bic;
import com.github.fabriciofx.shah.measure.Collision;
import com.github.fabriciofx.shah.measure.CyclicKey;
import com.github.fabriciofx.shah.measure.DiffDistribution;
import com.github.fabriciofx.shah.measure.Differential;
import com.github.fabriciofx.shah.measure.Distribution;
import com.github.fabriciofx.shah.measure.MomentChiSquared;
import com.github.fabriciofx.shah.measure.PerlinNoise;
import com.github.fabriciofx.shah.measure.PermutationKey;
import com.github.fabriciofx.shah.measure.Prng;
import com.github.fabriciofx.shah.measure.Sanity;
import com.github.fabriciofx.shah.measure.SeedsCollisions;
import com.github.fabriciofx.shah.measure.SparseKey;
import com.github.fabriciofx.shah.measure.TextKey;
import com.github.fabriciofx.shah.measure.TwoBytes;
import com.github.fabriciofx.shah.measure.Verification;
import com.github.fabriciofx.shah.measure.WindowedKey;
import com.github.fabriciofx.shah.measure.WordsCollisions;
import com.github.fabriciofx.shah.measure.ZeroesKey;
import com.github.fabriciofx.shah.seed.Seed64;
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link Xxh3Hash128}.
 *
 * <p>Expected hashes come from the reference xxHash v0.8 library and
 * are written in the canonical (big-endian) form printed by
 * {@code xxh128sum}. Each one exercises a different input length
 * branch of the algorithm.</p>
 *
 * <p>The measures adapt the SMHasher test suite to measure the
 * distribution quality of the XXH3 128-bit implementation. XXH3 has a
 * known SMHasher3 weakness: it fails the BIC measure for short keys,
 * where SMHasher3 reports a bias of 0.79 with 4-byte keys.</p>
 *
 * @since 0.0.1
 * @checkstyle ClassFanOutComplexityCheck (600 lines)
 */
final class Xxh3Hash128Test {

    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of an empty string",
            () -> new Xxh3Hash128(
                new KeyOf(""),
                new Seed64(0L)
            ).hash().asString(),
            new IsText("99aa06d3014798d86001c324468d497f")
        ).affirm();
    }

    @Test
    void evaluateOneToThreeBytes() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 2-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("ñ"),
                new Seed64(0x3C6EF372FE94F82BL)
            ).hash().asString(),
            new IsText("e87d2c0fabf995c1b4e8fcbe25e90fe6")
        ).affirm();
    }

    @Test
    void evaluateFourToEightBytes() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 5-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("über"),
                new Seed64(7L)
            ).hash().asString(),
            new IsText("7063e20cc156cf0dfb8775bd19451e9f")
        ).affirm();
    }

    @Test
    void evaluateNineToSixteenBytes() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 12-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("Ünïcödé!"),
                new Seed64(0xA54FF53A5F1D36F1L)
            ).hash().asString(),
            new IsText("a871e8006834b1346084cc0d02375ad2")
        ).affirm();
    }

    @Test
    void evaluateSeventeenToOneHundredTwentyEightBytes() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 32-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("Çà et là, l'œuvre ébauchée"),
                new Seed64(0x510E527FADE682D1L)
            ).hash().asString(),
            new IsText("4f84bfb2fe4c56e54da804df4b5f067c")
        ).affirm();
    }

    @Test
    void evaluateOneHundredTwentyNineToTwoHundredFortyBytes() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 180-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("€".repeat(60)),
                new Seed64(0x9B05688C2B3E6C1FL)
            ).hash().asString(),
            new IsText("77e9359e9d3e4afdd9d686a85a695567")
        ).affirm();
    }

    @Test
    void evaluateASingleBlock() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 660-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("Привет, мир! ".repeat(30)),
                new Seed64(0x1F83D9ABFB41BD6BL)
            ).hash().asString(),
            new IsText("e85aab4d208dc7b6225aa8ff257f8a6b")
        ).affirm();
    }

    @Test
    void evaluateManyBlocks() {
        new Assertion<>(
            "must evaluate the xxh3 128-bit hash of a 5500-byte string",
            () -> new Xxh3Hash128(
                new KeyOf("ação ✓ ".repeat(500)),
                new Seed64(0x5BE0CD19137E2179L)
            ).hash().asString(),
            new IsText("2cf610e3c89d20afa26839b48573ac6b")
        ).affirm();
    }

    @Test
    void evaluateManyBlocksWithoutSeed() {
        new Assertion<>(
            "must evaluate the unseeded xxh3 128-bit hash of a long string",
            () -> new Xxh3Hash128(
                new KeyOf("ação ✓ ".repeat(500)),
                new Seed64(0L)
            ).hash().asString(),
            new IsText("8777cfba5faf4a16abbc59bde71283ef")
        ).affirm();
    }

    @Test
    void passesVerificationTest() {
        new Assertion<>(
            "xxh3 128-bit must produce the SMHasher3 verification code",
            new Verification(
                (key, seed) -> new Xxh3Hash128(key, seed).hash()
            ).metric(),
            new IsEqual<>(0x288DAA94L)
        ).affirm();
    }

    @Test
    void passesAvalancheWithFourBytesKeys() {
        new Assertion<>(
            "xxh3 128-bit must pass avalanche measure with 4-byte keys",
            new Avalanche(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                4,
                new Seed64(54_321L),
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithSixteenBytesKeys() {
        new Assertion<>(
            "xxh3 128-bit must pass avalanche measure with 16-byte keys",
            new Avalanche(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                16,
                new Seed64(54_321L),
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithSixtyFourBytesKeys() {
        new Assertion<>(
            "xxh3 128-bit must pass avalanche measure with 64-byte keys",
            new Avalanche(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                64,
                new Seed64(54_321L),
                50_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "xxh3 128-bit must pass collision measure",
            new Collision(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(67_890L),
                16,
                new Seed64(12_345L),
                1_000_000
            ).metric().ratio().value(),
            new IsLessThan(2.0, "collision ratio")
        ).affirm();
    }

    @Test
    void passesDistributionTest() {
        new Assertion<>(
            "xxh3 128-bit must pass distribution measure",
            new Distribution(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                16,
                new Seed64(67_890L),
                1_000_000
            ).metric().value(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void passesBicWithFourBytesKeys() {
        new Assertion<>(
            "xxh3 128-bit BIC measure (known weakness with 4-byte keys)",
            new Bic(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(11_111L),
                4,
                new Seed64(12_345L),
                100_000
            ).metric().max(),
            new IsLessThan(0.85, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "xxh3 128-bit must pass sanity measure",
            new Sanity(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(0L),
                32
            ).metric().value(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "xxh3 128-bit must pass zeroes measure",
            new ZeroesKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(0L),
                204_800
            ).metric().ratio().value(),
            new IsLessThan(2.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "xxh3 128-bit must pass cyclic key measure",
            new CyclicKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                4,
                8
            ).metric().ratio().value(),
            new IsLessThan(2.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "xxh3 128-bit must pass two-bytes measure",
            new TwoBytes(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(),
                4
            ).metric().ratio().value(),
            new IsLessThan(2.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "xxh3 128-bit must pass sparse key measure",
            new SparseKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(),
                32,
                3
            ).metric().ratio().value(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "xxh3 128-bit must pass permutation measure",
            new PermutationKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).metric().ratio().value(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "xxh3 128-bit must pass windowed key measure",
            new WindowedKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(),
                4,
                12
            ).metric().ratios().worst().value(),
            new IsLessThan(10.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "xxh3 128-bit must pass text measure",
            new TextKey(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(),
                "Fôo".getBytes(StandardCharsets.UTF_8),
                "Bär".getBytes(StandardCharsets.UTF_8),
                4
            ).metric().ratio().value(),
            new IsLessThan(10.0, "text collision ratio")
        ).affirm();
    }

    @Test
    void passesDifferentialTest() {
        new Assertion<>(
            "xxh3 128-bit must pass differential measure",
            new Differential(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                4,
                new Seed64(54_321L),
                100_000
            ).metric().ratios().worst().value(),
            new IsLessThan(10.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "xxh3 128-bit must pass diff distribution measure",
            new DiffDistribution(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                4,
                new Seed64(54_321L),
                100_000
            ).metric(),
            new IsLessThan(0.01, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesSeedTest() {
        new Assertion<>(
            "xxh3 128-bit must pass seed measure",
            new SeedsCollisions(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                100_000
            ).metric().ratio().value(),
            new IsLessThan(2.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "xxh3 128-bit must pass appended zeroes measure",
            new AppendedZeroes(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L)
            ).metric().value(),
            new IsLessThan(0.01, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "xxh3 128-bit must pass perlin noise measure",
            new PerlinNoise(
                (key, seed) -> new Xxh3Hash128(key, seed).hash()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "xxh3 128-bit must pass PRNG measure",
            new Prng(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "xxh3 128-bit must pass words measure",
            new WordsCollisions(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L),
                new Words(100_000, 2, 10)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "xxh3 128-bit must pass moment chi-squared measure",
            new MomentChiSquared(
                (key, seed) -> new Xxh3Hash128(key, seed).hash(),
                new Seed64(12_345L)
            ).metric(),
            new IsLessThan(500.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "xxh3 128-bit must pass bad seeds measure",
            new BadSeeds(
                (key, seed) -> new Xxh3Hash128(key, seed).hash()
            ).metric().value(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }
}
