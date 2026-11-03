/*
 * SPDX-FileCopyrightText: Copyright (C) 2026 Fabrício Barros Cabral
 * SPDX-License-Identifier: MIT
 */
package com.github.fabriciofx.shah.func.collet;

import com.github.fabriciofx.shah.Func;
import com.github.fabriciofx.shah.Hash;
import com.github.fabriciofx.shah.Key;
import com.github.fabriciofx.shah.Seed;
import com.github.fabriciofx.shah.hash.Hash128;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.HexFormat;

/**
 * XXH3 128-bit hash.
 *
 * <p>The 128-bit variant of XXH3, from xxHash v0.8 by Yann Collet, seeded
 * with a 64-bit value. SMHasher3 tests it as {@code XXH3-128}. The hash is
 * written in the canonical big-endian form, the same printed by
 * {@code xxh128sum}.</p>
 *
 * <p>For example, the hash of an empty key with a zero seed is
 * {@code 99aa06d3014798d86001c324468d497f}:</p>
 *
 * <pre>{@code
 * new Xxh3Hash128(new KeyOf(""), new Seed64(0L)).hash().asString();
 * }</pre>
 *
 * @see <a href="https://github.com/Cyan4973/xxHash">xxHash</a>
 * @since 0.0.1
 * @checkstyle BooleanExpressionComplexityCheck (500 lines)
 */
public final class Xxh3Hash128 implements Func {

    /**
     * First 32-bit prime.
     */
    private static final long PRIME32_1 = 0x9E3779B1L;

    /**
     * Second 32-bit prime.
     */
    private static final long PRIME32_2 = 0x85EBCA77L;

    /**
     * First 64-bit prime.
     */
    private static final long PRIME64_1 = 0x9E3779B185EBCA87L;

    /**
     * Second 64-bit prime.
     */
    private static final long PRIME64_2 = 0xC2B2AE3D27D4EB4FL;

    /**
     * Third 64-bit prime.
     */
    private static final long PRIME64_3 = 0x165667B19E3779F9L;

    /**
     * Fourth 64-bit prime.
     */
    private static final long PRIME64_4 = 0x85EBCA77C2B2AE63L;

    /**
     * Default secret, 192 bytes long.
     */
    private static final byte[] SECRET = HexFormat.of().parseHex(
        String.join(
            "",
            "b8fe6c3923a44bbe7c01812cf721ad1cded46de9839097db",
            "7240a4a4b7b3671fcb79e64eccc0e578825ad07dccff7221",
            "b8084674f743248ee03590e6813a264c3c2852bb91c300cb",
            "88d0658b1b532ea371644897a20df94e3819ef46a9deacd8",
            "a8fa763fe39c343ff9dcbbc7c70b4f1d8a51e04bcdb45931",
            "c89f7ec9d9787364eac5ac8334d3ebc3c581a0fffa1363eb",
            "170ddd51b7f0da49d316552629d4689e2b16be587d47a1fc",
            "8ff8b8d17ad031ce45cb3a8f95160428afd7fbcabb4b407e"
        )
    );

    /**
     * Reader and writer of little-endian 64-bit words in byte arrays.
     */
    private static final VarHandle LONGS =
        MethodHandles.byteArrayViewVarHandle(
            long[].class,
            ByteOrder.LITTLE_ENDIAN
        );

    /**
     * Reader of little-endian 32-bit words in byte arrays.
     */
    private static final VarHandle INTS =
        MethodHandles.byteArrayViewVarHandle(
            int[].class,
            ByteOrder.LITTLE_ENDIAN
        );

    /**
     * Number of bytes consumed by each accumulation.
     */
    private static final int STRIPE = 64;

    /**
     * Number of bytes consumed between two scrambles.
     */
    private static final int BLOCK = 1024;

    /**
     * The key to be hashed.
     */
    private final Key key;

    /**
     * The seed.
     */
    private final Seed seed;

    /**
     * Ctor.
     *
     * @param key The key to be hashed
     * @param seed The seed
     */
    public Xxh3Hash128(final Key key, final Seed seed) {
        this.key = key;
        this.seed = seed;
    }

