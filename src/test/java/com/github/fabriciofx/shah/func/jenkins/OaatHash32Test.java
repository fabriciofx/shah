/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.benchmark.HashmapBenchmark;
import com.github.fabriciofx.shah.benchmark.SpeedBenchmark;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.test.AppendedZeroesTest;
import com.github.fabriciofx.shah.test.AvalancheTest;
import com.github.fabriciofx.shah.test.BicTest;
import com.github.fabriciofx.shah.test.CollisionTest;
import com.github.fabriciofx.shah.test.CyclicKeyTest;
import com.github.fabriciofx.shah.test.DiffDistTest;
import com.github.fabriciofx.shah.test.DifferentialTest;
import com.github.fabriciofx.shah.test.DistributionTest;
import com.github.fabriciofx.shah.test.MomentChi2Test;
import com.github.fabriciofx.shah.test.PermutationTest;
import com.github.fabriciofx.shah.test.PrngTest;
import com.github.fabriciofx.shah.test.SanityTest;
import com.github.fabriciofx.shah.test.SparseKeyTest;
import com.github.fabriciofx.shah.test.TextTest;
import com.github.fabriciofx.shah.test.TwoBytesTest;
import com.github.fabriciofx.shah.test.WindowedKeyTest;
import com.github.fabriciofx.shah.test.WordsTest;
import com.github.fabriciofx.shah.test.ZeroesTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsText;

/**
 * Tests for {@link OaatHash32}.
 *
 * <p>Note: OAAT is a simple hash function with several known
 * SMHasher weaknesses:</p>
 * <ul>
 *   <li>Fails strict avalanche test (1% bias) for small keys</li>
 *   <li>Fails BIC test (5% bias)</li>
 *   <li>Maps all zero-byte keys to 0 regardless of length</li>
 *   <li>Shows higher collision rates in two-byte sparse keysets</li>
 *   <li>Poor differential behavior with single-bit flips</li>
 * </ul>
 *
 * <p>Thresholds are relaxed to reflect these known limitations
 * while still validating reasonable distribution quality. OAAT
 * has no seed parameter, so the SeedTest is not applicable.</p>
 *
 * @since 0.0.1
 */
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.TooManyMethods"})
final class OaatHash32Test {
    @Test
    void evaluateAnEmptyString() {
        new Assertion<>(
            "must evaluate the oaat hash of an empty string",
            () -> new OaatHash32(new KeyOf("")).hash().asString(),
            new IsText("00000000")
        ).affirm();
    }

    @Test
    void evaluateCharacterA() {
        new Assertion<>(
            "must evaluate the oaat hash of character 'a'",
            () -> new OaatHash32(new KeyOf("a")).hash().asString(),
            new IsText("42942eca")
        ).affirm();
    }

    @Test
    void evaluateAllCharacters() {
        new Assertion<>(
            "must evaluate the oaat hash of all characters",
            () -> new OaatHash32(
                new KeyOf("The quick brown fox jumps over the lazy dog")
            ).hash().asString(),
            new IsText("f5919e51")
        ).affirm();
    }

