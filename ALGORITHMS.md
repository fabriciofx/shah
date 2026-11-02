# Algorithms

This file lists every hash function that
[SMHasher](https://github.com/rurban/smhasher) can test. The data comes from
the `g_hashes` table in `main.cpp` and from the results table in `README.md` of
rurban/smhasher at commit `e154a82` (2026-10-21). That fork extends the
[original SMHasher](https://github.com/aappleby/smhasher) by Austin Appleby,
and the **Orig.** column marks the functions that already existed there.

SMHasher tests 244 functions: 144 rated `GOOD`,
93 rated `POOR` and 7 rated `SKIP`.

## Columns

- **Bits**: width of the hash output.
- **Seed**: width of the seed that the algorithm itself accepts.
  SMHasher always passes a 32-bit seed and adapts it to each algorithm:
  - *none (XOR into state)*: the algorithm has no seed, so SMHasher XORs the
    seed into its initial state (cryptographic hashes, for example);
  - *none (ignored)*: the algorithm has no seed and SMHasher discards it;
  - *secret key*: the algorithm takes a secret key, random tables or a large
    seed buffer, and SMHasher derives it from the seed or copies the seed
    into part of it;
  - *undetermined*: the source lives in a git submodule that was not
    inspected.
- **Key**: every function hashes a key made of an arbitrary number of bytes,
  as SMHasher calls all of them through
  `hash(const void *key, int len, uint32_t seed, void *out)`.
- **Pair collision**: probability that two distinct keys collide for an
  ideal hash of that width, `2^-bits`.
- **50% collision**: number of random keys after which an ideal hash of that
  width has a 50% chance of at least one collision (birthday bound,
  `1.1774 × 2^(bits/2)`). Measured weaknesses appear under **Problems**.
- **Quality**: rating given by rurban/smhasher, either `GOOD` or `POOR`. A
  `GOOD` hash may still list minor problems. `SKIP` marks functions that are
  not real hashes, such as call overhead baselines and intentionally bad
  functions.
- **Verification**: SMHasher verification code, which checks that an
  implementation is correct.
- **MiB/s**, **Cycles/hash**, **Cycles/map** and **Size**: bulk speed, small
  key latency, hash map cost and object code size in bytes, as measured in
  rurban/smhasher.
- **Problems**: test failures and known weaknesses reported by SMHasher.
- **Secrets**: internal secrets and known bad seeds that SMHasher tests
  against; only the first three are shown.
- **Platform**: preprocessor condition required to build the function.
- **Orig.**: present in the original SMHasher by Austin Appleby.

## Table

<!-- markdownlint-disable MD013 -->
| # | Name | Bits | Seed | Pair collision | 50% collision | Quality | Verification | MiB/s | Cycles/hash | Cycles/map | Size | Problems | Secrets | Platform | Orig. | Description |
|--:|:-----|-----:|:-----|:---------------|--------------:|:--------|:-------------|------:|------------:|-----------:|-----:|:---------|:--------|:---------|:-----:|:------------|
| 1 | `donothing32` | 32 | none (ignored) | 2⁻³² | 77 163 | SKIP | `0x0` | 11536809.55 | 4.00 |  | 13 | bad seed 0, test NOP | `0` |  | ✓ | Do-Nothing function (measure call overhead) |
| 2 | `donothing64` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | SKIP | `0x0` | 12269832.97 | 4.00 |  | 13 | bad seed 0, test NOP | `0` |  | ✓ | Do-Nothing function (measure call overhead) |
| 3 | `donothing128` | 128 | none (ignored) | 2⁻¹²⁸ | 2.2 × 10¹⁹ | SKIP | `0x0` | 11961841.48 | 4.08 |  | 13 | bad seed 0, test NOP | `0` |  | ✓ | Do-Nothing function (measure call overhead) |
| 4 | `NOP_OAAT_read64` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | SKIP | `0x0` | 11652662.97 | 14.00 |  | 47 | test NOP |  |  |  | Noop function (measure call + OAAT reading overhead) |
| 5 | `BadHash` | 32 | 32 bits | 2⁻³² | 77 163 | SKIP | `0xAB432E23` | 794.68 | 72.80 |  | 48 | bad seed 0, test FAIL | `0` |  |  | very simple XOR shift |
| 6 | `sumhash` | 32 | 32 bits | 2⁻³² | 77 163 | SKIP | `0x0000A9AC` | 10184.67 | 29.40 |  | 417 | bad seed 0, test FAIL | `0` |  |  | sum all bytes |
| 7 | `sumhash32` | 32 | 32 bits | 2⁻³² | 77 163 | SKIP | `0x3D6DC280` | 44755.87 | 23.46 |  | 697 | UB, test FAIL | `0x9e3779b97f4a7c15` |  |  | sum all 32bit words |
| 8 | `multiply_shift` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x654E7474` | 8286.63 | 25.39 | 209.06 (2) | 345 | bad seeds & 0xfffffff0, fails most tests | `0xfffffff0`, `0x1fffffff0`, `0xb13dea7c9c324e51` (+1) | `#ifdef __SIZEOF_INT128__` |  | Dietzfelbinger Multiply-shift on strings |
| 9 | `pair_multiply_shift` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x26A5AB8C` | 5728.05 | 39.67 | 218.48 (12) | 609 | fails most tests | `0xb13dea7c9c324e51`, `0x75f17d6b3588f843` | `#ifdef __SIZEOF_INT128__` |  | Pair-multiply-shift |
| 10 | `crc32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x3719DB20` | 369.88 | 142.33 | 396.81 (101) | 363 | insecure, 8590x collisions, distrib, PerlinNoise |  |  | ✓ | CRC-32 soft |
| 11 | `md5-128` | 128 | none (XOR into state) | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xF263F96F` | 344.63 | 669.59 | 856.34 (5) | 557 |  |  |  |  | MD5 |
| 12 | `md5_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x12F0BA8E` | 360.20 | 640.38 | 869.55 (6) | 662 |  |  |  |  | MD5, bits 32-95 |
| 13 | `md5_32` | 32 | none (XOR into state) | 2⁻³² | 77 163 | GOOD | `0xF3DFF19F` | 359.43 | 638.92 | 865.21 (6) | 598 |  |  |  |  | MD5, bits 32-63 |
| 14 | `sha1-160` | 160 | none (XOR into state) | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | GOOD | `0x6AF411D8` | 364.95 | 1470.55 | 1794.16 (13) | 5126 | Comb/Cyclic low32 |  |  |  | SHA1 |
| 15 | `sha1_32` | 32 | none (XOR into state) | 2⁻³² | 77 163 | GOOD | `0xB3122757` | 353.03 | 1385.80 | 1759.94 (5) | 5126 | Sanity, Cyclic low32, 36.6% distrib |  |  |  | SHA1, low 32 bits |
| 16 | `sha1_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x995397D5` | 353.03 | 1385.80 | 1759.94 (5) | 5126 | Sanity, Cyclic low32, 36.6% distrib |  |  |  | SHA1, low 64 bits |
| 17 | `sha2-224` | 224 | none (XOR into state) | 2⁻²²⁴ | 6.1 × 10³³ | POOR | `0x407AA518` | 147.13 | 1354.81 | 1589.92 (12) | 695 | Comb low32 |  |  |  | SHA2-224 |
| 18 | `sha2-224_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xF3E40ECA` | 147.60 | 1360.10 | 1620.93 (13) | 600 | Cyclic low32 |  |  |  | SHA2-224, low 64 bits |
| 19 | `sha2-256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xEBDA2FB1` | 147.80 | 1374.90 | 1606.06 (16) | 663 |  |  |  |  | SHA2-256 |
| 20 | `sha2-256_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xC1C4FA72` | 148.01 | 1376.34 | 1624.71 (16) | 600 |  |  |  |  | SHA2-256, low 64 bits |
| 21 | `sha1ni` | 160 | none (XOR into state) | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | POOR | `0x375755A4` | 1632.88 | 170.77 | 379.84 (5) | 1321 | insecure,sanity, Permutation, Zeroes, machine-specific | `0x67452301` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` |  | SHA1\_NI (amd64 HW SHA ext) |
| 22 | `sha1ni_32` | 32 | none (XOR into state) | 2⁻³² | 77 163 | GOOD | `0xE70686CC` | 1583.50 | 172.17 | 387.70 (4) | 1273 | machine-specific | `0x67452301` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` |  | hardened SHA1\_NI (amd64 HW SHA ext), low 32 bits |
| 23 | `sha2ni-256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | POOR | `0x4E3BB25E` | 1556.66 | 180.75 | 393.79 (5) | 1495 | insecure,sanity, Permutation, Zeroes, machine-specific | `0x6a09e667` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` |  | SHA2\_NI-256 (amd64 HW SHA ext) |
| 24 | `sha2ni-256_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xF938E80E` | 1508.82 | 184.89 | 393.07 (6) | 1409 | Zeroes, machine-specific | `0x6a09e667` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` |  | hardened SHA2\_NI-256 (amd64 HW SHA ext), low 64 bits |
| 25 | `rmd128` | 128 | none (XOR into state) | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xFF576977` | 294.42 | 712.67 | 930.65 (4) | 950 |  |  |  |  | RIPEMD-128 |
| 26 | `rmd160` | 160 | none (XOR into state) | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | GOOD | `0x30B37AC6` | 202.16 | 1045.79 | 1287.74 (16) | 950 | Cyclic hi32 |  |  |  | RIPEMD-160 |
| 27 | `rmd256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xEB16FAD7` | 366.21 | 615.39 | 830.04 (7) | 1026 |  |  |  |  | RIPEMD-256 |
| 28 | `edonr224` | 224 | none (XOR into state) | 2⁻²²⁴ | 6.1 × 10³³ | GOOD | `0x83A8E7AB` | 863.77 | 304.76 | 496.66 (3) | 834 |  |  | `#ifdef HAVE_EDONR` |  | EDON-R 224 |
| 29 | `edonr256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x06DD4F96` | 870.97 | 296.40 | 517.82 (6) | 834 |  |  | `#ifdef HAVE_EDONR` |  | EDON-R 256 |
| 30 | `blake3_c` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x50E4CD91` | 1298.04 | 354.25 | 563.63 (4) | 16462 | no 32bit portability | `0x6a09e667` | `#ifndef HAVE_ASAN` |  | BLAKE3 c |
| 31 | `Tachyon` | 256 | 64 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xE9BBF229` | 460.14 | 370.28 | 851.29 (2) |  | AES only |  |  |  | Tachyon 256-bit C |
| 32 | `blake3` | 256 | none (ignored) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x0` |  |  |  |  |  |  | `#if defined(HAVE_BLAKE3)` |  | BLAKE3 Rust |
| 33 | `blake3_64` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` |  |  |  |  |  |  | `#if defined(HAVE_BLAKE3)` |  | BLAKE3 Rust, low 64 bits |
| 34 | `blake2s-128` | 128 | none (XOR into state) | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xE8D8FCDF` | 295.30 | 698.09 | 1059.24 (51) | 329 |  |  |  |  | blake2s-128 |
| 35 | `blake2s-160` | 160 | none (XOR into state) | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | GOOD | `0xD50FF144` | 215.01 | 1026.74 | 1239.54 (11) | 329 |  |  |  |  | blake2s-160 |
| 36 | `blake2s-224` | 224 | none (XOR into state) | 2⁻²²⁴ | 6.1 × 10³³ | GOOD | `0x19B36D2C` | 207.06 | 1063.86 | 1236.50 (20) | 329 |  |  |  |  | blake2s-224 |
| 37 | `blake2s-256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x841D6354` | 215.28 | 1014.88 | 1230.38 (28) | 329 |  |  |  |  | blake2s-256 |
| 38 | `blake2s-256_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x53000BB2` | 211.52 | 1044.22 | 1228.43 (8) | 323 |  |  |  |  | blake2s-256, low 64 bits |
| 39 | `blake2b-160` | 160 | none (XOR into state) | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | GOOD | `0x28ADDA30` | 356.08 | 1236.84 | 1458.15 (12) | 505 |  |  |  |  | blake2b-160 |
| 40 | `blake2b-224` | 224 | none (XOR into state) | 2⁻²²⁴ | 6.1 × 10³³ | GOOD | `0x101A62A4` | 356.59 | 1228.50 | 1425.87 (16) | 505 |  |  |  |  | blake2b-224 |
| 41 | `blake2b-256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xC9D8D995` | 355.97 | 1232.22 | 1443.31 (19) | 505 |  |  |  |  | blake2b-256 |
| 42 | `blake2b-256_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xCF4F7EC3` | 356.97 | 1222.76 | 1435.03 (9) | 501 |  |  |  |  | blake2b-256, low 64 bits |
| 43 | `asconhashv12` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xA969C160` | 144.98 | 885.02 | 1324.23 (38) | 4341 |  | `0xee9398aadb67f03d` |  |  | asconhashv12 256bit |
| 44 | `asconhashv12_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xE7DEF300` | 159.33 | 402.54 | 473.15 (4) | 6490 |  | `0xee9398aadb67f03d` |  |  | asconhashv12, low 64 bits |
| 45 | `sha3-256` | 256 | none (XOR into state) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | POOR | `0x21048CE3` | 100.58 | 3877.18 | 4159.79 (37) | 1686 | PerlinNoise |  |  |  | SHA3-256 (Keccak) |
| 46 | `sha3-256_64` | 64 | none (XOR into state) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xE62E5CC0` | 100.57 | 3909.00 | 4174.63 (16) | 1630 | PerlinNoise |  |  |  | SHA3-256 (Keccak), low 64 bits |
| 47 | `hasshe2` | 256 | 32 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | POOR | `0xF5D39DFE` | 2879.93 | 62.47 | 272.34 (2) | 505 | Permutation,TwoBytes,Zeroes,Seed |  | `#if defined(HAVE_SSE2)` |  | SSE2 hasshe2, 256-bit |
| 48 | `poly_1_mersenne` | 32 | secret key | 2⁻³² | 77 163 | POOR | `0x0` | 1278.37 | 63.42 | 244.41 (1) | 479 | fails most tests |  | `#ifdef __SIZEOF_INT128__` |  | Degree 1 Hashing mod 2^61-1 |
| 49 | `poly_2_mersenne` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0x0` | 1391.90 | 69.97 | 255.44 (3) | 479 |  | `0x60e8512c` | `#ifdef __SIZEOF_INT128__` |  | Degree 2 Hashing mod 2^61-1 |
| 50 | `poly_3_mersenne` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0x0` | 1320.15 | 80.81 | 263.12 (2) | 479 |  | `0x3d25f745` | `#ifdef __SIZEOF_INT128__` |  | Degree 3 Hashing mod 2^61-1 |
| 51 | `poly_4_mersenne` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0x0` | 1393.93 | 89.07 | 262.97 (2) | 479 |  |  | `#ifdef __SIZEOF_INT128__` |  | Degree 4 Hashing mod 2^61-1 |
| 52 | `tabulation` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xB49C607C` | 7407.94 | 39.83 | 240.25 (4) | 554 |  |  | `#ifdef __SIZEOF_INT128__` |  | 64-bit Tabulation with Multiply-Shift Mixer |
| 53 | `tabulation32` | 32 | secret key | 2⁻³² | 77 163 | POOR | `0x335F64EA` | 5819.40 | 40.16 | 233.00 (3) | 848 | collisions |  |  |  | 32-bit Tabulation with Multiply-Shift Mixer |
| 54 | `crc32_hw` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x0C7346F0` | 5537.79 | 40.80 | 225.65 (3) | 653 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (SSE4.2/NEON) | `0x111c2232` | `#ifdef HAVE_SSE42` |  | SSE4.2 crc32 in HW |
| 55 | `crc64_hw` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xE7C3FD0E` | 5579.17 | 40.87 | 202.19 (1) | 652 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (SSE4.2/NEON) | `0x0` | `#ifdef HAVE_SSE42` |  | SSE4.2 crc64 in HW |
| 56 | `crc32_hw1` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x0C7346F0` | 7626.17 | 50.21 | 228.50 (2) | 696 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (x86 SSE4.2) | `0x111c2232` | `#ifdef HAVE_SSE42` |  | Faster Adler SSE4.2 crc32 on Intel HW |
| 57 | `crc32_pclmul` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x0` | 7963.20 | 106.02 | 567.70 (3) | 254 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (x86 SSE4.2+PCLMUL) | `0x0` | `#if defined(HAVE_CLMUL) && !defined(_MSC_VER) && defined(__x86_64__)` |  | -mpclmul crc32 in asm on HW |
| 58 | `crc64_jones1` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x7DC1B496` | 1673.67 | 83.00 | 249.16 (2) |  | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` |  | crc64, jones polynomial, standard fast software method from Mark Adler |
| 59 | `crc64_jones2` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x7DC1B496` | 2968.07 | 314.60 | 253.01 (3) |  | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` |  | crc64, jones polynomial, 2 pipelines in 1 thread, joined at the end |
| 60 | `crc64_jones3` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x7DC1B496` | 3398.80 | 302.59 | 258.16 (8) |  | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` |  | crc64, jones polynomial, 3 pipelines in 1 thread, joined at the end |
| 61 | `crc64_jones` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x7DC1B496` | 3512.66 | 80.70 | 251.40 (2) |  | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` |  | crc64, jones polynomial, auto 1-3 pipelines depending on length, joined at the end |
| 62 | `o1hash` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x85051E87` | 11530366.92 | 18.40 | 206.94 (1) | 101 | insecure, no seed, zeros, fails all tests | `0x0` | `#ifdef HAVE_INT64` |  | o(1)hash unseeded, from wyhash |
| 63 | `fhtw` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x0` |  |  |  |  |  |  | `#if 0 && defined(__x86_64__) && (defined(__linux__) \|\| defined(__APPLE__))` |  | fhtw asm |
| 64 | `fibonacci` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xFE3BD380` | 15409.62 | 22.67 | 872.83 (3) | 1692 | UB, zeros, fails all tests | `0x0`, `0xffffffff00000000` |  |  | wordwise Fibonacci |
| 65 | `k-hash32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x99B3FFCD` | 2227.90 | 53.85 | 255.78 (2) | 808 | insecure, zeros, UB, bad seeds, fails all tests |  | `#ifndef HAVE_ALIGNED_ACCESS_REQUIRED` |  | K-Hash mixer, 32-bit |
| 66 | `k-hash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xAB5518A1` | 2492.66 | 48.18 | 242.58 (2) | 609 | insecure, zeros, UB, bad seeds, fails all tests |  | `#ifndef HAVE_ALIGNED_ACCESS_REQUIRED` |  | K-Hash mixer, 64-bit |
| 67 | `FNV1a` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xE3CBBE91` | 754.70 | 75.28 | 226.30 (2) | 54 | bad seed, zeros, fails all tests | `0x811c9dc5` |  |  | Fowler-Noll-Vo hash, 32-bit |
| 68 | `FNV1A_Totenschiff` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x95D95ACF` | 6166.85 | 26.60 | 235.71 (2) | 270 | UB, zeros, fails all tests | `0x811c9dc5` | `#ifdef HAVE_INT64` |  | FNV1A\_Totenschiff\_v1 64-bit sanmayce |
| 69 | `FNV1A_Pippip_Yurii` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xDFE2D53C` | 24196.44 | 86.25 | 539.41 (3) | 603 | bad seeds, UB, sanity, x64 AESNI only | `0x1077bd26`, `0x1c07e567`, `0x1c2e04b6` (+7) | `#ifdef HAVE_INT64` and `#if defined(HAVE_AESNI) && defined(__SIZEOF_INT128__) && (defined(__x86_64__) \|\| defined(_M_AMD64))` |  | FNV1A-Pippip\_Yurii\_OOO\_128bit\_AES\_TriXZi\_Mikayla 32-bit sanmayce |
| 70 | `FNV1a_YT` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xD8AFFD71` | 13481.70 | 30.67 | 233.71 (7) | 336 | bad seed, UB, fails all tests | `0x811c9dc5`, `0x23d4a49d` | `#ifdef HAVE_INT64` |  | FNV1a-YoshimitsuTRIAD 32-bit sanmayce |
| 71 | `FNV64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x103455FC` | 747.70 | 74.18 | 189.18 (1) | 70 | fails all tests | `0x811c9dc5`, `0xcbf29ce4`, `0x84222325` (+1) | `#ifdef HAVE_INT64` |  | Fowler-Noll-Vo hash, 64-bit |
| 72 | `FNV128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0xC6FF4526` | 408.59 | 130.82 | 299.47 (20) | 153 | fails all tests |  | `#ifdef HAVE_INT64` |  | Go variant of FNV, 128-bit |
| 73 | `FNV2` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x1967C625` | 5630.87 | 32.09 | 207.08 (1) | 278 | fails all tests |  |  |  | wordwise FNV |
| 74 | `fletcher2` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x890767C0` | 15410.12 | 20.72 | 345.55 (5) | 117 | bad seed 0, UB, fails all tests | `0` |  |  | fletcher2 ZFS |
| 75 | `fletcher4` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x890767C0` | 15603.68 | 21.24 | 320.09 (2) | 117 | bad seed 0, UB, fails all tests | `0` |  |  | fletcher4 ZFS |
| 76 | `bernstein` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xBDB4B640` | 1032.00 | 59.04 | 225.09 (3) | 56 | bad seed 0, fails all tests | `0` |  | ✓ | Bernstein, 32-bit |
| 77 | `sdbm` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x582AF769` | 772.01 | 71.67 | 220.09 (2) | 54 | bad seed 0, fails all tests | `0` |  |  | sdbm as in perl5 |
| 78 | `x17` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x8128E14C` | 765.01 | 75.61 | 225.81 (2) | 65 | 99.98% bias, fails all tests |  |  |  | x17 |
| 79 | `libiberty` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x584FBC20` | 618.39 | 86.58 | 220.95 (2) | 37 | insecure, 100% bias, fails all tests, bad seed | `0x2ba97ba0` |  |  | libiberty htab\_hash\_string |
| 80 | `gcc` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xC6239327` | 612.10 | 88.44 | 224.62 (2) | 55 | insecure, 100% bias, fails all tests, bad seed | `0xbaa0af90` |  |  | gcc libcpp |
| 81 | `JenkinsOOAT` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x83E133DA` | 615.60 | 111.23 | 251.08 (2) | 150 | bad seed 0, 53.5% bias, fails all tests | `0` |  |  | Bob Jenkins' OOAT as in perl 5.18 |
| 82 | `JenkinsOOAT_perl` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xEE05869B` | 631.79 | 93.13 | 232.44 (1) | 65 | bad seed 0, 1.5-11.5% bias, 7.2x collisions, BIC, LongNeighbors | `0` |  |  | Bob Jenkins' OOAT as in old perl5 |
| 83 | `pearsonhash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x12E4C8CD` | 439.85 | 123.07 | 213.68 (1) |  | Avalanche, Seed, SSSE3 only. broken MSVC |  | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` |  | Pearson hash, 64-bit SSSE3 |
| 84 | `pearsonhash128` | 128 | none (ignored) | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0x6CCBB7B3` | 438.49 | 123.81 | 212.89 (2) |  | Avalanche, Seed, SSSE3 only. broken MSVC |  | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` |  | Pearson hash, 128-bit SSSE3, low 64-bit |
| 85 | `pearsonhash256` | 256 | none (ignored) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | POOR | `0x7F8BEB21` | 440.63 | 120.72 | 224.63 (2) |  | Avalanche, Seed, SSSE3 only. broken MSVC |  | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` |  | Pearson hash, 256-bit SSSE3, low 64-bit |
| 86 | `pearsonbhash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x14C3D184` | 1749.98 | 99.84 | 257.11 (2) | 683 |  |  | `#ifdef HAVE_INT64` |  | Pearson block hash, 64-bit |
| 87 | `pearsonbhash128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x6BEFE6EA` | 1656.68 | 106.78 | 276.92 (3) | 1134 |  |  | `#ifdef HAVE_INT64` |  | Pearson block hash, 128-bit, low 64-bit |
| 88 | `pearsonbhash256` | 256 | 64 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x999B3C19` | 1418.61 | 123.68 | 301.26 (3) | 844 |  |  | `#ifdef HAVE_INT64` |  | Pearson block hash, 256-bit, low 64-bit |
| 89 | `VHASH_32` | 32 | secret key | 2⁻³² | 77 163 | POOR | `0xF0077651` | 13084.37 | 65.45 | 280.14 (2) | 1231 | sanity, Seed, MomentChi2 |  |  |  | VHASH\_32 by Ted Krovetz and Wei Dai |
| 90 | `VHASH_64` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xF97D84FE` | 13217.64 | 64.90 | 270.04 (2) | 1231 | sanity, Seed, Sparse |  |  |  | VHASH\_64 by Ted Krovetz and Wei Dai |
| 91 | `MicroOAAT` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x16F1BA97` | 730.89 | 78.25 | 236.07 (3) | 78 | 100% bias, distrib, BIC | `0x3b00` |  |  | Small non-multiplicative OAAT (by funny-falcon) |
| 92 | `farsh32` | 32 | 64 bits | 2⁻³² | 77 163 | POOR | `0xBCDE332C` | 27583.89 | 65.65 | 266.64 (2) | 944 | insecure: AppendedZeroes, collisions+bias, MomentChi2, LongNeighbors |  | `#ifdef HAVE_SSE2` |  | FARSH 32bit |
| 93 | `farsh64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xDE2FDAEE` | 13558.69 | 113.74 | 327.26 (3) | 944 | insecure: AppendedZeroes, collisions+bias, MomentChi2, LongNeighbors |  | `#ifdef HAVE_SSE2` |  | FARSH 64bit |
| 94 | `farsh128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0x82B6CBEC` | 7055.39 | 229.42 | 375.57 (4) | 944 | insecure: AppendedZeroes, collisions+bias, permut,combin,2bytes,zeroes,PerlinNoise |  | `#ifdef HAVE_SSE2` |  | FARSH 128bit |
| 95 | `farsh256` | 256 | 64 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | POOR | `0xFEBEA0BC` | 3466.26 | 444.74 | 610.23 (4) | 944 | insecure: AppendedZeroes, collisions+bias, permut,combin,2bytes,zeroes,PerlinNoise |  | `#ifdef HAVE_SSE2` |  | FARSH 256bit |
| 96 | `jodyhash32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xA2AEFC60` | 1762.46 | 42.66 | 236.09 (2) | 153 | bias, collisions, distr, BIC LongNeighbors | `0x9fd368f4` |  |  | jodyhash, 32-bit (v6) |
| 97 | `jodyhash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xC1CBFA34` | 4861.84 | 44.05 | 234.35 (2) | 1036 | bias, collisions, distr, BIC, LongNeighbors |  | `#ifdef HAVE_INT64` |  | jodyhash, 64-bit (v7.1) |
| 98 | `lookup3` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x3D83917A` | 2474.09 | 40.19 | 238.22 (3) | 408 | UB, 28% bias, collisions, 30% distr, BIC | `0x21524101` |  | ✓ | Bob Jenkins' lookup3 |
| 99 | `superfast` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x0C80403A` | 2085.79 | 49.89 | 230.76 (3) | 210 | UB, bad seed 0, 91% bias, 5273.01x collisions, 37% distr, BIC | `0x0` |  | ✓ | Paul Hsieh's SuperFastHash, CHAR\_MIN |
| 100 | `MurmurOAAT` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x5363BD98` | 513.75 | 105.23 | 244.81 (4) | 45 | bad seed 0, collisions, 99.998% distr., BIC, LongNeighbors | `0x0` |  | ✓ | Murmur one-at-a-time |
| 101 | `Crap8` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x743E97A1` | 3081.04 | 37.15 | 234.94 (2) | 245 | UB, 2.42% bias, collisions, 2% distrib |  |  | ✓ | Crap8 |
| 102 | `xxHash32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xBA88B743` | 6064.37 | 48.86 | 234.27 (3) | 776 | LongNeighbors, collisions with 4bit diff, MomentChi2 220 |  |  |  | xxHash, 32-bit for x86 |
| 103 | `Murmur1` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x9EA7D056` | 1955.36 | 48.84 | 236.25 (2) | 197 | UB, 1 bad seed, 511x collisions, Diff, BIC | `0xc6a4a793` |  |  | MurmurHash1 |
| 104 | `Murmur2` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x27864C1E` | 3082.03 | 41.62 | 250.72 (4) | 185 | UB, 1 bad seed, 1.7% bias, 81x coll, 1.7% distrib, BIC | `0x10` |  | ✓ | MurmurHash2 for x86, 32-bit |
| 105 | `Murmur2A` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0x7FBD4396` | 2850.40 | 46.60 | 284.58 (13) | 244 | UB, 1 bad seed, 12.7% bias, LongNeighbors | `0x2fc301c9` |  | ✓ | MurmurHash2A for x86, 32-bit |
| 106 | `Murmur2B` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x1F0D3804` | 6039.96 | 38.70 | 212.23 (1) | 307 | UB, 1.8% bias, collisions, 3.4% distrib, BIC | `0xc6a4a7935bd1e995` | `#if __WORDSIZE >= 64` | ✓ | MurmurHash64A for x64, 64-bit |
| 107 | `Murmur2C` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xDD537C05` | 3802.68 | 49.82 | 220.13 (2) | 311 | UB, 2^32 bad seeds, 91% bias, collisions, distr, BIC, LongNeighbors | `0x10`, `0xffffffff00000010` | `#ifdef HAVE_INT64` | ✓ | MurmurHash64B for x86, 64-bit |
| 108 | `Murmur3A` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xB0F57EE3` | 3027.30 | 48.99 | 234.49 (2) | 264 | UB, 1 bad seed, Moment Chi2 69 | `0xfca58b2d` |  | ✓ | MurmurHash3 for x86, 32-bit |
| 109 | `PMurHash32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xB0F57EE3` | 3001.44 | 48.99 | 240.35 (3) | 1862 | UB, 1 bad seed, Moment Chi2 69 | `0xfca58b2d` |  | ✓ | Shane Day's portable-ized MurmurHash3 for x86, 32-bit |
| 110 | `Murmur3C` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0xB3ECE62A` | 4824.95 | 57.39 | 243.91 (2) | 780 | UB, LongNeighbors, Text, DiffDist | `0x239b961b` |  | ✓ | MurmurHash3 for x86, 128-bit |
| 111 | `PMPML_32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xEAE2E3CC` | 6904.30 | 44.25 | 233.59 (2) | 1020 | Avalanche >512, unseeded: Seed, BIC, MomentChi2, PerlinNoise |  | `#if !defined(DEBUG) && !defined(CROSSCOMPILING) && !defined(__aarch64__)` |  | PMP\_Multilinear 32-bit unseeded |
| 112 | `PMPML_64` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x584CC9DF` | 10030.67 | 49.22 | 239.07 (5) | 1299 | unseeded: Seed, MomentChi2, BIC |  | `#if !defined(DEBUG) && !defined(CROSSCOMPILING) && !defined(__aarch64__)` |  | PMP\_Multilinear 64-bit unseeded |
| 113 | `City32` | 32 | 32 bits | 2⁻³² | 77 163 | POOR | `0xEDED9084` | 5745.83 | 52.44 | 242.69 (2) | 1057 |  | `0x2eb38c9f` |  |  | Google CityHash32WithSeed (v1.1) |
| 114 | `metrohash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x6FA828C9` | 14209.14 | 40.85 | 225.16 (2) | 647 | UB, LongNeighbors, BIC |  | `#ifdef HAVE_INT64` |  | MetroHash64, 64-bit |
| 115 | `metrohash64_1` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xEE88F7D2` | 14495.30 | 40.83 | 213.00 (2) | 624 | UB, LongNeighbors, BIC, MomentChi2 |  | `#ifdef HAVE_INT64` |  | MetroHash64\_1, 64-bit (legacy) |
| 116 | `metrohash64_2` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xE1FC7C6E` | 14359.39 | 41.09 | 215.53 (2) | 627 | UB, LongNeighbors |  | `#ifdef HAVE_INT64` |  | MetroHash64\_2, 64-bit (legacy) |
| 117 | `metrohash128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x4A6673E7` | 15847.63 | 72.33 | 266.54 (2) | 878 | UB, LongNeighbors |  | `#ifdef HAVE_INT64` |  | MetroHash128, 128-bit |
| 118 | `metrohash128_1` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x20E8A1D7` | 15556.40 | 73.49 | 259.05 (2) | 894 | UB, LongNeighbors |  | `#ifdef HAVE_INT64` |  | MetroHash128\_1, 128-bit (legacy) |
| 119 | `metrohash128_2` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x5437C684` | 15408.56 | 74.36 | 259.57 (2) | 894 | UB, LongNeighbors |  | `#ifdef HAVE_INT64` |  | MetroHash128\_2, 128-bit (legacy) |
| 120 | `metrohash64crc_1` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x29C68A50` | 8010.90 | 44.43 | 213.94 (2) | 674 | UB, Cyclic 8/8 byte, DiffDist, BIC, MomentChi2, machine-specific (SSE4.2/NEON) |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | MetroHash64crc\_1 for x64 (legacy) |
| 121 | `metrohash64crc_2` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x2C00BD9F` | 7939.65 | 44.84 | 222.01 (2) | 626 | UB, Cyclic 8/8 byte, DiffDist, BIC, machine-specific (SSE4.2/NEON) |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | MetroHash64crc\_2 for x64 (legacy) |
| 122 | `cmetrohash64_1o` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xEE88F7D2` | 14678.56 | 40.33 | 216.44 (2) | 3506 | UB, LongNeighbors, BIC, MomentChi2 |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | cmetrohash64\_1 (shorter key optimized), 64-bit for x64 |
| 123 | `cmetrohash64_1` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xEE88F7D2` | 14332.75 | 41.04 | 216.05 (2) | 651 | UB, LongNeighbors, BIC, MomentChi2 |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | cmetrohash64\_1, 64-bit for x64 |
| 124 | `cmetrohash64_2` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xE1FC7C6E` | 14498.20 | 40.87 | 219.65 (2) | 647 | LongNeighbors |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | cmetrohash64\_2, 64-bit for x64 |
| 125 | `metrohash128crc_1` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x5E75144E` | 8182.02 | 77.38 | 256.80 (2) | 723 | UB, machine-specific (SSE4.2/NEON) |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | MetroHash128crc\_1 for x64 (legacy) |
| 126 | `metrohash128crc_2` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x1ACF3E77` | 7996.33 | 78.81 | 262.53 (2) | 723 | UB, machine-specific (SSE4.2/NEON) |  | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` |  | MetroHash128crc\_2 for x64 (legacy) |
| 127 | `City64noSeed` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x4C4E54B1` | 14023.27 | 33.37 | 223.59 (2) | 1038 | Avalanche, Sparse, TwoBytes, MomentChi2, Seed |  |  |  | Google CityHash64 without seed (v1.1)(default version, misses one final avalanche) |
| 128 | `City64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x5FABC5C5` | 14390.09 | 46.69 | 231.99 (2) | 1120 | Sparse, TwoBytes |  |  | ✓ | Google CityHash64WithSeed (v1.1) |
| 129 | `aesnihash` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xA68E0D42` | 5365.60 | 57.21 | 255.87 (3) | 590 | fails many tests, machine-specific (x64 AES-NI) | `0x70736575` | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` |  | majek's seeded aesnihash with aesenc, 64-bit for x64 |
| 130 | `aesni` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xF06DA1B1` | 31185.98 | 29.45 | 226.75 (2) | 519 | machine-specific (x64 AES-NI) |  | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` |  | aesni 128bit |
| 131 | `aesni-low` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x3AA1A480` | 31027.39 | 29.47 | 232.54 (2) | 519 | machine-specific (x64 AES-NI) |  | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` |  | aesni 64bit |
| 132 | `aesni-hash-peterrk` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x4E311231` | 29107.73 | 28.86 | 217.57 (1) |  | machine-specific (x64 AES-NI) |  | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI)` |  | PeterRK's seeded aesnihash, 128-bit for x64, unportable |
| 133 | `falkhash` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x2F99B071` | 52401.48 | 122.70 | 316.79 (4) | 264 | Sparse, LongNeighbors, machine-specific (x64 AES-NI) |  | `#if defined(HAVE_SSE2) && defined(__x86_64__) && !defined(_WIN32) && !defined(_MSC_VER)` |  | falkhash.asm with aesenc, 64-bit for x64 |
| 134 | `MeowHash32low` | 32 | secret key | 2⁻³² | 77 163 | POOR | `0x8872DE1A` | 30562.54 | 63.77 | 283.26 (3) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` |  | MeowHash (requires x64 AES-NI) |
| 135 | `MeowHash64low` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xB04AC842` | 29438.45 | 63.76 | 269.41 (4) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` |  | MeowHash (requires x64 AES-NI) |
| 136 | `MeowHash` | 128 | secret key | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0xA0D29861` | 29969.81 | 64.90 | 273.79 (8) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` |  | MeowHash (requires x64 AES-NI) |
| 137 | `t1ha1_64le` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xD6836381` | 13425.03 | 31.37 | 221.97 (2) | 612 | Avalanche |  |  |  | Fast Positive Hash (portable, aims 64-bit, little-endian) |
| 138 | `t1ha1_64be` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x93F864DE` | 12002.50 | 31.57 | 226.52 (2) | 639 | Avalanche |  |  |  | Fast Positive Hash (portable, aims 64-bit, big-endian) |
| 139 | `t1ha0_32le` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0x7F7D7B29` | 7276.16 | 49.11 | 236.39 (2) | 594 | Sparse, LongNeighbors |  |  |  | Fast Positive Hash (portable, aims 32-bit, little-endian) |
| 140 | `t1ha0_32be` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xDA6A4061` | 6860.87 | 50.16 | 241.26 (2) | 607 | Sparse, LongNeighbors |  |  |  | Fast Positive Hash (portable, aims 32-bit, big-endian) |
| 141 | `tifuhash_64` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 35.60 | 1679.52 | 1212.75 (15) | 276 | Cyclic low32 |  |  |  | Tiny Floatingpoint Unique Hash with continued egyptian fractions |
| 142 | `beamsplitter` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 789.22 | 682.45 | 1150.33 (26) | 4203 | UB |  |  |  | A possibly universal hash made with a 10x64 s-box. |
| 143 | `discohash1` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xBEBB4185` | 4152.62 | 202.14 | 414.34 (4) | 1294 |  |  |  |  | DISCoHAsH 64 (was BEBB4185) v1 |
| 144 | `discohash1-128` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x05C0460C` | 4064.39 | 231.06 | 430.94 (6) | 1294 |  |  |  |  | DISCoHAsH v1 |
| 145 | `discohash2` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x8FF45ABF` | 4026.84 | 204.37 | 408.13 (6) | 1294 |  |  |  |  | DISCoHAsH v2 |
| 146 | `discohash2-128` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x95E58C14` | 4153.48 | 226.54 | 429.24 (4) | 1294 |  |  |  |  | DISCoHAsH v2 |
| 147 | `discoNONG` | 64 | none (ignored) | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x9182A886` | 3661.23 | 397.81 | 651.23 (54) | 2995 | bad seeds | `0x00005147`, `0x00009dce`, `0x0000addc` (+1028) |  |  | discoNONG - discohash with 512-bit internal state 64-bit output |
| 148 | `fasthash32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0xE9481AFC` | 6107.05 | 40.49 | 237.83 (2) | 566 | UB | `0x880355f21e6d1965` |  |  | fast-hash 32bit |
| 149 | `fasthash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xA16231A7` | 5600.11 | 38.04 | 219.71 (2) | 509 | UB | `0x880355f21e6d1965` |  |  | fast-hash 64bit |
| 150 | `floppsyhash` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 35.72 | 1868.92 | 1411.07 (7) | 616 |  |  |  |  | slow hash designed for floating point hardware |
| 151 | `chaskey` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xBB4F6706` | 1142.38 | 113.98 | 288.04 (1) | 1450 | PerlinNoise |  |  |  | mouha.be/chaskey/ with added seed support |
| 152 | `SipHash` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xC58D7F9C` | 953.51 | 147.32 | 332.02 (5) | 1071 |  |  |  |  | SipHash 2-4 - SSSE3 optimized |
| 153 | `HalfSipHash` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0xA7A05F72` | 1128.85 | 80.48 | 260.42 (2) | 603 | zeroes |  |  |  | HalfSipHash 2-4, 32bit |
| 154 | `GoodOAAT` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x7B14EEE5` | 744.67 | 87.23 | 243.36 (2) | 119 |  | `0x3b00` |  |  | Small non-multiplicative OAAT |
| 155 | `prvhash64_64m` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x1A3F7B06` | 3573.29 | 51.42 | 477.94 (4) | 403 |  |  | `#ifdef HAVE_INT64` |  | prvhash64m 4.3.4 64bit |
| 156 | `prvhash64_64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x1A3F7B06` | 3379.95 | 51.12 | 516.80 (46) | 387 |  |  | `#ifdef HAVE_INT64` |  | prvhash64 4.3.4 64bit |
| 157 | `prvhash64_128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x316B4D9F` | 3352.65 | 69.16 | 495.87 (5) | 657 |  |  | `#ifdef HAVE_INT64` |  | prvhash64 4.3.4 128bit |
| 158 | `prvhash64s_64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x891521D6` | 6724.41 | 266.09 | 438.90 (3) | 5442 |  |  | `#ifdef HAVE_INT64` |  | prvhash64s 4.3.4 64bit |
| 159 | `prvhash64s_128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x0199728A` | 6703.66 | 326.46 | 508.67 (3) | 5618 |  |  | `#ifdef HAVE_INT64` |  | prvhash64s 4.3.4 128bit |
| 160 | `SipHash13` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x29C010BF` | 1803.96 | 107.93 | 304.05 (3) | 772 | 0.9% bias |  |  |  | SipHash 1-3 - SSSE3 optimized |
| 161 | `TSip` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x5C2395A5` | 4330.87 | 52.43 | 245.31 (2) | 393 | !msvc |  | `#ifndef _MSC_VER` |  | Damian Gryski's Tiny SipHash variant |
| 162 | `seahash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xF0374078` | 8240.58 | 58.61 | 260.35 (1) | 871 | PerlinNoise, !msvc |  | `#ifndef _MSC_VER` and `#ifdef HAVE_INT64` |  | seahash (64-bit, little-endian) |
| 163 | `seahash32low` | 32 | 64 bits | 2⁻³² | 77 163 | GOOD | `0x712F0EE8` | 8245.91 | 58.61 | 264.07 (2) | 871 | PerlinNoise 32, !msvc |  | `#ifndef _MSC_VER` and `#ifdef HAVE_INT64` |  | seahash - lower 32bit |
| 164 | `clhash` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 22932.36 | 67.22 | 262.78 (2) | 1167 | PerlinNoise, machine-specific (x64 SSE4.2) | `0xb3816f6a2c68e530`, `711` | `#if defined(HAVE_SSE42) && defined(__x86_64__)` |  | carry-less mult. hash -DBITMIX (64-bit for x64, SSE4.2) |
| 165 | `HighwayHash64` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 6242.58 | 99.55 | 248.41 (3) | 2546 |  |  | `#ifdef HAVE_HIGHWAYHASH` |  | Google HighwayHash (portable with dylib overhead) |
| 166 | `Murmur3F` | 128 | 32 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x6384BA69` | 7104.08 | 52.69 | 216.34 (2) | 632 | UB | `0x87c37b91114253d5` | `#if __WORDSIZE >= 64` | ✓ | MurmurHash3 for x64, 128-bit |
| 167 | `MUM` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xA973C6C0` | 9631.26 | 34.78 | 219.14 (2) | 1912 | UB, too many bad seeds, machine-specific (32/64 differs) | `0x0` |  |  | github.com/vnmakarov/mum-hash |
| 168 | `MUMlow` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x7F898826` | 8532.17 | 35.95 | 238.37 (2) | 1912 | UB, 5 bad seeds | `0x11fb062a`, `0x3ca9411b`, `0x3edd9a7d` (+2) |  |  | github.com/vnmakarov/mum-hash |
| 169 | `MUMv3` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xB5560703` | 16628.30 | 21.58 | 180.82 (24) | 1177 | PerlinNoise, too many bad seeds, machine-specific (32/64 differs) | `0x0`, `0x8bd72b8c`, `0xf00000ff` |  |  | github.com/vnmakarov/mum-hash |
| 170 | `xmsx32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x6B54E1D4` | 2105.03 | 45.35 | 238.92 (3) | 189 | 2 bad seeds | `0x1505929f`, `0xf0a6a74a` |  |  | XMSX-32 |
| 171 | `mirhash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x422A66FC` | 5793.85 | 38.15 | 212.91 (2) | 1112 | UB, 2^36 bad seeds, LongNeighbors, machine-specific (32/64 differs) | `0x0`, `0x5e74c778`, `0xa521f17b` (+1) | `#ifdef HAVE_INT64` |  | mirhash |
| 172 | `mirhash32low` | 32 | 64 bits | 2⁻³² | 77 163 | POOR | `0xD50D1F09` | 6168.04 | 38.35 | 234.32 (2) | 1112 | UB, 4 bad seeds, Cyclic, LongNeighbors, machine-specific (32/64 differs) | `0x0`, `0x5e74c778`, `0xa521f17b` (+1) | `#ifdef HAVE_INT64` |  | mirhash - lower 32bit |
| 173 | `mirhashstrict` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x422A66FC` | 3587.79 | 49.85 | 219.49 (2) | 699 |  | `0x7fcc747f` | `#ifdef HAVE_INT64` |  | mirhashstrict (portable, 64-bit, little-endian) |
| 174 | `mirhashstrict32low` | 32 | 64 bits | 2⁻³² | 77 163 | POOR | `0xD50D1F09` | 3642.77 | 50.16 | 233.34 (3) | 1112 | 1 bad seed, MomentChi2 9 | `0x7fcc747f` | `#ifdef HAVE_INT64` |  | mirhashstrict - lower 32bit |
| 175 | `City64low` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x6C4EF416` | 13119.17 | 47.92 | 251.81 (2) | 1120 |  |  |  |  | Google CityHash64WithSeed (v1.1)(low 32-bits) |
| 176 | `City128` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x305C0D9A` | 14472.20 | 88.71 | 285.61 (10) | 1841 |  |  | `#if defined(HAVE_SSE42) && defined(__x86_64__)` | ✓ | Google CityHash128WithSeed (v1.1) |
| 177 | `CityCrc128` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x98C09AB4` | 12255.05 | 89.25 | 281.85 (2) | 200 |  |  | `#if defined(HAVE_SSE42) && defined(__x86_64__)` |  | Google CityHashCrc128WithSeed SSE4.2 (v1.1) |
| 178 | `CityCrc256` | 256 | none (ignored) | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x2A7036C8` | 12428.45 | 164.93 | 358.73 (2) | 1729 |  |  | `#if defined(HAVE_SSE42) && defined(__x86_64__)` |  | Google CityHashCrc256 SSE4.2 (v1.1) |
| 179 | `FarmHash32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x0` | 22136.14 | 46.75 | 258.73 (2) | 11489 | machine-specific (x64 SSE4/AVX) | `0x2b091701` |  |  | Google FarmHash32WithSeed |
| 180 | `FarmHash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xEBC4A679` | 12929.54 | 46.82 | 238.50 (2) | 3758 |  |  |  |  | Google FarmHash64WithSeed |
| 181 | `FarmHash128` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x305C0D9A` | 14454.12 | 68.09 | 254.04 (2) |  |  |  |  |  | Google FarmHash128WithSeed |
| 182 | `farmhash32_c` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x0` | 22150.13 | 46.08 | 251.43 (3) | 762 | machine-specific (x64 SSE4/AVX) | `0x2b091701` | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` |  | farmhash32\_with\_seed (C99) |
| 183 | `farmhash64_c` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xEBC4A679` | 12925.73 | 46.76 | 239.47 (2) | 3688 |  |  | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` |  | farmhash64\_with\_seed (C99) |
| 184 | `farmhash128_c` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x305C0D9A` | 14284.69 | 68.80 | 256.66 (3) | 1890 |  |  | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` |  | farmhash128\_with\_seed (C99) |
| 185 | `gxhash32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0xEC19D715` | 47943.08 | 37.71 | 251.38 (2) | 736 | oob UB, AES only | `0xe1c1ec7d`, `0x0376a937`, `0x64ef3cc9` (+7) | `#ifdef HAVE_AESNI` |  | gxHash, 32-bit, AES-only |
| 186 | `gxhash64` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x9189E456` | 48919.73 | 36.61 | 236.98 (3) | 720 | oob UB, AES only |  | `#ifdef HAVE_AESNI` |  | gxHash, 64-bit, AES-only, unportable |
| 187 | `xxHash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x024B7CF4` | 11176.72 | 49.67 | 230.24 (6) | 2783 |  |  |  |  | xxHash, 64-bit |
| 188 | `lanehash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xD048C22B` | 130737.76 | 17.22 | 102.33 (9) |  |  |  | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` |  | lanehash, 64-bit, chained NH-32 stripes + mum short path, no AES |
| 189 | `lanehash128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xC2F39939` | 130363.17 | 18.65 | 100.17 (1) |  | Sparse high32 |  | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` |  | lanehash, 128-bit, chained NH-32 stripes + mum short path, no AES |
| 190 | `lanehash_aes64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x9FF60BEF` | 43802.63 | 21.82 | 119.16 (2) | 3834 | AES only |  | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` |  | lanehash\_aes, 64-bit, AES lanes + mum short path |
| 191 | `lanehash_aes128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x1A79672D` | 44261.24 | 25.31 | 138.73 (2) | 3638 | Sparse high32, AES only |  | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` |  | lanehash\_aes, 128-bit, AES lanes + mum short path |
| 192 | `xxhash256` | 64 | undetermined | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x024B7CF4` |  |  |  |  |  |  | `#if 0` |  | xxhash256, 64-bit unportable |
| 193 | `Spooky32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x3F798BBB` | 13623.84 | 55.45 | 248.11 (3) | 2221 | UB | `0x111af082`, `0x26bb3cda`, `0x94c4f96c` (+1) |  | ✓ | Bob Jenkins' SpookyHash, 32-bit result |
| 194 | `Spooky64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xA7F955F1` | 13400.95 | 55.06 | 244.86 (3) | 2221 | UB |  |  | ✓ | Bob Jenkins' SpookyHash, 64-bit result |
| 195 | `Spooky128` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x8D263080` | 13621.00 | 59.05 | 235.42 (2) | 2221 | UB |  |  | ✓ | Bob Jenkins' SpookyHash, 128-bit result |
| 196 | `SpookyV2_32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0xA48BE265` | 13626.51 | 56.19 | 256.57 (19) | 2069 |  |  |  |  | Bob Jenkins' SpookyV2, 32-bit result |
| 197 | `SpookyV2_64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x972C4BDC` | 13401.13 | 56.18 | 246.33 (11) | 2069 |  |  |  |  | Bob Jenkins' SpookyV2, 64-bit result |
| 198 | `SpookyV2_128` | 128 | 128 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x893CFCBE` | 12252.64 | 58.94 | 235.46 (2) | 2069 |  |  |  |  | Bob Jenkins' SpookyV2, 128-bit result |
| 199 | `pengyhash` | 64 | 32 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x1FC2217B` | 13347.17 | 74.79 | 278.74 (3) | 846 |  |  |  |  | pengyhash |
| 200 | `mx3` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x4DB51E5B` | 9332.99 | 47.07 | 221.61 (2) | 766 | UB | `0x10` |  |  | mx3 64bit |
| 201 | `umash32` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0x9451AF3B` | 24556.40 | 40.01 | 490.26 (33) | 1530 |  | `0x36ced9cb`, `0x707764c0`, `0x7a1583fe` (+2) | `#ifdef HAVE_UMASH` |  | umash 32 |
| 202 | `umash32_hi` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0x0CC4850F` | 26601.23 | 42.25 | 484.02 (17) | 1530 |  |  | `#ifdef HAVE_UMASH` |  | umash 32 hi |
| 203 | `umash64` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x161495C6` | 26628.37 | 39.87 | 502.33 (38) | 1530 |  |  | `#ifdef HAVE_UMASH` |  | umash 64 |
| 204 | `umash128` | 128 | secret key | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x36D4EC95` | 14523.56 | 43.73 | 475.12 (12) | 1530 |  |  | `#ifdef HAVE_UMASH` |  | umash 128 |
| 205 | `halftime_hash64` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 4801.79 | 99.05 | 310.02 (2) | 444 |  | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` |  | NH tree hash variant |
| 206 | `halftime_hash128` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 18220.34 | 94.31 | 307.50 (1) | 444 | UB overflow | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` |  | NH tree hash variant |
| 207 | `halftime_hash256` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 18249.32 | 97.56 | 322.85 (2) | 444 | UB overflow | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` |  | NH tree hash variant |
| 208 | `halftime_hash512` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x0` | 10906.18 | 118.54 | 326.76 (3) | 444 |  | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` |  | NH tree hash variant |
| 209 | `t1ha2_atonce` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x8F16C948` | 14275.60 | 37.02 | 224.12 (2) | 660 | Zeroes low3 |  |  |  | Fast Positive Hash (portable |
| 210 | `t1ha2_stream` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | POOR | `0xDED9B580` | 13673.22 | 81.12 | 263.88 (3) | 1643 | Sparse, Permutation, LongNeighbors |  |  |  | Fast Positive Hash (portable) |
| 211 | `t1ha2_atonce128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xB44C43A1` | 14059.85 | 56.44 | 251.53 (3) | 692 | LongNeighbors |  |  |  | Fast Positive Hash (portable) |
| 212 | `t1ha2_stream128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | POOR | `0xE929E756` | 13913.43 | 94.60 | 296.15 (4) | 1570 | Sparse, Permutation, LongNeighbors |  |  |  | Fast Positive Hash (portable) |
| 213 | `t1ha0_aes_noavx` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xF07C4DA5` | 27335.47 | 37.34 | 230.59 (2) | 679 | LongNeighbors, machine-specific (x86 AES-NI) |  | `#if T1HA0_AESNI_AVAILABLE` |  | Fast Positive Hash (AES-NI) |
| 214 | `t1ha0_aes_avx1` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xF07C4DA5` | 27881.52 | 36.78 | 227.13 (2) | 679 | LongNeighbors, machine-specific (x64 AVX) |  | `#if T1HA0_AESNI_AVAILABLE` |  | Fast Positive Hash (AES-NI & AVX) |
| 215 | `t1ha0_aes_avx2` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x8B38C599` | 56581.83 | 36.51 | 223.59 (1) | 628 | LongNeighbors, machine-specific (x64 AVX2) |  | `#if T1HA0_AESNI_AVAILABLE` |  | Fast Positive Hash (AES-NI & AVX2) |
| 216 | `ahash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x00000000` | 9862.62 | 27.32 | 181.68 (1) | 412 | rust |  | `#ifdef HAVE_AHASH_C` |  | ahash 64bit |
| 217 | `xxh3` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x39CD9E4A` | 20853.74 | 29.46 | 220.07 (2) | 744 | DiffDist bit 7 w. 36 bits, BIC | `0x47ebda34` |  |  | xxHash v3, 64-bit |
| 218 | `xxh3low` | 32 | 64 bits | 2⁻³² | 77 163 | GOOD | `0xFAE8467B` | 20568.96 | 29.98 | 243.82 (5) | 756 |  | `0x47ebda34` |  |  | xxHash v3, 64-bit, low 32-bits part |
| 219 | `xxh128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xEB61B3A0` | 19259.99 | 31.58 | 228.84 (2) | 1012 |  | `0x47ebda34` |  |  | xxHash v3, 128-bit |
| 220 | `xxh128low` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x54D1CC70` | 19555.88 | 30.85 | 227.83 (2) | 1012 |  | `0x47ebda34` |  |  | xxHash v3, 128-bit, low 64-bits part |
| 221 | `silicium_hash64` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x00000000` |  |  |  |  |  |  |  |  | SiliciumHash 64-bit (AVX2/SWAR) |
| 222 | `silicium_hash128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x00000000` |  |  |  |  |  |  |  |  | SiliciumHash 128-bit (AVX2/SWAR) |
| 223 | `wyhash32` | 32 | 32 bits | 2⁻³² | 77 163 | GOOD | `0x09DE8066` | 2532.89 | 48.40 | 484.57 (1) | 426 | 4 bad and broken seeds, 32-bit | `0x51a43a0f`, `0x522235ae`, `0x99ac2b20` (+1) | `#ifdef HAVE_BIT32` |  | wyhash v4.2 (32-bit native) |
| 224 | `wyhash32low` | 32 | 64 bits | 2⁻³² | 77 163 | GOOD | `0xC5DF9AA0` | 23104.85 | 28.56 | 239.71 (4) | 532 | 5 bad seeds | `0x1443cf9a`, `0x2ae375a2`, `0x729973d2` (+2) | `#ifdef HAVE_BIT32` is false |  | wyhash v4.2 lower 32bit |
| 225 | `wyhash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x9DAE7DD3` | 22640.53 | 28.91 | 229.00 (2) | 532 |  |  | `#ifdef HAVE_BIT32` is false |  | wyhash v4.2 (64-bit) |
| 226 | `w1hash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x648948F1` | 14208.56 | 26.85 | 221.76 (2) | 1497 |  |  | `#ifdef HAVE_BIT32` is false |  | w1hash (64-bit) |
| 227 | `rapidhash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x1FDC65EE` | 21513.71 | 22.04 | 169.05 (29) | 487 |  |  | `#ifdef HAVE_INT64` |  | rapidhash v3 |
| 228 | `rapidhashMicro` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x6F183D61` | 21513.71 | 22.04 | 169.05 (29) | 487 |  |  | `#ifdef HAVE_INT64` |  | rapidhashMicro |
| 229 | `rapidhashNano` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x2C200DC7` | 21295.09 | 22.53 | 166.80 (31) | 782 |  |  | `#ifdef HAVE_INT64` |  | rapidhashNano |
| 230 | `nmhash32` | 32 | undetermined | 2⁻³² | 77 163 | GOOD |  | 12676.08 | 57.09 | 255.20 (2) | 2445 |  |  |  |  | nmhash32 |
| 231 | `nmhash32x` | 32 | undetermined | 2⁻³² | 77 163 | GOOD |  | 13072.64 | 42.09 | 288.12 (3) | 1494 |  |  |  |  | nmhash32x |
| 232 | `k-hashv32` | 32 | secret key | 2⁻³² | 77 163 | GOOD | `0xB69DF8EB` | 8393.09 | 53.95 | 252.98 (3) | 1280 |  |  | `#ifdef HAVE_KHASHV` |  | Vectorized K-HashV, 32-bit |
| 233 | `k-hashv64` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xA6B7E55B` | 9251.05 | 51.72 | 251.83 (2) | 1280 |  |  | `#ifdef HAVE_KHASHV` |  | Vectorized K-HashV, 64-bit |
| 234 | `komihash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x8157FF6D` | 11143.94 | 33.67 | 483.19 (11) | 1015 |  |  |  |  | komihash 5.26 |
| 235 | `a5hash` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xBF40CAE1` | 9730.52 | 28.84 | 469.50 (5) | 372 |  |  |  |  | a5hash 5.13 |
| 236 | `polymur` | 64 | secret key | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x4F894810` | 9913.53 | 41.68 | 232.56 (3) | 1141 |  |  |  |  | github.com/orlp/polymur-hash v1, \_\_SIZEOF\_INT128\_\_ |
| 237 | `rainbow` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x2972C52E` | 6115.72 | 71.13 | 470.84 (12) | 891 |  |  |  |  | Rainbow 64-bit v1.0.6 (aka Newhash 128-bit) |
| 238 | `rainbow-128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0xFD9BCADD` | 6463.52 | 68.99 | 474.64 (5) | 943 |  |  |  |  | Rainbow 128-bit v1.0.6 (aka Newhash 128-bit) |
| 239 | `rainbow-256` | 256 | 64 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0xB327563D` | 5455.59 | 86.42 | 522.60 (24) | 1245 |  |  |  |  | Rainbow 256-bit v1.0.6 (aka Newhash 256-bit) |
| 240 | `rainstorm` | 64 | 64 bits | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0xC8DB71D5` | 1848.60 | 181.45 | 657.74 (43) | 2086 |  |  |  |  | Rainstorm 64-bit v0.0.6 |
| 241 | `rainstorm-128` | 128 | 64 bits | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x17E0FC1B` | 1862.69 | 199.59 | 652.74 (10) | 2170 |  |  |  |  | Rainstorm 128-bit v0.0.6 |
| 242 | `rainstorm-256` | 256 | 64 bits | 2⁻²⁵⁶ | 4.0 × 10³⁸ | GOOD | `0x8BDBF180` | 1908.41 | 240.20 | 697.98 (26) | 2529 |  |  |  |  | Rainstorm 256-bit v0.0.6 |
| 243 | `fifty2hash64` | 64 | undetermined | 2⁻⁶⁴ | 5.1 × 10⁹ | GOOD | `0x3C368934` |  |  |  |  |  |  |  |  | 52hash, 64-bit |
| 244 | `fifty2hash128` | 128 | undetermined | 2⁻¹²⁸ | 2.2 × 10¹⁹ | GOOD | `0x13CC0E73` |  |  |  |  |  |  |  |  | 52hash, 128-bit |
<!-- markdownlint-enable MD013 -->

## Only in the original SMHasher

These functions exist in the original SMHasher by Austin Appleby but not in
rurban/smhasher, at least not under the same name.

<!-- markdownlint-disable MD013 -->
| Name | Bits | Pair collision | 50% collision | Verification | Description |
|:-----|-----:|:---------------|--------------:|:-------------|:------------|
| `md5_32a` | 32 | 2⁻³² | 77 163 | `0xC10C356B` | MD5, first 32 bits of result |
| `sha1_32a` | 32 | 2⁻³² | 77 163 | `0xF9376EA7` | SHA1, first 32 bits of result |
| `FNV` | 32 | 2⁻³² | 77 163 | `0xE3CBBE91` | Fowler-Noll-Vo hash, 32-bit |
<!-- markdownlint-enable MD013 -->
