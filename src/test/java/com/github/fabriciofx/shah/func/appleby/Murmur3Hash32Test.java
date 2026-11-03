/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.appleby;

import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.seed.Seed32;
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
import java.nio.charset.StandardCharsets;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link Murmur3Hash32}.
 *
 * <p>These measures adapt the SMHasher test suite to measure the
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
final class Murmur3Hash32Test {

    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the murmur3 hash of an empty string",
            () -> new Murmur3Hash32(
                new KeyOf(""),
                new Seed32(0)
            ).hash().asString(),
            new IsText("00000000")
        ).affirm();
    }

    @Test
    void evaluateCharacterA() {
        new Assertion<>(
            "must evaluate the murmur3 hash of character 'a'",
            () -> new Murmur3Hash32(
                new KeyOf("a"),
                new Seed32(0)
            ).hash().asString(),
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
                new Seed32(0)
            ).hash().asString(),
            new IsText("23f74f2e")
        ).affirm();
    }

    @Test
    void evaluateHello() {
        new Assertion<>(
            "must evaluate the murmur3 hash of hello string",
            () -> new Murmur3Hash32(
                new KeyOf("hello"),
                new Seed32(0)
            ).hash().asString(),
            new IsText("47fa8b24")
        ).affirm();
    }

    @Test
    void evaluateWithSeed() {
        new Assertion<>(
            "must evaluate the murmur3 hash with a non-zero seed",
            () -> new Murmur3Hash32(
                new KeyOf("hello"),
                new Seed32(42)
            ).hash().asString(),
            new IsText("e1d2dbe2")
        ).affirm();
    }

    @Test
    void evaluateExactlyFourBytes() {
        new Assertion<>(
            "must evaluate the murmur3 hash of a 4-byte string",
            () -> new Murmur3Hash32(
                new KeyOf("test"),
                new Seed32(0)
            ).hash().asString(),
            new IsText("13d26bba")
        ).affirm();
    }

    @Test
    void evaluateEightBytes() {
        new Assertion<>(
            "must evaluate the murmur3 hash of an 8-byte string",
            () -> new Murmur3Hash32(
                new KeyOf("testtest"),
                new Seed32(0)
            ).hash().asString(),
            new IsText("f3b1232b")
        ).affirm();
    }

    @Test
    void passesAvalancheWithFourBytesKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche measure with 4-byte keys",
            new Avalanche(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithEightByteKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche measure with 8-byte keys",
            new Avalanche(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                8,
                new Seed32(54_321),
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesAvalancheWithSixteenBytesKeys() {
        new Assertion<>(
            "murmur3 must pass avalanche measure with 16-byte keys",
            new Avalanche(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                16,
                new Seed32(54_321),
                500_000
            ).metric().bias().mean(),
            new IsLessThan(0.01, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "murmur3 must pass collision measure",
            new Collision(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(67_890),
                16,
                new Seed32(12_345),
                1_000_000
            ).metric().ratio().value(),
            new IsLessThan(2.0, "collision ratio")
        ).affirm();
    }

    @Test
    void passesDistributionTest() {
        new Assertion<>(
            "murmur3 must pass distribution measure",
            new Distribution(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                16,
                new Seed32(67_890),
                1_000_000
            ).metric().value(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void passesBicWithFourBytesKeys() {
        new Assertion<>(
            "murmur3 must pass BIC measure with 4-byte keys",
            new Bic(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(11_111),
                4,
                new Seed32(12_345),
                100_000
            ).metric().max(),
            new IsLessThan(0.05, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "murmur3 must pass sanity measure",
            new Sanity(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(0),
                32
            ).metric().value(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "murmur3 must pass zeroes measure",
            new ZeroesKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(0),
                204_800
            ).metric().ratio().value(),
            new IsLessThan(2.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "murmur3 must pass cyclic key measure",
            new CyclicKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                8
            ).metric().ratio().value(),
            new IsLessThan(2.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "murmur3 must pass two-bytes measure",
            new TwoBytes(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(),
                4
            ).metric().ratio().value(),
            new IsLessThan(2.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "murmur3 must pass sparse key measure",
            new SparseKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(),
                32,
                3
            ).metric().ratio().value(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "murmur3 must pass permutation measure",
            new PermutationKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).metric().ratio().value(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "murmur3 must pass windowed key measure",
            new WindowedKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(),
                4,
                12
            ).metric().ratios().worst().value(),
            new IsLessThan(10.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "murmur3 must pass text measure",
            new TextKey(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(),
                "Foo".getBytes(StandardCharsets.UTF_8),
                "Bar".getBytes(StandardCharsets.UTF_8),
                4
            ).metric().ratio().value(),
            new IsLessThan(10.0, "text collision ratio")
        ).affirm();
    }

    @Test
    void passesDifferentialTest() {
        new Assertion<>(
            "murmur3 must pass differential measure",
            new Differential(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                100_000
            ).metric().ratios().worst().value(),
            new IsLessThan(10.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "murmur3 must pass diff distribution measure",
            new DiffDistribution(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                100_000
            ).metric(),
            new IsLessThan(0.01, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesSeedTest() {
        new Assertion<>(
            "murmur3 must pass seed measure",
            new SeedsCollisions(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                100_000
            ).metric().ratio().value(),
            new IsLessThan(2.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "murmur3 must pass appended zeroes measure",
            new AppendedZeroes(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345)
            ).metric().value(),
            new IsLessThan(0.01, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesVerificationTest() {
        new Assertion<>(
            "murmur3 must produce correct verification code",
            new Verification(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric(),
            new IsEqual<>(0xB0F57EE3L)
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "murmur3 must pass perlin noise measure",
            new PerlinNoise(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "murmur3 must pass PRNG measure",
            new Prng(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "murmur3 must pass words measure",
            new WordsCollisions(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345),
                new Words(100_000, 2, 10)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "murmur3 must pass moment chi-squared measure",
            new MomentChiSquared(
                (key, seed) -> new Murmur3Hash32(key, seed).hash(),
                new Seed32(12_345)
            ).metric(),
            new IsLessThan(500.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "murmur3 must pass bad seeds measure",
            new BadSeeds(
                (key, seed) -> new Murmur3Hash32(key, seed).hash()
            ).metric().value(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }
}