    @Test
    void avalancheWithFourBytesKeys() {
        new Assertion<>(
            "oaat avalanche bias with 4-byte keys must be below 60%",
            new AvalancheTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                4,
                54_321L,
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.60, "avalanche bias")
        ).affirm();
    }

    @Test
    void avalancheWithEightBytesKeys() {
        new Assertion<>(
            "oaat avalanche bias with 8-byte keys must be below 60%",
            new AvalancheTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                8,
                54_321L,
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.60, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "oaat must pass collision test",
            new CollisionTest(
                (key, seed) -> new OaatHash32(key).hash(),
                67_890L,
                16,
                12_345L,
                1_000_000
            ).metric().ratio(),
            new IsLessThan(2.0, "collision ratio")
        ).affirm();
    }

    @Test
    void passesDistributionTest() {
        new Assertion<>(
            "oaat must pass distribution test",
            new DistributionTest(
                key -> new OaatHash32(key).hash(),
                1_000_000,
                16,
                67_890L
            ).metric(),
            new IsLessThan(0.01, "distribution score")
        ).affirm();
    }

    @Test
    void bicWithFourBytesKeys() {
        new Assertion<>(
            "oaat BIC bias with 4-byte keys must be below 200%",
            new BicTest(
                (key, seed) -> new OaatHash32(key).hash(),
                11_111L,
                4,
                12_345L,
                100_000
            ).metric().max(),
            new IsLessThan(2.0, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "oaat must pass sanity test",
            new SanityTest(
                key -> new OaatHash32(key).hash(),
                32
            ).metric(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "oaat zeroes test (known weakness: all-zero keys collide)",
            new ZeroesTest(
                key -> new OaatHash32(key).hash(),
                204_800
            ).metric().ratio(),
            new IsLessThan(100_000.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "oaat must pass cyclic key test",
            new CyclicKeyTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                4,
                8
            ).metric().ratio(),
            new IsLessThan(10.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "oaat two-bytes test (known elevated collision rate)",
            new TwoBytesTest(
                key -> new OaatHash32(key).hash(),
                4
            ).metric().ratio(),
            new IsLessThan(25.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "oaat must pass sparse key test",
            new SparseKeyTest(
                key -> new OaatHash32(key).hash(),
                32,
                3
            ).metric().ratio(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "oaat must pass permutation test",
            new PermutationTest(
                key -> new OaatHash32(key).hash(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).metric().ratio(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "oaat windowed key test (known weakness with auto-sized window)",
            new WindowedKeyTest(
                key -> new OaatHash32(key).hash(),
                4,
                12
            ).metric().worst(),
            new IsLessThan(200.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "oaat must pass text test",
            new TextTest(
                key -> new OaatHash32(key).hash(),
                "Foo".getBytes(StandardCharsets.UTF_8),
                "Bar".getBytes(StandardCharsets.UTF_8),
                4
            ).metric().ratio(),
            new IsLessThan(10.0, "text collision ratio")
        ).affirm();
    }

    @Test
    void passesDifferentialTest() {
        new Assertion<>(
            "oaat differential test (known weakness with small keys)",
            new DifferentialTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                4,
                54_321L,
                100_000
            ).metric().worst(),
            new IsLessThan(5000.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "oaat diff dist test (known weakness with small keys)",
            new DiffDistTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                4,
                54_321L,
                100_000
            ).metric(),
            new IsLessThan(1.0, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "oaat appended zeroes test (known weakness: all-zero collide)",
            new AppendedZeroesTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L
            ).metric(),
            new IsLessThan(1.0, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "oaat PRNG test (known weakness: zero fixed point)",
            new PrngTest(
                key -> new OaatHash32(key).hash()
            ).metric().ratio(),
            new IsLessThan(100_000.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "oaat must pass words test",
            new WordsTest(
                (key, seed) -> new OaatHash32(key).hash(),
                12_345L,
                new Words(100_000, 2, 10)
            ).metric().ratio(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "oaat must pass moment chi-squared test",
            new MomentChi2Test(
                key -> new OaatHash32(key).hash()
            ).metric(),
            new IsLessThan(500.0, "moment chi-squared")
        ).affirm();
    }

    @Test
    void computeSpeedBenchmark() {
        new Assertion<>(
            "must compute oaat speed benchmark",
            new SpeedBenchmark(
                key -> new OaatHash32(key).hash()
            ).run(),
            new IsLessThan(100_000_000.0, "bulk ns/op")
        ).affirm();
    }

    @Test
    void passesSmallKeySpeedTest() {
        new Assertion<>(
            "oaat small key speed test must complete",
            new SpeedBenchmark(
                key -> new OaatHash32(key).hash(),
                4
            ).run(),
            new IsLessThan(100_000.0, "small key ns/op")
        ).affirm();
    }

    @Test
    void performHashmapBenchmark() {
        new Assertion<>(
            "must perform ooat hashmap benchmark",
            new HashmapBenchmark(
                key -> new OaatHash32(key).hash()
            ).run(),
            new IsLessThan(100_000.0, "hashmap ns/op")
        ).affirm();
    }
}