    @Override
    public Hash hash() {
        final byte[] input = this.key.asBytes();
        final long salt = this.seed.asLong();
        final long[] value;
        if (input.length == 0) {
            value = Xxh3Hash128.empty(salt);
        } else if (input.length <= 3) {
            value = Xxh3Hash128.tiny(input, salt);
        } else if (input.length <= 8) {
            value = Xxh3Hash128.small(input, salt);
        } else if (input.length <= 16) {
            value = Xxh3Hash128.medium(input, salt);
        } else if (input.length <= 128) {
            value = Xxh3Hash128.large(input, salt);
        } else if (input.length <= 240) {
            value = Xxh3Hash128.huge(input, salt);
        } else {
            value = Xxh3Hash128.bulk(input, salt);
        }
        return new Hash128(
            Long.reverseBytes(value[1]),
            Long.reverseBytes(value[0])
        );
    }

    private static long[] empty(final long salt) {
        return new long[]{
            Xxh3Hash128.spread(
                salt ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 64)
                    ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 72)
            ),
            Xxh3Hash128.spread(
                salt ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 80)
                    ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 88)
            ),
        };
    }

    private static long[] tiny(final byte[] input, final long salt) {
        final int length = input.length;
        final int combined = (input[0] & 0xFF) << 16
            | (input[length >> 1] & 0xFF) << 24
            | input[length - 1] & 0xFF
            | length << 8;
        return new long[]{
            Xxh3Hash128.spread(
                Integer.toUnsignedLong(combined)
                    ^ Integer.toUnsignedLong(
                        Xxh3Hash128.half(Xxh3Hash128.SECRET, 0)
                            ^ Xxh3Hash128.half(Xxh3Hash128.SECRET, 4)
                    ) + salt
            ),
            Xxh3Hash128.spread(
                Integer.toUnsignedLong(
                    Xxh3Hash128.half(Xxh3Hash128.SECRET, 8)
                        ^ Xxh3Hash128.half(Xxh3Hash128.SECRET, 12)
                ) - salt ^ Integer.toUnsignedLong(
                    Integer.rotateLeft(Integer.reverseBytes(combined), 13)
                )
            ),
        };
    }

    private static long[] small(final byte[] input, final long salt) {
        final int length = input.length;
        final long mixed = salt
            ^ (long) Integer.reverseBytes((int) salt) << 32;
        final long keyed = (
            Integer.toUnsignedLong(Xxh3Hash128.half(input, 0))
                + ((long) Xxh3Hash128.half(input, length - 4) << 32)
            ) ^ (
                (
                    Xxh3Hash128.word(Xxh3Hash128.SECRET, 16)
                        ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 24)
                ) + mixed
            );
        final long factor = Xxh3Hash128.PRIME64_1 + ((long) length << 2);
        long low = keyed * factor;
        long high = Math.unsignedMultiplyHigh(keyed, factor);
        high += low << 1;
        low ^= high >>> 3;
        low ^= low >>> 35;
        low *= 0x9FB21C651E98DF25L;
        low ^= low >>> 28;
        return new long[]{low, Xxh3Hash128.avalanche(high)};
    }

    private static long[] medium(final byte[] input, final long salt) {
        final int length = input.length;
        final long last = Xxh3Hash128.word(input, length - 8)
            ^ (
                Xxh3Hash128.word(Xxh3Hash128.SECRET, 48)
                    ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 56)
            ) + salt;
        final long product = Xxh3Hash128.word(input, 0)
            ^ Xxh3Hash128.word(input, length - 8)
            ^ (
                Xxh3Hash128.word(Xxh3Hash128.SECRET, 32)
                    ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, 40)
            ) - salt;
        final long high = Math.unsignedMultiplyHigh(
            product,
            Xxh3Hash128.PRIME64_1
        ) + last + (last & 0xFFFFFFFFL) * (Xxh3Hash128.PRIME32_2 - 1L);
        final long low = (product * Xxh3Hash128.PRIME64_1
            + ((long) (length - 1) << 54)) ^ Long.reverseBytes(high);
        return new long[]{
            Xxh3Hash128.avalanche(low * Xxh3Hash128.PRIME64_2),
            Xxh3Hash128.avalanche(
                Math.unsignedMultiplyHigh(low, Xxh3Hash128.PRIME64_2)
                    + high * Xxh3Hash128.PRIME64_2
            ),
        };
    }

    private static long[] large(final byte[] input, final long salt) {
        final int length = input.length;
        final long[] acc = {length * Xxh3Hash128.PRIME64_1, 0L};
        for (int idx = (length - 1) / 32; idx >= 0; --idx) {
            Xxh3Hash128.mix(
                acc,
                input,
                16 * idx,
                length - 16 * (idx + 1),
                32 * idx,
                salt
            );
        }
        return Xxh3Hash128.finish(acc, length, salt);
    }

    private static long[] huge(final byte[] input, final long salt) {
        final int length = input.length;
        final long[] acc = {length * Xxh3Hash128.PRIME64_1, 0L};
        for (int idx = 32; idx < 160; idx += 32) {
            Xxh3Hash128.mix(
                acc,
                input,
                idx - 32,
                idx - 16,
                idx - 32,
                salt
            );
        }
        acc[0] = Xxh3Hash128.avalanche(acc[0]);
        acc[1] = Xxh3Hash128.avalanche(acc[1]);
        for (int idx = 160; idx <= length; idx += 32) {
            Xxh3Hash128.mix(
                acc,
                input,
                idx - 32,
                idx - 16,
                idx - 157,
                salt
            );
        }
        Xxh3Hash128.mix(
            acc,
            input,
            length - 16,
            length - 32,
            103,
            -salt
        );
        return Xxh3Hash128.finish(acc, length, salt);
    }

    private static long[] bulk(final byte[] input, final long salt) {
        final int length = input.length;
        final byte[] secret = Xxh3Hash128.secret(salt);
        final long[] acc = {
            0xC2B2AE3DL, Xxh3Hash128.PRIME64_1,
            Xxh3Hash128.PRIME64_2, Xxh3Hash128.PRIME64_3,
            Xxh3Hash128.PRIME64_4, Xxh3Hash128.PRIME32_2,
            0x27D4EB2F165667C5L, Xxh3Hash128.PRIME32_1,
        };
        final int blocks = (length - 1) / Xxh3Hash128.BLOCK;
        for (int block = 0; block < blocks; ++block) {
            Xxh3Hash128.accumulate(
                acc,
                input,
                block * Xxh3Hash128.BLOCK,
                secret,
                Xxh3Hash128.BLOCK / Xxh3Hash128.STRIPE
            );
            Xxh3Hash128.scramble(acc, secret);
        }
        Xxh3Hash128.accumulate(
            acc,
            input,
            blocks * Xxh3Hash128.BLOCK,
            secret,
            (length - 1 - blocks * Xxh3Hash128.BLOCK) / Xxh3Hash128.STRIPE
        );
        Xxh3Hash128.stripe(
            acc,
            input,
            length - Xxh3Hash128.STRIPE,
            secret,
            secret.length - Xxh3Hash128.STRIPE - 7
        );
        return new long[]{
            Xxh3Hash128.merge(
                acc,
                secret,
                11,
                length * Xxh3Hash128.PRIME64_1
            ),
            Xxh3Hash128.merge(
                acc,
                secret,
                secret.length - Xxh3Hash128.STRIPE - 11,
                ~(length * Xxh3Hash128.PRIME64_2)
            ),
        };
    }

    private static void mix(
        final long[] acc,
        final byte[] input,
        final int first,
        final int second,
        final int position,
        final long salt
    ) {
        acc[0] += Xxh3Hash128.fold(input, first, position, salt);
        acc[0] ^= Xxh3Hash128.word(input, second)
            + Xxh3Hash128.word(input, second + 8);
        acc[1] += Xxh3Hash128.fold(input, second, position + 16, salt);
        acc[1] ^= Xxh3Hash128.word(input, first)
            + Xxh3Hash128.word(input, first + 8);
    }

    private static long fold(
        final byte[] input,
        final int offset,
        final int position,
        final long salt
    ) {
        return Xxh3Hash128.multiply(
            Xxh3Hash128.word(input, offset)
                ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, position) + salt,
            Xxh3Hash128.word(input, offset + 8)
                ^ Xxh3Hash128.word(Xxh3Hash128.SECRET, position + 8) - salt
        );
    }

    private static long[] finish(
        final long[] acc,
        final int length,
        final long salt
    ) {
        return new long[]{
            Xxh3Hash128.avalanche(acc[0] + acc[1]),
            -Xxh3Hash128.avalanche(
                acc[0] * Xxh3Hash128.PRIME64_1
                    + acc[1] * Xxh3Hash128.PRIME64_4
                    + (length - salt) * Xxh3Hash128.PRIME64_2
            ),
        };
    }

    private static void accumulate(
        final long[] acc,
        final byte[] input,
        final int offset,
        final byte[] secret,
        final int stripes
    ) {
        for (int idx = 0; idx < stripes; ++idx) {
            Xxh3Hash128.stripe(
                acc,
                input,
                offset + idx * Xxh3Hash128.STRIPE,
                secret,
                idx * 8
            );
        }
    }

    private static void stripe(
        final long[] acc,
        final byte[] input,
        final int offset,
        final byte[] secret,
        final int position
    ) {
        for (int lane = 0; lane < acc.length; ++lane) {
            final long data = Xxh3Hash128.word(input, offset + lane * 8);
            final long keyed = data
                ^ Xxh3Hash128.word(secret, position + lane * 8);
            acc[lane ^ 1] += data;
            acc[lane] += (keyed & 0xFFFFFFFFL) * (keyed >>> 32);
        }
    }

    private static void scramble(final long[] acc, final byte[] secret) {
        final int position = secret.length - Xxh3Hash128.STRIPE;
        for (int lane = 0; lane < acc.length; ++lane) {
            acc[lane] = (
                acc[lane] ^ acc[lane] >>> 47
                    ^ Xxh3Hash128.word(secret, position + lane * 8)
                ) * Xxh3Hash128.PRIME32_1;
        }
    }

    private static long merge(
        final long[] acc,
        final byte[] secret,
        final int position,
        final long start
    ) {
        long result = start;
        for (int idx = 0; idx < 4; ++idx) {
            result += Xxh3Hash128.multiply(
                acc[2 * idx] ^ Xxh3Hash128.word(secret, position + 16 * idx),
                acc[2 * idx + 1]
                    ^ Xxh3Hash128.word(secret, position + 16 * idx + 8)
            );
        }
        return Xxh3Hash128.avalanche(result);
    }

    private static byte[] secret(final long salt) {
        final byte[] secret = new byte[Xxh3Hash128.SECRET.length];
        for (int idx = 0; idx < secret.length; idx += 16) {
            Xxh3Hash128.LONGS.set(
                secret,
                idx,
                Xxh3Hash128.word(Xxh3Hash128.SECRET, idx) + salt
            );
            Xxh3Hash128.LONGS.set(
                secret,
                idx + 8,
                Xxh3Hash128.word(Xxh3Hash128.SECRET, idx + 8) - salt
            );
        }
        return secret;
    }

    private static long multiply(final long left, final long right) {
        return left * right ^ Math.unsignedMultiplyHigh(left, right);
    }

    private static long avalanche(final long value) {
        final long mixed = (value ^ value >>> 37) * 0x165667919E3779F9L;
        return mixed ^ mixed >>> 32;
    }

    private static long spread(final long value) {
        long mixed = value;
        mixed ^= mixed >>> 33;
        mixed *= Xxh3Hash128.PRIME64_2;
        mixed ^= mixed >>> 29;
        mixed *= Xxh3Hash128.PRIME64_3;
        mixed ^= mixed >>> 32;
        return mixed;
    }

    private static long word(final byte[] bytes, final int offset) {
        return (long) Xxh3Hash128.LONGS.get(bytes, offset);
    }

    private static int half(final byte[] bytes, final int offset) {
        return (int) Xxh3Hash128.INTS.get(bytes, offset);
    }
}
