/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.IsGreaterThanOrEqualTo;
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
import com.github.fabriciofx.shah.seed.Seed32;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link Lookup2Hash32}.
 *
 * <p>Note: Lookup2 is an older hash function that fails SMHasher's
 * strict avalanche measure (bias threshold 1%) for small key sizes and
 * the BIC measure (bias threshold 5%). The thresholds here are relaxed
 * to reflect the known limitations of this func.</p>
 *
 * <p>Lookup2 also fails SMHasher's permutation criterion (collision
 * ratio below 2.0) for about half of the seeds. For keys up to eight
 * bytes, bytes 0 and 4 land in the low bits of two registers whose
 * final mix starts by subtracting one from the other. Keys that
 * differ only by the same increment in both bytes cancel that
 * difference at once and collide far more often than expected. The
 * seed decides how many of those pairs collide, so the permutation
 * measure uses the relaxed threshold of 10.0.</p>
 *
 * <p>The zeroes measure hashes all-zero keys from 0 to 204800 bytes with
 * a random seed, which gives only about 4.9 expected collisions. With
 * so few collisions, SMHasher's threshold of 2.0 means just 10 of them,
 * and even an ideal hash reaches that for about 2.8% of the seeds.
 * Lookup2 behaves like an ideal hash here, so the zeroes measure uses
 * the threshold of 4.0, which an ideal hash exceeds for about one seed
 * in four million.</p>
 *
 * @since 0.0.1
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
final class Lookup2Hash32Test {

    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the lookup2 hash of an empty string",
            () -> new Lookup2Hash32(
                new KeyOf(""),
                new Seed32(0)
            ).hash().asString(),
            new IsText("0dd149bd")
        ).affirm();
    }

    @Test
    void evaluateCharacterA() {
        new Assertion<>(
            "must evaluate the lookup2 hash of character 'a'",
            () -> new Lookup2Hash32(
                new KeyOf("a"),
                new Seed32(0)
            ).hash().asString(),
            new IsText("18c8ee29")
        ).affirm();
    }

    @Test
    void evaluateHello() {
        new Assertion<>(
            "must evaluate the lookup2 hash of hello string",
            () -> new Lookup2Hash32(
                new KeyOf("hello"),
                new Seed32(0)
            ).hash().asString(),
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
                new Seed32(0)
            ).hash().asString(),
            new IsText("de5815fc")
        ).affirm();
    }

    @Test
    void avalancheWithFourBytesKeys() {
        new Assertion<>(
            "lookup2 avalanche bias with 4-byte keys must be below 30%",
            new Avalanche(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.30, "avalanche bias")
        ).affirm();
    }

    @Test
    void avalancheWithEightBytesKeys() {
        new Assertion<>(
            "lookup2 avalanche bias with 8-byte keys must be below 40%",
            new Avalanche(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345),
                8,
                new Seed32(54_321),
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.40, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "lookup2 must pass collision measure",
            new Collision(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 must pass distribution measure",
            new Distribution(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345),
                16,
                new Seed32(67_890),
                1_000_000
            ).metric().value(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void bicWithFourBytesKeys() {
        new Assertion<>(
            "lookup2 BIC bias with 4-byte keys must be below 60%",
            new Bic(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(11_111),
                4,
                new Seed32(12_345),
                100_000
            ).metric().max(),
            new IsLessThan(0.60, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "lookup2 must pass sanity measure",
            new Sanity(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(),
                32
            ).metric().value(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "lookup2 must pass zeroes measure",
            new ZeroesKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(),
                204_800
            ).metric().ratio().value(),
            new IsLessThan(4.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "lookup2 must pass cyclic key measure",
            new CyclicKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 must pass two-bytes measure",
            new TwoBytes(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(),
                4
            ).metric().ratio().value(),
            new IsLessThan(2.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "lookup2 must pass sparse key measure",
            new SparseKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 must pass permutation measure",
            new PermutationKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                5
            ).metric().ratio().value(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void exceedsSmhasherPermutationRatioForSomeSeed() {
        new Assertion<>(
            "lookup2 must exceed smhasher permutation ratio for some seed",
            IntStream.range(0, 24).mapToDouble(
                idx -> new PermutationKey(
                    (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                    new Seed32(),
                    new byte[]{
                        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
                    },
                    5
                ).metric().ratio().value()
            ).max().orElseThrow(),
            new IsGreaterThanOrEqualTo(2.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "lookup2 must pass windowed key measure",
            new WindowedKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 must pass text measure",
            new TextKey(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 must pass differential measure",
            new Differential(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
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
            "lookup2 diff dist measure (known weakness with small keys)",
            new DiffDistribution(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                100_000
            ).metric(),
            new IsLessThan(0.20, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesSeedTest() {
        new Assertion<>(
            "lookup2 must pass seed measure",
            new SeedsCollisions(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                100_000
            ).metric().ratio().value(),
            new IsLessThan(3.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "lookup2 must pass appended zeroes measure",
            new AppendedZeroes(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345)
            ).metric().value(),
            new IsLessThan(0.01, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesVerificationTest() {
        new Assertion<>(
            "lookup2 must produce correct verification code",
            new Verification(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric(),
            new IsEqual<>(0x8B7FB2D2L)
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "lookup2 must pass perlin noise measure",
            new PerlinNoise(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "lookup2 must pass PRNG measure",
            new Prng(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(0)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "lookup2 must pass words measure",
            new WordsCollisions(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345),
                new Words(100_000, 2, 10)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "lookup2 moment chi-squared (known weakness, fails SMHasher)",
            new MomentChiSquared(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345)
            ).metric(),
            new IsLessThan(1000.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "lookup2 must pass bad seeds measure",
            new BadSeeds(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric().value(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }
}
