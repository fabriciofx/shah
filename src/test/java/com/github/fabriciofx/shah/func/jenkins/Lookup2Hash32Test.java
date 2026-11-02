/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.IsGreaterThanOrEqualTo;
import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.seed.Seed32;
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
import java.util.stream.IntStream;
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
 * <p>Lookup2 also fails SMHasher's permutation criterion (collision
 * ratio below 2.0) for about half of the seeds. For keys up to eight
 * bytes, bytes 0 and 4 land in the low bits of two registers whose
 * final mix starts by subtracting one from the other. Keys that
 * differ only by the same increment in both bytes cancel that
 * difference at once and collide far more often than expected. The
 * seed decides how many of those pairs collide, so the permutation
 * test uses the relaxed threshold of 10.0.</p>
 *
 * <p>The zeroes test hashes all-zero keys from 0 to 204800 bytes with
 * a random seed, which gives only about 4.9 expected collisions. With
 * so few collisions, SMHasher's threshold of 2.0 means just 10 of them,
 * and even an ideal hash reaches that for about 2.8% of the seeds.
 * Lookup2 behaves like an ideal hash here, so the zeroes test uses
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
            new AvalancheTest(
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
            new AvalancheTest(
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
            "lookup2 must pass collision test",
            new CollisionTest(
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
            "lookup2 must pass distribution test",
            new DistributionTest(
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
            new BicTest(
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
            "lookup2 must pass sanity test",
            new SanityTest(
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
            "lookup2 must pass zeroes test",
            new ZeroesTest(
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
            "lookup2 must pass cyclic key test",
            new CyclicKeyTest(
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
            "lookup2 must pass two-bytes test",
            new TwoBytesTest(
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
            "lookup2 must pass sparse key test",
            new SparseKeyTest(
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
            "lookup2 must pass permutation test",
            new PermutationTest(
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
                idx -> new PermutationTest(
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
            "lookup2 must pass windowed key test",
            new WindowedKeyTest(
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
            "lookup2 must pass text test",
            new TextTest(
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
            "lookup2 must pass differential test",
            new DifferentialTest(
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
            "lookup2 diff dist test (known weakness with small keys)",
            new DiffDistTest(
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
            "lookup2 must pass seed test",
            new SeedTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                100_000
            ).metric().ratio().value(),
            new IsLessThan(3.0, "seed collision ratio")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "lookup2 must pass appended zeroes test",
            new AppendedZeroesTest(
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
            new VerificationTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric(),
            new IsEqual<>(0x8B7FB2D2L)
        ).affirm();
    }

    @Test
    void passesPerlinNoiseTest() {
        new Assertion<>(
            "lookup2 must pass perlin noise test",
            new PerlinNoiseTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric().ratio().value(),
            new IsLessThan(2.0, "perlin noise collision ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "lookup2 must pass PRNG test",
            new PrngTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(0)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "lookup2 must pass words test",
            new WordsTest(
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
            new MomentChi2Test(
                (key, seed) -> new Lookup2Hash32(key, seed).hash(),
                new Seed32(12_345)
            ).metric(),
            new IsLessThan(1000.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void passesBadSeedsTest() {
        new Assertion<>(
            "lookup2 must pass bad seeds test",
            new BadSeedsTest(
                (key, seed) -> new Lookup2Hash32(key, seed).hash()
            ).metric().value(),
            new IsLessThan(0.01, "bad seeds failure ratio")
        ).affirm();
    }
}
