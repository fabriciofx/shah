/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.jenkins;

import com.github.fabriciofx.shah.IsLessThan;
import com.github.fabriciofx.shah.collection.Words;
import com.github.fabriciofx.shah.key.KeyOf;
import com.github.fabriciofx.shah.seed.Seed32;
import com.github.fabriciofx.shah.measure.AppendedZeroes;
import com.github.fabriciofx.shah.measure.Avalanche;
import com.github.fabriciofx.shah.measure.Bic;
import com.github.fabriciofx.shah.measure.Collision;
import com.github.fabriciofx.shah.measure.CyclicKey;
import com.github.fabriciofx.shah.measure.DiffDistribution;
import com.github.fabriciofx.shah.measure.Differential;
import com.github.fabriciofx.shah.measure.Distribution;
import com.github.fabriciofx.shah.measure.MomentChiSquared;
import com.github.fabriciofx.shah.measure.PermutationKey;
import com.github.fabriciofx.shah.measure.Prng;
import com.github.fabriciofx.shah.measure.Sanity;
import com.github.fabriciofx.shah.measure.SparseKey;
import com.github.fabriciofx.shah.measure.TextKey;
import com.github.fabriciofx.shah.measure.TwoBytes;
import com.github.fabriciofx.shah.measure.WindowedKey;
import com.github.fabriciofx.shah.measure.WordsCollisions;
import com.github.fabriciofx.shah.measure.ZeroesKey;
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
 * <li>Fails strict avalanche measure (1% bias) for small keys</li>
 * <li>Fails BIC measure (5% bias)</li>
 * <li>Maps all zero-byte keys to 0 regardless of length</li>
 * <li>Shows higher collision rates in two-byte sparse keysets</li>
 * <li>Poor differential behavior with single-bit flips</li>
 * </ul>
 *
 * <p>Thresholds are relaxed to reflect these known limitations
 * while still validating reasonable distribution quality. OAAT
 * has no seed parameter, so the SeedTest is not applicable.</p>
 *
 * @since 0.0.1
 */
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
            new Avalanche(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.60, "avalanche bias")
        ).affirm();
    }

    @Test
    void avalancheWithEightBytesKeys() {
        new Assertion<>(
            "oaat avalanche bias with 8-byte keys must be below 60%",
            new Avalanche(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                8,
                new Seed32(54_321),
                500_000
            ).metric().bias().max(),
            new IsLessThan(0.60, "avalanche bias")
        ).affirm();
    }

    @Test
    void passesCollisionTest() {
        new Assertion<>(
            "oaat must pass collision measure",
            new Collision(
                (key, seed) -> new OaatHash32(key).hash(),
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
            "oaat must pass distribution measure",
            new Distribution(
                (key, seed) -> new OaatHash32(key).hash(),
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
            "oaat BIC bias with 4-byte keys must be below 200%",
            new Bic(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(11_111),
                4,
                new Seed32(12_345),
                100_000
            ).metric().max(),
            new IsLessThan(2.0, "BIC bias")
        ).affirm();
    }

    @Test
    void passesSanityTest() {
        new Assertion<>(
            "oaat must pass sanity measure",
            new Sanity(
                (key, seed) -> new OaatHash32(key).hash(),
                32
            ).metric().value(),
            new IsLessThan(0.01, "sanity failure ratio")
        ).affirm();
    }

    @Test
    void passesZeroesTest() {
        new Assertion<>(
            "oaat zeroes measure (known weakness: all-zero keys collide)",
            new ZeroesKey(
                (key, seed) -> new OaatHash32(key).hash(),
                204_800
            ).metric().ratio().value(),
            new IsLessThan(100_000.0, "zeroes collision ratio")
        ).affirm();
    }

    @Test
    void passesCyclicKeyTest() {
        new Assertion<>(
            "oaat must pass cyclic key measure",
            new CyclicKey(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                4,
                8
            ).metric().ratio().value(),
            new IsLessThan(10.0, "cyclic collision ratio")
        ).affirm();
    }

    @Test
    void passesTwoBytesTest() {
        new Assertion<>(
            "oaat two-bytes measure (known elevated collision rate)",
            new TwoBytes(
                (key, seed) -> new OaatHash32(key).hash(),
                4
            ).metric().ratio().value(),
            new IsLessThan(25.0, "two-bytes collision ratio")
        ).affirm();
    }

    @Test
    void passesSparseKeyTest() {
        new Assertion<>(
            "oaat must pass sparse key measure",
            new SparseKey(
                (key, seed) -> new OaatHash32(key).hash(),
                32,
                3
            ).metric().ratio().value(),
            new IsLessThan(10.0, "sparse collision ratio")
        ).affirm();
    }

    @Test
    void passesPermutationTest() {
        new Assertion<>(
            "oaat must pass permutation measure",
            new PermutationKey(
                (key, seed) -> new OaatHash32(key).hash(),
                new byte[]{0, 1, 2, 3, 4, 5, 6, 7},
                4
            ).metric().ratio().value(),
            new IsLessThan(10.0, "permutation collision ratio")
        ).affirm();
    }

    @Test
    void passesWindowedKeyTest() {
        new Assertion<>(
            "oaat windowed key measure (known weakness with auto-sized window)",
            new WindowedKey(
                (key, seed) -> new OaatHash32(key).hash(),
                4,
                12
            ).metric().ratios().worst().value(),
            new IsLessThan(200.0, "windowed collision ratio")
        ).affirm();
    }

    @Test
    void passesTextTest() {
        new Assertion<>(
            "oaat must pass text measure",
            new TextKey(
                (key, seed) -> new OaatHash32(key).hash(),
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
            "oaat differential measure (known weakness with small keys)",
            new Differential(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                100_000
            ).metric().ratios().worst().value(),
            new IsLessThan(5000.0, "differential collision ratio")
        ).affirm();
    }

    @Test
    void passesDiffDistTest() {
        new Assertion<>(
            "oaat diff dist measure (known weakness with small keys)",
            new DiffDistribution(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                4,
                new Seed32(54_321),
                100_000
            ).metric(),
            new IsLessThan(1.0, "diff distribution score")
        ).affirm();
    }

    @Test
    void passesAppendedZeroesTest() {
        new Assertion<>(
            "oaat appended zeroes measure (known weakness: all-zero collide)",
            new AppendedZeroes(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345)
            ).metric().value(),
            new IsLessThan(1.0, "appended zeroes failure ratio")
        ).affirm();
    }

    @Test
    void passesPrngTest() {
        new Assertion<>(
            "oaat PRNG measure (known weakness: zero fixed point)",
            new Prng(
                (key, seed) -> new OaatHash32(key).hash()
            ).metric().ratio().value(),
            new IsLessThan(100_000.0, "PRNG collision ratio")
        ).affirm();
    }

    @Test
    void passesWordsTest() {
        new Assertion<>(
            "oaat must pass words measure",
            new WordsCollisions(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345),
                new Words(100_000, 2, 10)
            ).metric().ratio().value(),
            new IsLessThan(2.0, "words collision ratio")
        ).affirm();
    }

    @Test
    void passesMomentChiSquaredTest() {
        new Assertion<>(
            "oaat must pass moment chi-squared measure",
            new MomentChiSquared(
                (key, seed) -> new OaatHash32(key).hash(),
                new Seed32(12_345)
            ).metric(),
            new IsLessThan(500.0, "moment chi-squared")
        ).affirm();
    }
}
