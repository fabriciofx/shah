# Algorithms

This file lists every hash function tested by the SMHasher family of test
suites, one row per algorithm, with the results of both suites side by side.

[SMHasher](https://github.com/rurban/smhasher) by Reini Urban tests 244
functions: 144 rated `GOOD`, 93 rated `POOR` and 7 rated `SKIP`. Its data
comes from the `g_hashes` table in `main.cpp` and from the results table in
`README.md` at commit `e154a82` (2026-10-21). That fork extends the
[original SMHasher](https://github.com/aappleby/smhasher) by Austin Appleby,
and the **Orig.** column marks the functions that already existed there.

[SMHasher3](https://gitlab.com/fwojcik/smhasher3) by Frank J. T. Wojcik is
taken at commit `3b61937` (2026-08-27). Its data comes from the
`REGISTER_HASH` blocks in `hashes/` and from `results/README.md`.

The table has 405 rows: 188 list a function tested by both suites,
56 only by rurban/smhasher and 161 only by SMHasher3.

## Pairing

A row pairs two functions when they compute the same algorithm, in the same
version and with the same output width, even under another name or another
implementation (for example, `sha2ni-256` and `SHA-2-256`). Equal
verification codes reveal most pairs; the rest come from reading both
sources, since the suites often apply the seed differently. Several
rurban/smhasher functions may share one SMHasher3 counterpart.

SMHasher3 variants that only change how the seed is applied are left out
(for example, `CityHash-128.seed2` and `XXH3-64.reinit`). The `aesrng`
functions are left out too, since SMHasher3 describes them as random number
generators, not hashes.

## Columns

Columns shared by both suites:

- **Name** and **S3 name**: name of the function in rurban/smhasher and in
  SMHasher3; blank when that suite does not test it.
- **Bits**: width of the hash output.
- **Pair collision**: probability that two distinct keys collide for an
  ideal hash of that width, `2^-bits`.
- **50% collision**: number of random keys after which an ideal hash of that
  width has a 50% chance of at least one collision (birthday bound,
  `1.1774 × 2^(bits/2)`). Measured weaknesses appear under **Problems**.
- **Description**: description given by rurban/smhasher, or by SMHasher3
  when only it tests the function.

Every function hashes a key made of an arbitrary number of bytes, as
SMHasher calls all of them through
`hash(const void *key, int len, uint32_t seed, void *out)`.

Columns from rurban/smhasher:

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

Columns from SMHasher3, prefixed with **S3**:

- **S3 seed**: seed width declared by SMHasher3: *none* (no official seed),
  *32 bits*, *64 bits* or *over 64 bits*.
- **S3 result**: `Pass` passes every test, `Fail` passes the Sanity tests
  but fails others, and `Unusable` fails the Sanity tests. It is blank when
  SMHasher3 publishes no result for the function.
- **S3 failed**: failed tests over tests run.
- **S3 cycles (1-32 B)**: average cycles per hash for keys of 1 to 32 bytes.
- **S3 bytes/cycle**: bulk speed.
- **S3 verification**: SMHasher3 verification code. It often differs from
  the rurban/smhasher one, since the suites seed the functions differently.
- **S3 traits**: SMHasher3 flags describing how the function works.
- **S3 license**: license of the implementation used by SMHasher3.

The two suites run different tests on different machines, so their ratings
and speeds cannot be compared with each other.

## Table

<!-- markdownlint-disable MD013 -->
| # | Name | S3 name | Bits | Pair collision | 50% collision | Seed | Quality | Verification | MiB/s | Cycles/hash | Cycles/map | Size | Problems | Secrets | Platform | Orig. | S3 seed | S3 result | S3 failed | S3 cycles (1-32 B) | S3 bytes/cycle | S3 verification | S3 traits | S3 license | Description |
| --: | :--- | :--- | ---: | :--- | ---: | :--- | :--- | :--- | ---: | ---: | ---: | ---: | :--- | :--- | :--- | :---: | :--- | :--- | ---: | ---: | ---: | :--- | :--- | :--- | :--- |
| 1 | `donothing32` | `donothing-32` | 32 | 2⁻³² | 77 163 | none (ignored) | SKIP | `0x0` | 11536809.55 | 4.00 | | 13 | bad seed 0, test NOP | `0` | | ✓ | 64 bits | Unusable | 249/250 | 0.00 | 228866.34 | `0x0` | mock | MIT | Do-Nothing function (measure call overhead) |
| 2 | `donothing64` | `donothing-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | SKIP | `0x0` | 12269832.97 | 4.00 | | 13 | bad seed 0, test NOP | `0` | | ✓ | 64 bits | Unusable | 249/250 | 0.00 | 222144.42 | `0x0` | mock | MIT | Do-Nothing function (measure call overhead) |
| 3 | `donothing128` | `donothing-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | none (ignored) | SKIP | `0x0` | 11961841.48 | 4.08 | | 13 | bad seed 0, test NOP | `0` | | ✓ | 64 bits | Unusable | 249/250 | 0.00 | 225261.39 | `0x0` | mock | MIT | Do-Nothing function (measure call overhead) |
| 4 | `NOP_OAAT_read64` | `donothingOAAT-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | SKIP | `0x0` | 11652662.97 | 14.00 | | 47 | test NOP | | | | 64 bits | Unusable | 249/250 | 39.22 | 3.40 | `0x0` | mock | MIT | Noop function (measure call + OAAT reading overhead) |
| 5 | `BadHash` | `badhash` | 32 | 2⁻³² | 77 163 | 32 bits | SKIP | `0xAB432E23` | 794.68 | 72.80 | | 48 | bad seed 0, test FAIL | `0` | | | 32 bits | Unusable | 226/235 | 75.95 | 0.23 | `0xAB432E23` | mock | MIT | very simple XOR shift |
| 6 | `sumhash` | `sum8hash` | 32 | 2⁻³² | 77 163 | 32 bits | SKIP | `0x0000A9AC` | 10184.67 | 29.40 | | 417 | bad seed 0, test FAIL | `0` | | | 32 bits | Unusable | 247/250 | 29.63 | 3.37 | `0x0000A9AC` | mock | MIT | sum all bytes |
| 7 | `sumhash32` | `sum32hash` | 32 | 2⁻³² | 77 163 | 32 bits | SKIP | `0x3D6DC280` | 44755.87 | 23.46 | | 697 | UB, test FAIL | `0x9e3779b97f4a7c15` | | | 32 bits | Unusable | 247/250 | 14.74 | 25.83 | `0x3D6DC280` | mock | MIT | sum all 32bit words |
| 8 | `multiply_shift` | `multiply-shift` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | POOR | `0x654E7474` | 8286.63 | 25.39 | 209.06 (2) | 345 | bad seeds & 0xfffffff0, fails most tests | `0xfffffff0`, `0x1fffffff0`, `0xb13dea7c9c324e51` (+1) | `#ifdef __SIZEOF_INT128__` | | 64 bits | Fail | 232/250 | 25.01 | 1.79 | `0xB7A5E66D` | lookup table, system specific | MIT | Dietzfelbinger Multiply-shift on strings |
| 9 | `pair_multiply_shift` | `pair-multiply-shift` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | POOR | `0x26A5AB8C` | 5728.05 | 39.67 | 218.48 (12) | 609 | fails most tests | `0xb13dea7c9c324e51`, `0x75f17d6b3588f843` | `#ifdef __SIZEOF_INT128__` | | 64 bits | Fail | 230/250 | 26.68 | 1.89 | `0x4FBA804D` | lookup table, system specific | MIT | Pair-multiply-shift |
| 10 | `crc32` | | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x3719DB20` | 369.88 | 142.33 | 396.81 (101) | 363 | insecure, 8590x collisions, distrib, PerlinNoise | | | ✓ | | | | | | | | | CRC-32 soft |
| 11 | `md5-128` | `MD5` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | none (XOR into state) | GOOD | `0xF263F96F` | 344.63 | 669.59 | 856.34 (5) | 557 | | | | | none | Pass | 0/235 | 520.36 | 0.14 | `0x1363415D` | cryptographic, weak cryptographic | GPL 3.0 | MD5 |
| 12 | `md5_64` | `MD5.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0x12F0BA8E` | 360.20 | 640.38 | 869.55 (6) | 662 | | | | | none | Pass | 0/235 | 520.40 | 0.14 | `0xF2E011D4` | cryptographic, weak cryptographic | GPL 3.0 | MD5, bits 32-95 |
| 13 | `md5_32` | `MD5.32` | 32 | 2⁻³² | 77 163 | none (XOR into state) | GOOD | `0xF3DFF19F` | 359.43 | 638.92 | 865.21 (6) | 598 | | | | | none | Pass | 0/235 | 520.66 | 0.14 | `0x4003D7EE` | cryptographic, weak cryptographic | GPL 3.0 | MD5, bits 32-63 |
| 14 | `sha1-160` | | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | none (XOR into state) | GOOD | `0x6AF411D8` | 364.95 | 1470.55 | 1794.16 (13) | 5126 | Comb/Cyclic low32 | | | | | | | | | | | | SHA1 |
| 15 | `sha1_32` | `SHA-1.32` | 32 | 2⁻³² | 77 163 | none (XOR into state) | GOOD | `0xB3122757` | 353.03 | 1385.80 | 1759.94 (5) | 5126 | Sanity, Cyclic low32, 36.6% distrib | | | | none | Pass | 0/235 | 490.07 | 0.48 | `0xF0E4D9E9` | cryptographic, weak cryptographic | MIT | SHA1, low 32 bits |
| 16 | `sha1_64` | `SHA-1.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0x995397D5` | 353.03 | 1385.80 | 1759.94 (5) | 5126 | Sanity, Cyclic low32, 36.6% distrib | | | | none | Pass | 0/235 | 490.61 | 0.48 | `0x36801ECB` | cryptographic, weak cryptographic | MIT | SHA1, low 64 bits |
| 17 | `sha2-224` | `SHA-2-224` | 224 | 2⁻²²⁴ | 6.1 × 10³³ | none (XOR into state) | POOR | `0x407AA518` | 147.13 | 1354.81 | 1589.92 (12) | 695 | Comb low32 | | | | none | Pass | 0/235 | 325.46 | 0.45 | `0x6BA219E5` | cryptographic | MIT | SHA2-224 |
| 18 | `sha2-224_64` | `SHA-2-224.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | POOR | `0xF3E40ECA` | 147.60 | 1360.10 | 1620.93 (13) | 600 | Cyclic low32 | | | | none | Pass | 0/235 | 406.07 | 0.45 | `0x36C55CA5` | cryptographic | MIT | SHA2-224, low 64 bits |
| 19 | `sha2-256` | `SHA-2-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0xEBDA2FB1` | 147.80 | 1374.90 | 1606.06 (16) | 663 | | | | | none | Pass | 0/235 | 428.33 | 0.45 | `0x33BD25DE` | cryptographic | MIT | SHA2-256 |
| 20 | `sha2-256_64` | `SHA-2-256.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0xC1C4FA72` | 148.01 | 1376.34 | 1624.71 (16) | 600 | | | | | none | Pass | 0/235 | 406.11 | 0.45 | `0x31C40E74` | cryptographic | MIT | SHA2-256, low 64 bits |
| 21 | `sha1ni` | | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | none (XOR into state) | POOR | `0x375755A4` | 1632.88 | 170.77 | 379.84 (5) | 1321 | insecure,sanity, Permutation, Zeroes, machine-specific | `0x67452301` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` | | | | | | | | | | SHA1\_NI (amd64 HW SHA ext) |
| 22 | `sha1ni_32` | `SHA-1.32` | 32 | 2⁻³² | 77 163 | none (XOR into state) | GOOD | `0xE70686CC` | 1583.50 | 172.17 | 387.70 (4) | 1273 | machine-specific | `0x67452301` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` | | none | Pass | 0/235 | 490.07 | 0.48 | `0xF0E4D9E9` | cryptographic, weak cryptographic | MIT | hardened SHA1\_NI (amd64 HW SHA ext), low 32 bits |
| 23 | `sha2ni-256` | `SHA-2-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | POOR | `0x4E3BB25E` | 1556.66 | 180.75 | 393.79 (5) | 1495 | insecure,sanity, Permutation, Zeroes, machine-specific | `0x6a09e667` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` | | none | Pass | 0/235 | 428.33 | 0.45 | `0x33BD25DE` | cryptographic | MIT | SHA2\_NI-256 (amd64 HW SHA ext) |
| 24 | `sha2ni-256_64` | `SHA-2-256.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | POOR | `0xF938E80E` | 1508.82 | 184.89 | 393.07 (6) | 1409 | Zeroes, machine-specific | `0x6a09e667` | `#if defined(HAVE_SHANI) && defined(__x86_64__)` | | none | Pass | 0/235 | 406.11 | 0.45 | `0x31C40E74` | cryptographic | MIT | hardened SHA2\_NI-256 (amd64 HW SHA ext), low 64 bits |
| 25 | `rmd128` | `RIPEMD-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | none (XOR into state) | GOOD | `0xFF576977` | 294.42 | 712.67 | 930.65 (4) | 950 | | | | | none | Pass | 0/235 | 479.70 | 0.15 | `0xC9B0B675` | cryptographic | MIT | RIPEMD-128 |
| 26 | `rmd160` | `RIPEMD-160` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | none (XOR into state) | GOOD | `0x30B37AC6` | 202.16 | 1045.79 | 1287.74 (16) | 950 | Cyclic hi32 | | | | none | Pass | 0/235 | 703.02 | 0.10 | `0x8613F5B2` | cryptographic | MIT | RIPEMD-160 |
| 27 | `rmd256` | `RIPEMD-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0xEB16FAD7` | 366.21 | 615.39 | 830.04 (7) | 1026 | | | | | none | Pass | 0/235 | 557.16 | 0.13 | `0x870A973A` | cryptographic | MIT | RIPEMD-256 |
| 28 | `edonr224` | | 224 | 2⁻²²⁴ | 6.1 × 10³³ | none (XOR into state) | GOOD | `0x83A8E7AB` | 863.77 | 304.76 | 496.66 (3) | 834 | | | `#ifdef HAVE_EDONR` | | | | | | | | | | EDON-R 224 |
| 29 | `edonr256` | | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0x06DD4F96` | 870.97 | 296.40 | 517.82 (6) | 834 | | | `#ifdef HAVE_EDONR` | | | | | | | | | | EDON-R 256 |
| 30 | `blake3_c` | `blake3` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0x50E4CD91` | 1298.04 | 354.25 | 563.63 (4) | 16462 | no 32bit portability | `0x6a09e667` | `#ifndef HAVE_ASAN` | | none | Pass | 0/235 | 316.54 | 0.42 | `0x50E4CD91` | cryptographic, lookup table | MIT | BLAKE3 c |
| 31 | `Tachyon` | | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 64 bits | GOOD | `0xE9BBF229` | 460.14 | 370.28 | 851.29 (2) | | AES only | | | | | | | | | | | | Tachyon 256-bit C |
| 32 | `blake3` | `blake3` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (ignored) | GOOD | `0x0` | | | | | | | `#if defined(HAVE_BLAKE3)` | | none | Pass | 0/235 | 316.54 | 0.42 | `0x50E4CD91` | cryptographic, lookup table | MIT | BLAKE3 Rust |
| 33 | `blake3_64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | GOOD | `0x0` | | | | | | | `#if defined(HAVE_BLAKE3)` | | | | | | | | | | BLAKE3 Rust, low 64 bits |
| 34 | `blake2s-128` | `blake2s-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | none (XOR into state) | GOOD | `0xE8D8FCDF` | 295.30 | 698.09 | 1059.24 (51) | 329 | | | | | none | Pass | 0/235 | 430.47 | 0.18 | `0xE8D8FCDF` | cryptographic, lookup table | MIT | blake2s-128 |
| 35 | `blake2s-160` | `blake2s-160` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | none (XOR into state) | GOOD | `0xD50FF144` | 215.01 | 1026.74 | 1239.54 (11) | 329 | | | | | none | Pass | 0/235 | 431.01 | 0.18 | `0xD50FF144` | cryptographic, lookup table | MIT | blake2s-160 |
| 36 | `blake2s-224` | `blake2s-224` | 224 | 2⁻²²⁴ | 6.1 × 10³³ | none (XOR into state) | GOOD | `0x19B36D2C` | 207.06 | 1063.86 | 1236.50 (20) | 329 | | | | | none | Pass | 0/235 | 449.41 | 0.18 | `0x19B36D2C` | cryptographic, lookup table | MIT | blake2s-224 |
| 37 | `blake2s-256` | `blake2s-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0x841D6354` | 215.28 | 1014.88 | 1230.38 (28) | 329 | | | | | none | Pass | 0/235 | 429.99 | 0.18 | `0x841D6354` | cryptographic, lookup table | MIT | blake2s-256 |
| 38 | `blake2s-256_64` | `blake2s-256.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0x53000BB2` | 211.52 | 1044.22 | 1228.43 (8) | 323 | | | | | none | Pass | 0/235 | 427.19 | 0.18 | `0x53000BB2` | cryptographic, lookup table | MIT | blake2s-256, low 64 bits |
| 39 | `blake2b-160` | `blake2b-160` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | none (XOR into state) | GOOD | `0x28ADDA30` | 356.08 | 1236.84 | 1458.15 (12) | 505 | | | | | none | Pass | 0/235 | 580.75 | 0.26 | `0x28ADDA30` | cryptographic, lookup table | MIT | blake2b-160 |
| 40 | `blake2b-224` | `blake2b-224` | 224 | 2⁻²²⁴ | 6.1 × 10³³ | none (XOR into state) | GOOD | `0x101A62A4` | 356.59 | 1228.50 | 1425.87 (16) | 505 | | | | | none | Pass | 0/235 | 602.89 | 0.26 | `0x101A62A4` | cryptographic, lookup table | MIT | blake2b-224 |
| 41 | `blake2b-256` | `blake2b-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0xC9D8D995` | 355.97 | 1232.22 | 1443.31 (19) | 505 | | | | | none | Pass | 0/235 | 581.72 | 0.26 | `0xC9D8D995` | cryptographic, lookup table | MIT | blake2b-256 |
| 42 | `blake2b-256_64` | `blake2b-256.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0xCF4F7EC3` | 356.97 | 1222.76 | 1435.03 (9) | 501 | | | | | none | Pass | 0/235 | 581.72 | 0.26 | `0xCF4F7EC3` | cryptographic, lookup table | MIT | blake2b-256, low 64 bits |
| 43 | `asconhashv12` | | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | GOOD | `0xA969C160` | 144.98 | 885.02 | 1324.23 (38) | 4341 | | `0xee9398aadb67f03d` | | | | | | | | | | | asconhashv12 256bit |
| 44 | `asconhashv12_64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | GOOD | `0xE7DEF300` | 159.33 | 402.54 | 473.15 (4) | 6490 | | `0xee9398aadb67f03d` | | | | | | | | | | | asconhashv12, low 64 bits |
| 45 | `sha3-256` | `SHA-3` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (XOR into state) | POOR | `0x21048CE3` | 100.58 | 3877.18 | 4159.79 (37) | 1686 | PerlinNoise | | | | none | Pass | 0/235 | 2964.70 | 0.05 | `0x79AEFB60` | cryptographic | MIT | SHA3-256 (Keccak) |
| 46 | `sha3-256_64` | `SHA-3-256.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (XOR into state) | POOR | `0xE62E5CC0` | 100.57 | 3909.00 | 4174.63 (16) | 1630 | PerlinNoise | | | | none | Pass | 0/235 | 2965.13 | 0.05 | `0x76804BEC` | cryptographic | MIT | SHA3-256 (Keccak), low 64 bits |
| 47 | `hasshe2` | `hasshe2` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 32 bits | POOR | `0xF5D39DFE` | 2879.93 | 62.47 | 272.34 (2) | 505 | Permutation,TwoBytes,Zeroes,Seed | | `#if defined(HAVE_SSE2)` | | none | Unusable | 29/238 | 96.90 | 0.93 | `0x68CBC5F1` | | public domain | SSE2 hasshe2, 256-bit |
| 48 | `poly_1_mersenne` | `poly-mersenne.deg1` | 32 | 2⁻³² | 77 163 | secret key | POOR | `0x0` | 1278.37 | 63.42 | 244.41 (1) | 479 | fails most tests | | `#ifdef __SIZEOF_INT128__` | | 64 bits | Fail | 134/238 | 53.30 | 0.50 | `0x2C5C1B0E` | system specific | BSD | Degree 1 Hashing mod 2^61-1 |
| 49 | `poly_2_mersenne` | `poly-mersenne.deg2` | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0x0` | 1391.90 | 69.97 | 255.44 (3) | 479 | | `0x60e8512c` | `#ifdef __SIZEOF_INT128__` | | 64 bits | Fail | 34/238 | 61.56 | 0.50 | `0x35AF4EA2` | system specific | BSD | Degree 2 Hashing mod 2^61-1 |
| 50 | `poly_3_mersenne` | `poly-mersenne.deg3` | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0x0` | 1320.15 | 80.81 | 263.12 (2) | 479 | | `0x3d25f745` | `#ifdef __SIZEOF_INT128__` | | 64 bits | Pass | 0/238 | 69.79 | 0.50 | `0x8197A37D` | system specific | BSD | Degree 3 Hashing mod 2^61-1 |
| 51 | `poly_4_mersenne` | `poly-mersenne.deg4` | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0x0` | 1393.93 | 89.07 | 262.97 (2) | 479 | | | `#ifdef __SIZEOF_INT128__` | | 64 bits | Pass | 0/238 | 78.16 | 0.50 | `0x27C2F53B` | system specific | BSD | Degree 4 Hashing mod 2^61-1 |
| 52 | `tabulation` | `tabulation-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0xB49C607C` | 7407.94 | 39.83 | 240.25 (4) | 554 | | | `#ifdef __SIZEOF_INT128__` | | 64 bits | Fail | 30/250 | 37.33 | 3.12 | `0x53B08B2D` | lookup table, system specific | BSD | 64-bit Tabulation with Multiply-Shift Mixer |
| 53 | `tabulation32` | `tabulation-32` | 32 | 2⁻³² | 77 163 | secret key | POOR | `0x335F64EA` | 5819.40 | 40.16 | 233.00 (3) | 848 | collisions | | | | 64 bits | Fail | 103/250 | 27.88 | 2.20 | `0x0D34E471` | lookup table, system specific | BSD | 32-bit Tabulation with Multiply-Shift Mixer |
| 54 | `crc32_hw` | `CRC-32C` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x0C7346F0` | 5537.79 | 40.80 | 225.65 (3) | 653 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (SSE4.2/NEON) | `0x111c2232` | `#ifdef HAVE_SSE42` | | 32 bits | Fail | 235/250 | 30.75 | 7.69 | `0x6E6071BD` | CRC, lookup table | BSD | SSE4.2 crc32 in HW |
| 55 | `crc64_hw` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xE7C3FD0E` | 5579.17 | 40.87 | 202.19 (1) | 652 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (SSE4.2/NEON) | `0x0` | `#ifdef HAVE_SSE42` | | | | | | | | | | SSE4.2 crc64 in HW |
| 56 | `crc32_hw1` | `CRC-32C` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x0C7346F0` | 7626.17 | 50.21 | 228.50 (2) | 696 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (x86 SSE4.2) | `0x111c2232` | `#ifdef HAVE_SSE42` | | 32 bits | Fail | 235/250 | 30.75 | 7.69 | `0x6E6071BD` | CRC, lookup table | BSD | Faster Adler SSE4.2 crc32 on Intel HW |
| 57 | `crc32_pclmul` | | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x0` | 7963.20 | 106.02 | 567.70 (3) | 254 | insecure, 100% bias, collisions, distrib, BIC, machine-specific (x86 SSE4.2+PCLMUL) | `0x0` | `#if defined(HAVE_CLMUL) && !defined(_MSC_VER) && defined(__x86_64__)` | | | | | | | | | | -mpclmul crc32 in asm on HW |
| 58 | `crc64_jones1` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x7DC1B496` | 1673.67 | 83.00 | 249.16 (2) | | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` | | | | | | | | | | crc64, jones polynomial, standard fast software method from Mark Adler |
| 59 | `crc64_jones2` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x7DC1B496` | 2968.07 | 314.60 | 253.01 (3) | | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` | | | | | | | | | | crc64, jones polynomial, 2 pipelines in 1 thread, joined at the end |
| 60 | `crc64_jones3` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x7DC1B496` | 3398.80 | 302.59 | 258.16 (8) | | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` | | | | | | | | | | crc64, jones polynomial, 3 pipelines in 1 thread, joined at the end |
| 61 | `crc64_jones` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x7DC1B496` | 3512.66 | 80.70 | 251.40 (2) | | insecure, 100% bias, collisions, distrib, BIC, machine-specific | `0xdeadbeef` | `#ifdef HAVE_INT64` | | | | | | | | | | crc64, jones polynomial, auto 1-3 pipelines depending on length, joined at the end |
| 62 | `o1hash` | `o1hash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | POOR | `0x85051E87` | 11530366.92 | 18.40 | 206.94 (1) | 101 | insecure, no seed, zeros, fails all tests | `0x0` | `#ifdef HAVE_INT64` | | none | Unusable | 248/250 | 15.00 | 140946.80 | `0xAE049F09` | mock | public domain | o(1)hash unseeded, from wyhash |
| 63 | `fhtw` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | POOR | `0x0` | | | | | | | `#if 0 && defined(__x86_64__) && (defined(__linux__) \|\| defined(__APPLE__))` | | | | | | | | | | fhtw asm |
| 64 | `fibonacci` | `fibonacci-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xFE3BD380` | 15409.62 | 22.67 | 872.83 (3) | 1692 | UB, zeros, fails all tests | `0x0`, `0xffffffff00000000` | | | 64 bits | Unusable | 246/250 | 22.58 | 9.56 | `0xFE3BD380` | | MIT | wordwise Fibonacci |
| 65 | `k-hash32` | `khash-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x99B3FFCD` | 2227.90 | 53.85 | 255.78 (2) | 808 | insecure, zeros, UB, bad seeds, fails all tests | | `#ifndef HAVE_ALIGNED_ACCESS_REQUIRED` | | 64 bits | Unusable | 181/250 | 52.98 | 1.39 | `0xA17DA29E` | | MIT | K-Hash mixer, 32-bit |
| 66 | `k-hash64` | `khash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xAB5518A1` | 2492.66 | 48.18 | 242.58 (2) | 609 | insecure, zeros, UB, bad seeds, fails all tests | | `#ifndef HAVE_ALIGNED_ACCESS_REQUIRED` | | 64 bits | Unusable | 143/250 | 41.70 | 1.56 | `0x44BD88C4` | | MIT | K-Hash mixer, 64-bit |
| 67 | `FNV1a` | `FNV-1a-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xE3CBBE91` | 754.70 | 75.28 | 226.30 (2) | 54 | bad seed, zeros, fails all tests | `0x811c9dc5` | | | none | Fail | 227/235 | 73.02 | 0.25 | `0xE3CBBE91` | | MIT | Fowler-Noll-Vo hash, 32-bit |
| 68 | `FNV1A_Totenschiff` | `FNV-Totenschiff` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x95D95ACF` | 6166.85 | 26.60 | 235.71 (2) | 270 | UB, zeros, fails all tests | `0x811c9dc5` | `#ifdef HAVE_INT64` | | none | Unusable | 206/250 | 30.06 | 2.00 | `0x95D95ACF` | | MIT | FNV1A\_Totenschiff\_v1 64-bit sanmayce |
| 69 | `FNV1A_Pippip_Yurii` | `FNV-PippipYurii` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xDFE2D53C` | 24196.44 | 86.25 | 539.41 (3) | 603 | bad seeds, UB, sanity, x64 AESNI only | `0x1077bd26`, `0x1c07e567`, `0x1c2e04b6` (+7) | `#ifdef HAVE_INT64` and `#if defined(HAVE_AESNI) && defined(__SIZEOF_INT128__) && (defined(__x86_64__) \|\| defined(_M_AMD64))` | | none | Unusable | 197/250 | 31.42 | 2.00 | `0xE79AE3E4` | | MIT | FNV1A-Pippip\_Yurii\_OOO\_128bit\_AES\_TriXZi\_Mikayla 32-bit sanmayce |
| 70 | `FNV1a_YT` | `FNV-YoshimitsuTRIAD` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xD8AFFD71` | 13481.70 | 30.67 | 233.71 (7) | 336 | bad seed, UB, fails all tests | `0x811c9dc5`, `0x23d4a49d` | `#ifdef HAVE_INT64` | | none | Fail | 230/250 | 26.17 | 5.27 | `0xD8AFFD71` | | MIT | FNV1a-YoshimitsuTRIAD 32-bit sanmayce |
| 71 | `FNV64` | `FNV-1a-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x103455FC` | 747.70 | 74.18 | 189.18 (1) | 70 | fails all tests | `0x811c9dc5`, `0xcbf29ce4`, `0x84222325` (+1) | `#ifdef HAVE_INT64` | | none | Fail | 230/235 | 73.00 | 0.25 | `0x103455FC` | | MIT | Fowler-Noll-Vo hash, 64-bit |
| 72 | `FNV128` | `FNV-1a-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | POOR | `0xC6FF4526` | 408.59 | 130.82 | 299.47 (20) | 153 | fails all tests | | `#ifdef HAVE_INT64` | | none | Fail | 230/235 | 93.08 | 0.19 | `0x0269D36F` | | BSD | Go variant of FNV, 128-bit |
| 73 | `FNV2` | `FNV-1a-64.wordwise` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x1967C625` | 5630.87 | 32.09 | 207.08 (1) | 278 | fails all tests | | | | none | Unusable | 240/250 | 34.84 | 2.00 | `0x1967C625` | | MIT | wordwise FNV |
| 74 | `fletcher2` | `fletcher2.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x890767C0` | 15410.12 | 20.72 | 345.55 (5) | 117 | bad seed 0, UB, fails all tests | `0` | | | none | Unusable | 245/250 | 21.23 | 4.92 | `0x890767C0` | | MIT | fletcher2 ZFS |
| 75 | `fletcher4` | `fletcher4.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x890767C0` | 15603.68 | 21.24 | 320.09 (2) | 117 | bad seed 0, UB, fails all tests | `0` | | | none | Unusable | 248/250 | 21.10 | 1.91 | `0x47660EB7` | | MIT | fletcher4 ZFS |
| 76 | `bernstein` | `perl-djb2` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xBDB4B640` | 1032.00 | 59.04 | 225.09 (3) | 56 | bad seed 0, fails all tests | `0` | | ✓ | 32 bits | Fail | 230/235 | 55.03 | 0.33 | `0x4962CBAB` | | GPL 3.0 | Bernstein, 32-bit |
| 77 | `sdbm` | `perl-sdbm` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x582AF769` | 772.01 | 71.67 | 220.09 (2) | 54 | bad seed 0, fails all tests | `0` | | | 32 bits | Fail | 230/235 | 70.00 | 0.25 | `0xD973311D` | | GPL 3.0 | sdbm as in perl5 |
| 78 | `x17` | `x17` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x8128E14C` | 765.01 | 75.61 | 225.81 (2) | 65 | 99.98% bias, fails all tests | | | | 32 bits | Fail | 230/235 | 73.12 | 0.25 | `0x8128E14C` | | MIT | x17 |
| 79 | `libiberty` | | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x584FBC20` | 618.39 | 86.58 | 220.95 (2) | 37 | insecure, 100% bias, fails all tests, bad seed | `0x2ba97ba0` | | | | | | | | | | | libiberty htab\_hash\_string |
| 80 | `gcc` | | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xC6239327` | 612.10 | 88.44 | 224.62 (2) | 55 | insecure, 100% bias, fails all tests, bad seed | `0xbaa0af90` | | | | | | | | | | | gcc libcpp |
| 81 | `JenkinsOOAT` | `perl-jenkins-hard` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x83E133DA` | 615.60 | 111.23 | 251.08 (2) | 150 | bad seed 0, 53.5% bias, fails all tests | `0` | | | 64 bits | Fail | 134/235 | 115.01 | 0.20 | `0x1C216B25` | | GPL 3.0 | Bob Jenkins' OOAT as in perl 5.18 |
| 82 | `JenkinsOOAT_perl` | `perl-jenkins-old` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xEE05869B` | 631.79 | 93.13 | 232.44 (1) | 65 | bad seed 0, 1.5-11.5% bias, 7.2x collisions, BIC, LongNeighbors | `0` | | | 32 bits | Unusable | 173/235 | 95.00 | 0.20 | `0xEE05869B` | | GPL 3.0 | Bob Jenkins' OOAT as in old perl5 |
| 83 | `pearsonhash64` | `Pearson-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x12E4C8CD` | 439.85 | 123.07 | 213.68 (1) | | Avalanche, Seed, SSSE3 only. broken MSVC | | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` | | 64 bits | Fail | 219/235 | 125.08 | 0.14 | `0x12E4C8CD` | AES, lookup table | public domain | Pearson hash, 64-bit SSSE3 |
| 84 | `pearsonhash128` | `Pearson-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | none (ignored) | POOR | `0x6CCBB7B3` | 438.49 | 123.81 | 212.89 (2) | | Avalanche, Seed, SSSE3 only. broken MSVC | | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` | | 64 bits | Fail | 222/235 | 121.01 | 0.14 | `0xDC5048A3` | AES, lookup table | public domain | Pearson hash, 128-bit SSSE3, low 64-bit |
| 85 | `pearsonhash256` | `Pearson-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (ignored) | POOR | `0x7F8BEB21` | 440.63 | 120.72 | 224.63 (2) | | Avalanche, Seed, SSSE3 only. broken MSVC | | `#if defined(HAVE_SSE42) && defined(__x86_64__) && !defined(_MSC_VER)` | | 64 bits | Fail | 220/235 | 126.13 | 0.14 | `0xA9B1DE02` | AES, lookup table | public domain | Pearson hash, 256-bit SSSE3, low 64-bit |
| 86 | `pearsonbhash64` | `PearsonBlock-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x14C3D184` | 1749.98 | 99.84 | 257.11 (2) | 683 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 101.19 | 0.57 | `0x14C3D184` | | public domain | Pearson block hash, 64-bit |
| 87 | `pearsonbhash128` | `PearsonBlock-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x6BEFE6EA` | 1656.68 | 106.78 | 276.92 (3) | 1134 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 109.30 | 0.53 | `0x6BEFE6EA` | | public domain | Pearson block hash, 128-bit, low 64-bit |
| 88 | `pearsonbhash256` | `PearsonBlock-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 64 bits | GOOD | `0x999B3C19` | 1418.61 | 123.68 | 301.26 (3) | 844 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 168.68 | 0.33 | `0x999B3C19` | | public domain | Pearson block hash, 256-bit, low 64-bit |
| 89 | `VHASH_32` | `VHASH.32` | 32 | 2⁻³² | 77 163 | secret key | POOR | `0xF0077651` | 13084.37 | 65.45 | 280.14 (2) | 1231 | sanity, Seed, MomentChi2 | | | | 64 bits | Fail | 56/250 | 91.32 | 5.12 | `0x613E4735` | cryptographic | public domain | VHASH\_32 by Ted Krovetz and Wei Dai |
| 90 | `VHASH_64` | `VHASH` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | POOR | `0xF97D84FE` | 13217.64 | 64.90 | 270.04 (2) | 1231 | sanity, Seed, Sparse | | | | 64 bits | Fail | 60/250 | 91.31 | 5.14 | `0x7417A00F` | cryptographic | public domain | VHASH\_64 by Ted Krovetz and Wei Dai |
| 91 | `MicroOAAT` | `MicroOAAT` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x16F1BA97` | 730.89 | 78.25 | 236.07 (3) | 78 | 100% bias, distrib, BIC | `0x3b00` | | | 32 bits | Fail | 195/235 | 76.56 | 0.23 | `0x16F1BA97` | | MIT | Small non-multiplicative OAAT (by funny-falcon) |
| 92 | `farsh32` | `FARSH-32` | 32 | 2⁻³² | 77 163 | 64 bits | POOR | `0xBCDE332C` | 27583.89 | 65.65 | 266.64 (2) | 944 | insecure: AppendedZeroes, collisions+bias, MomentChi2, LongNeighbors | | `#ifdef HAVE_SSE2` | | 64 bits | Unusable | 27/250 | 63.73 | 16.09 | `0xBCDE332C` | lookup table | MIT | FARSH 32bit |
| 93 | `farsh64` | `FARSH-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xDE2FDAEE` | 13558.69 | 113.74 | 327.26 (3) | 944 | insecure: AppendedZeroes, collisions+bias, MomentChi2, LongNeighbors | | `#ifdef HAVE_SSE2` | | 64 bits | Unusable | 30/250 | 115.98 | 7.97 | `0xDE2FDAEE` | lookup table | MIT | FARSH 64bit |
| 94 | `farsh128` | `FARSH-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | POOR | `0x82B6CBEC` | 7055.39 | 229.42 | 375.57 (4) | 944 | insecure: AppendedZeroes, collisions+bias, permut,combin,2bytes,zeroes,PerlinNoise | | `#ifdef HAVE_SSE2` | | 64 bits | Unusable | 33/238 | 234.02 | 3.98 | `0x82B6CBEC` | lookup table | MIT | FARSH 128bit |
| 95 | `farsh256` | `FARSH-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 64 bits | POOR | `0xFEBEA0BC` | 3466.26 | 444.74 | 610.23 (4) | 944 | insecure: AppendedZeroes, collisions+bias, permut,combin,2bytes,zeroes,PerlinNoise | | `#ifdef HAVE_SSE2` | | 64 bits | Unusable | 28/235 | 477.36 | 1.99 | `0xFEBEA0BC` | lookup table | MIT | FARSH 256bit |
| 96 | `jodyhash32` | | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xA2AEFC60` | 1762.46 | 42.66 | 236.09 (2) | 153 | bias, collisions, distr, BIC LongNeighbors | `0x9fd368f4` | | | | | | | | | | | jodyhash, 32-bit (v6) |
| 97 | `jodyhash64` | `jodyhash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xC1CBFA34` | 4861.84 | 44.05 | 234.35 (2) | 1036 | bias, collisions, distr, BIC, LongNeighbors | | `#ifdef HAVE_INT64` | | 64 bits | Unusable | 230/250 | 29.24 | 1.98 | `0xC1CBFA34` | | MIT | jodyhash, 64-bit (v7.1) |
| 98 | `lookup3` | `lookup3.32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x3D83917A` | 2474.09 | 40.19 | 238.22 (3) | 408 | UB, 28% bias, collisions, 30% distr, BIC | `0x21524101` | | ✓ | 64 bits | Fail | 91/238 | 35.99 | 0.81 | `0x3D83917A` | | public domain | Bob Jenkins' lookup3 |
| 99 | `superfast` | `SuperFastHash` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x0C80403A` | 2085.79 | 49.89 | 230.76 (3) | 210 | UB, bad seed 0, 91% bias, 5273.01x collisions, 37% distr, BIC | `0x0` | | ✓ | 32 bits | Fail | 219/238 | 45.12 | 0.78 | `0xCFA52B38` | | GPL 3.0 | Paul Hsieh's SuperFastHash, CHAR\_MIN |
| 100 | `MurmurOAAT` | `MurmurOAAT` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x5363BD98` | 513.75 | 105.23 | 244.81 (4) | 45 | bad seed 0, collisions, 99.998% distr., BIC, LongNeighbors | `0x0` | | ✓ | 32 bits | Unusable | 165/235 | 105.00 | 0.17 | `0x5363BD98` | | MIT | Murmur one-at-a-time |
| 101 | `Crap8` | `Crap8` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x743E97A1` | 3081.04 | 37.15 | 234.94 (2) | 245 | UB, 2.42% bias, collisions, 2% distrib | | | ✓ | 32 bits | Fail | 161/250 | 34.04 | 1.00 | `0x743E97A1` | | MIT | Crap8 |
| 102 | `xxHash32` | `XXH-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xBA88B743` | 6064.37 | 48.86 | 234.27 (3) | 776 | LongNeighbors, collisions with 4bit diff, MomentChi2 220 | | | | 32 bits | Fail | 83/250 | 44.38 | 2.00 | `0x6FD78385` | | BSD | xxHash, 32-bit for x86 |
| 103 | `Murmur1` | `MurmurHash1` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x9EA7D056` | 1955.36 | 48.84 | 236.25 (2) | 197 | UB, 1 bad seed, 511x collisions, Diff, BIC | `0xc6a4a793` | | | 32 bits | Fail | 116/238 | 46.29 | 0.67 | `0x9EA7D056` | | MIT | MurmurHash1 |
| 104 | `Murmur2` | `MurmurHash2-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x27864C1E` | 3082.03 | 41.62 | 250.72 (4) | 185 | UB, 1 bad seed, 1.7% bias, 81x coll, 1.7% distrib, BIC | `0x10` | | ✓ | 32 bits | Fail | 173/250 | 38.94 | 1.00 | `0x27864C1E` | | MIT | MurmurHash2 for x86, 32-bit |
| 105 | `Murmur2A` | `MurmurHash2a` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0x7FBD4396` | 2850.40 | 46.60 | 284.58 (13) | 244 | UB, 1 bad seed, 12.7% bias, LongNeighbors | `0x2fc301c9` | | ✓ | 32 bits | Fail | 150/250 | 43.77 | 1.00 | `0x7FBD4396` | | MIT | MurmurHash2A for x86, 32-bit |
| 106 | `Murmur2B` | `MurmurHash2-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x1F0D3804` | 6039.96 | 38.70 | 212.23 (1) | 307 | UB, 1.8% bias, collisions, 3.4% distrib, BIC | `0xc6a4a7935bd1e995` | `#if __WORDSIZE >= 64` | ✓ | 64 bits | Fail | 101/250 | 39.95 | 2.00 | `0x1F0D3804` | | MIT | MurmurHash64A for x64, 64-bit |
| 107 | `Murmur2C` | `MurmurHash2-64.int32` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xDD537C05` | 3802.68 | 49.82 | 220.13 (2) | 311 | UB, 2^32 bad seeds, 91% bias, collisions, distr, BIC, LongNeighbors | `0x10`, `0xffffffff00000010` | `#ifdef HAVE_INT64` | ✓ | 64 bits | Fail | 208/250 | 44.57 | 1.33 | `0xDD537C05` | | MIT | MurmurHash64B for x86, 64-bit |
| 108 | `Murmur3A` | `MurmurHash3-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xB0F57EE3` | 3027.30 | 48.99 | 234.49 (2) | 264 | UB, 1 bad seed, Moment Chi2 69 | `0xfca58b2d` | | ✓ | 32 bits | Fail | 82/250 | 44.83 | 1.00 | `0xB0F57EE3` | | MIT | MurmurHash3 for x86, 32-bit |
| 109 | `PMurHash32` | `MurmurHash3-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xB0F57EE3` | 3001.44 | 48.99 | 240.35 (3) | 1862 | UB, 1 bad seed, Moment Chi2 69 | `0xfca58b2d` | | ✓ | 32 bits | Fail | 82/250 | 44.83 | 1.00 | `0xB0F57EE3` | | MIT | Shane Day's portable-ized MurmurHash3 for x86, 32-bit |
| 110 | `Murmur3C` | `MurmurHash3-128.int32` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | POOR | `0xB3ECE62A` | 4824.95 | 57.39 | 243.91 (2) | 780 | UB, LongNeighbors, Text, DiffDist | `0x239b961b` | | ✓ | 32 bits | Fail | 96/250 | 46.82 | 1.63 | `0xB3ECE62A` | | MIT | MurmurHash3 for x86, 128-bit |
| 111 | `PMPML_32` | `PMP-Multilinear-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xEAE2E3CC` | 6904.30 | 44.25 | 233.59 (2) | 1020 | Avalanche >512, unseeded: Seed, BIC, MomentChi2, PerlinNoise | | `#if !defined(DEBUG) && !defined(CROSSCOMPILING) && !defined(__aarch64__)` | | 64 bits | | | | | `0xF3199670` | lookup table | BSD | PMP\_Multilinear 32-bit unseeded |
| 112 | `PMPML_64` | `PMP-Multilinear-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0x584CC9DF` | 10030.67 | 49.22 | 239.07 (5) | 1299 | unseeded: Seed, MomentChi2, BIC | | `#if !defined(DEBUG) && !defined(CROSSCOMPILING) && !defined(__aarch64__)` | | 64 bits | | | | | `0xB776D2B9` | lookup table | BSD | PMP\_Multilinear 64-bit unseeded |
| 113 | `City32` | `CityHash-32` | 32 | 2⁻³² | 77 163 | 32 bits | POOR | `0xEDED9084` | 5745.83 | 52.44 | 242.69 (2) | 1057 | | `0x2eb38c9f` | | | none | Fail | 70/250 | 44.19 | 1.92 | `0xEDED9084` | | MIT | Google CityHash32WithSeed (v1.1) |
| 114 | `metrohash64` | `MetroHash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x6FA828C9` | 14209.14 | 40.85 | 225.16 (2) | 647 | UB, LongNeighbors, BIC | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 29/250 | 42.39 | 5.00 | `0x6FA828C9` | | MIT | MetroHash64, 64-bit |
| 115 | `metrohash64_1` | `MetroHash-64.var1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xEE88F7D2` | 14495.30 | 40.83 | 213.00 (2) | 624 | UB, LongNeighbors, BIC, MomentChi2 | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 33/250 | 42.39 | 4.99 | `0xEE88F7D2` | | MIT | MetroHash64\_1, 64-bit (legacy) |
| 116 | `metrohash64_2` | `MetroHash-64.var2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0xE1FC7C6E` | 14359.39 | 41.09 | 215.53 (2) | 627 | UB, LongNeighbors | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 29/250 | 42.49 | 5.02 | `0xE1FC7C6E` | | MIT | MetroHash64\_2, 64-bit (legacy) |
| 117 | `metrohash128` | `MetroHash-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x4A6673E7` | 15847.63 | 72.33 | 266.54 (2) | 878 | UB, LongNeighbors | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 17/250 | 52.90 | 5.02 | `0x4A6673E7` | | MIT | MetroHash128, 128-bit |
| 118 | `metrohash128_1` | `MetroHash-128.var1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x20E8A1D7` | 15556.40 | 73.49 | 259.05 (2) | 894 | UB, LongNeighbors | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 22/250 | 52.91 | 5.01 | `0x20E8A1D7` | | MIT | MetroHash128\_1, 128-bit (legacy) |
| 119 | `metrohash128_2` | `MetroHash-128.var2` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x5437C684` | 15408.56 | 74.36 | 259.57 (2) | 894 | UB, LongNeighbors | | `#ifdef HAVE_INT64` | | 64 bits | Fail | 22/250 | 52.99 | 5.02 | `0x5437C684` | | MIT | MetroHash128\_2, 128-bit (legacy) |
| 120 | `metrohash64crc_1` | `MetroHashCrc-64.var1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0x29C68A50` | 8010.90 | 44.43 | 213.94 (2) | 674 | UB, Cyclic 8/8 byte, DiffDist, BIC, MomentChi2, machine-specific (SSE4.2/NEON) | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 46/250 | 46.53 | 7.98 | `0x29C68A50` | CRC | MIT | MetroHash64crc\_1 for x64 (legacy) |
| 121 | `metrohash64crc_2` | `MetroHashCrc-64.var2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0x2C00BD9F` | 7939.65 | 44.84 | 222.01 (2) | 626 | UB, Cyclic 8/8 byte, DiffDist, BIC, machine-specific (SSE4.2/NEON) | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 47/250 | 46.50 | 7.92 | `0x2C00BD9F` | | MIT | MetroHash64crc\_2 for x64 (legacy) |
| 122 | `cmetrohash64_1o` | `MetroHash-64.var1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xEE88F7D2` | 14678.56 | 40.33 | 216.44 (2) | 3506 | UB, LongNeighbors, BIC, MomentChi2 | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 33/250 | 42.39 | 4.99 | `0xEE88F7D2` | | MIT | cmetrohash64\_1 (shorter key optimized), 64-bit for x64 |
| 123 | `cmetrohash64_1` | `MetroHash-64.var1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xEE88F7D2` | 14332.75 | 41.04 | 216.05 (2) | 651 | UB, LongNeighbors, BIC, MomentChi2 | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 33/250 | 42.39 | 4.99 | `0xEE88F7D2` | | MIT | cmetrohash64\_1, 64-bit for x64 |
| 124 | `cmetrohash64_2` | `MetroHash-64.var2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0xE1FC7C6E` | 14498.20 | 40.87 | 219.65 (2) | 647 | LongNeighbors | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 29/250 | 42.49 | 5.02 | `0xE1FC7C6E` | | MIT | cmetrohash64\_2, 64-bit for x64 |
| 125 | `metrohash128crc_1` | `MetroHashCrc-128.var1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x5E75144E` | 8182.02 | 77.38 | 256.80 (2) | 723 | UB, machine-specific (SSE4.2/NEON) | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 50/250 | 58.38 | 7.98 | `0x5E75144E` | | MIT | MetroHash128crc\_1 for x64 (legacy) |
| 126 | `metrohash128crc_2` | `MetroHashCrc-128.var2` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x1ACF3E77` | 7996.33 | 78.81 | 262.53 (2) | 723 | UB, machine-specific (SSE4.2/NEON) | | `#if defined(HAVE_SSE42) && (defined(__x86_64__) \|\| defined(__aarch64__)) && !defined(_MSC_VER)` | | 64 bits | Fail | 50/250 | 58.38 | 7.96 | `0x1ACF3E77` | | MIT | MetroHash128crc\_2 for x64 (legacy) |
| 127 | `City64noSeed` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | POOR | `0x4C4E54B1` | 14023.27 | 33.37 | 223.59 (2) | 1038 | Avalanche, Sparse, TwoBytes, MomentChi2, Seed | | | | | | | | | | | | Google CityHash64 without seed (v1.1)(default version, misses one final avalanche) |
| 128 | `City64` | `CityHash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x5FABC5C5` | 14390.09 | 46.69 | 231.99 (2) | 1120 | Sparse, TwoBytes | | | ✓ | 64 bits | Fail | 45/250 | 51.89 | 4.75 | `0x5FABC5C5` | | MIT | Google CityHash64WithSeed (v1.1) |
| 129 | `aesnihash` | `aesnihash-majek` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0xA68E0D42` | 5365.60 | 57.21 | 255.87 (3) | 590 | fails many tests, machine-specific (x64 AES-NI) | `0x70736575` | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` | | none | Unusable | 64/250 | 57.97 | 1.78 | `0xA68E0D42` | AES | BSD | majek's seeded aesnihash with aesenc, 64-bit for x64 |
| 130 | `aesni` | `aesnihash-peterrk` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0xF06DA1B1` | 31185.98 | 29.45 | 226.75 (2) | 519 | machine-specific (x64 AES-NI) | | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` | | 32 bits | Fail | 41/250 | 32.58 | 9.43 | `0xF06DA1B1` | AES | BSD | aesni 128bit |
| 131 | `aesni-low` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x3AA1A480` | 31027.39 | 29.47 | 232.54 (2) | 519 | machine-specific (x64 AES-NI) | | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI) && !defined(_MSC_VER)` | | | | | | | | | | aesni 64bit |
| 132 | `aesni-hash-peterrk` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x4E311231` | 29107.73 | 28.86 | 217.57 (1) | | machine-specific (x64 AES-NI) | | `#if defined(HAVE_SSE2) && defined(HAVE_AESNI)` | | | | | | | | | | PeterRK's seeded aesnihash, 128-bit for x64, unportable |
| 133 | `falkhash` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | POOR | `0x2F99B071` | 52401.48 | 122.70 | 316.79 (4) | 264 | Sparse, LongNeighbors, machine-specific (x64 AES-NI) | | `#if defined(HAVE_SSE2) && defined(__x86_64__) && !defined(_WIN32) && !defined(_MSC_VER)` | | | | | | | | | | falkhash.asm with aesenc, 64-bit for x64 |
| 134 | `MeowHash32low` | `MeowHash.32` | 32 | 2⁻³² | 77 163 | secret key | POOR | `0x8872DE1A` | 30562.54 | 63.77 | 283.26 (3) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` | | none | Pass | 0/250 | 61.50 | 12.44 | `0xE9E94FF2` | AES | zlib | MeowHash (requires x64 AES-NI) |
| 135 | `MeowHash64low` | `MeowHash.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | POOR | `0xB04AC842` | 29438.45 | 63.76 | 269.41 (4) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` | | none | Pass | 0/250 | 61.51 | 12.19 | `0x4C9F52A6` | AES | zlib | MeowHash (requires x64 AES-NI) |
| 136 | `MeowHash` | `MeowHash` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | secret key | POOR | `0xA0D29861` | 29969.81 | 64.90 | 273.79 (8) | 1764 | Sparse, invertible, machine-specific (x64 AES-NI) | `0x920e7c64` | `#ifdef HAVE_MEOW_HASH` | | none | Pass | 0/250 | 61.51 | 12.43 | `0x7C648489` | AES | zlib | MeowHash (requires x64 AES-NI) |
| 137 | `t1ha1_64le` | `t1ha1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xD6836381` | 13425.03 | 31.37 | 221.97 (2) | 612 | Avalanche | | | | 64 bits | Fail | 65/250 | 30.31 | 4.58 | `0xD6836381` | | zlib | Fast Positive Hash (portable, aims 64-bit, little-endian) |
| 138 | `t1ha1_64be` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x93F864DE` | 12002.50 | 31.57 | 226.52 (2) | 639 | Avalanche | | | | | | | | | | | | Fast Positive Hash (portable, aims 64-bit, big-endian) |
| 139 | `t1ha0_32le` | `t1ha0` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0x7F7D7B29` | 7276.16 | 49.11 | 236.39 (2) | 594 | Sparse, LongNeighbors | | | | 64 bits | Fail | 18/250 | 45.35 | 2.42 | `0x7F7D7B29` | | zlib | Fast Positive Hash (portable, aims 32-bit, little-endian) |
| 140 | `t1ha0_32be` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xDA6A4061` | 6860.87 | 50.16 | 241.26 (2) | 607 | Sparse, LongNeighbors | | | | | | | | | | | | Fast Positive Hash (portable, aims 32-bit, big-endian) |
| 141 | `tifuhash_64` | `floppsyhash.old` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x0` | 35.60 | 1679.52 | 1212.75 (15) | 276 | Cyclic low32 | | | | 32 bits | Fail | 94/235 | 698.79 | 0.04 | `0x644236D4` | | MIT | Tiny Floatingpoint Unique Hash with continued egyptian fractions |
| 142 | `beamsplitter` | `beamsplitter` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x0` | 789.22 | 682.45 | 1150.33 (26) | 4203 | UB | | | | 32 bits | Fail | 4/235 | 920.62 | 0.18 | `0x1BDF358B` | lookup table | MIT | A possibly universal hash made with a 10x64 s-box. |
| 143 | `discohash1` | `Discohash1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0xBEBB4185` | 4152.62 | 202.14 | 414.34 (4) | 1294 | | | | | 32 bits | Pass | 0/238 | 208.67 | 1.34 | `0xBEBB4185` | | MIT | DISCoHAsH 64 (was BEBB4185) v1 |
| 144 | `discohash1-128` | `Discohash1-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x05C0460C` | 4064.39 | 231.06 | 430.94 (6) | 1294 | | | | | 32 bits | Pass | 0/238 | 239.92 | 1.34 | `0x05C0460C` | | MIT | DISCoHAsH v1 |
| 145 | `discohash2` | `Discohash2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x8FF45ABF` | 4026.84 | 204.37 | 408.13 (6) | 1294 | | | | | 32 bits | Pass | 0/238 | 214.89 | 1.34 | `0x8FF45ABF` | | MIT | DISCoHAsH v2 |
| 146 | `discohash2-128` | `Discohash2-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x95E58C14` | 4153.48 | 226.54 | 429.24 (4) | 1294 | | | | | 32 bits | Pass | 0/238 | 241.00 | 1.34 | `0x95E58C14` | | MIT | DISCoHAsH v2 |
| 147 | `discoNONG` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | none (ignored) | GOOD | `0x9182A886` | 3661.23 | 397.81 | 651.23 (54) | 2995 | bad seeds | `0x00005147`, `0x00009dce`, `0x0000addc` (+1028) | | | | | | | | | | | discoNONG - discohash with 512-bit internal state 64-bit output |
| 148 | `fasthash32` | `fasthash-32` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0xE9481AFC` | 6107.05 | 40.49 | 237.83 (2) | 566 | UB | `0x880355f21e6d1965` | | | 64 bits | Fail | 59/250 | 41.45 | 2.00 | `0xE9481AFC` | | MIT | fast-hash 32bit |
| 149 | `fasthash64` | `fasthash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xA16231A7` | 5600.11 | 38.04 | 219.71 (2) | 509 | UB | `0x880355f21e6d1965` | | | 64 bits | Fail | 99/250 | 39.66 | 2.00 | `0xA16231A7` | | MIT | fast-hash 64bit |
| 150 | `floppsyhash` | `floppsyhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x0` | 35.72 | 1868.92 | 1411.07 (7) | 616 | | | | | 32 bits | Fail | 17/235 | 724.92 | 0.05 | `0x5F9F6226` | | MIT | slow hash designed for floating point hardware |
| 151 | `chaskey` | `chaskey-8.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xBB4F6706` | 1142.38 | 113.98 | 288.04 (1) | 1450 | PerlinNoise | | | | none | Pass | 0/238 | 95.01 | 0.37 | `0x4DA0DD3A` | cryptographic | public domain | mouha.be/chaskey/ with added seed support |
| 152 | `SipHash` | `SipHash-2-4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0xC58D7F9C` | 953.51 | 147.32 | 332.02 (5) | 1071 | | | | | over 64 bits | Pass | 0/235 | 152.26 | 0.32 | `0x57B661ED` | cryptographic | MIT | SipHash 2-4 - SSSE3 optimized |
| 153 | `HalfSipHash` | `HalfSipHash` | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0xA7A05F72` | 1128.85 | 80.48 | 260.42 (2) | 603 | zeroes | | | | 64 bits | Pass | 0/238 | 81.75 | 0.34 | `0xD2BE7FD8` | cryptographic | MIT | HalfSipHash 2-4, 32bit |
| 154 | `GoodOAAT` | `GoodOAAT` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x7B14EEE5` | 744.67 | 87.23 | 243.36 (2) | 119 | | `0x3b00` | | | 32 bits | Pass | 0/235 | 86.50 | 0.24 | `0x7B14EEE5` | | MIT | Small non-multiplicative OAAT |
| 155 | `prvhash64_64m` | `prvhash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x1A3F7B06` | 3573.29 | 51.42 | 477.94 (4) | 403 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 51.07 | 0.94 | `0x1A3F7B06` | | MIT | prvhash64m 4.3.4 64bit |
| 156 | `prvhash64_64` | `prvhash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x1A3F7B06` | 3379.95 | 51.12 | 516.80 (46) | 387 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 51.07 | 0.94 | `0x1A3F7B06` | | MIT | prvhash64 4.3.4 64bit |
| 157 | `prvhash64_128` | `prvhash-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x316B4D9F` | 3352.65 | 69.16 | 495.87 (5) | 657 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/238 | 71.07 | 0.92 | `0x316B4D9F` | | MIT | prvhash64 4.3.4 128bit |
| 158 | `prvhash64s_64` | `prvhash-64.incr` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x891521D6` | 6724.41 | 266.09 | 438.90 (3) | 5442 | | | `#ifdef HAVE_INT64` | | over 64 bits | Pass | 0/238 | 188.16 | 2.29 | `0x891521D6` | | MIT | prvhash64s 4.3.4 64bit |
| 159 | `prvhash64s_128` | `prvhash-128.incr` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x0199728A` | 6703.66 | 326.46 | 508.67 (3) | 5618 | | | `#ifdef HAVE_INT64` | | over 64 bits | Pass | 0/238 | 279.13 | 2.17 | `0x0199728A` | | MIT | prvhash64s 4.3.4 128bit |
| 160 | `SipHash13` | `SipHash-1-3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x29C010BF` | 1803.96 | 107.93 | 304.05 (3) | 772 | 0.9% bias | | | | over 64 bits | Pass | 0/238 | 110.12 | 0.61 | `0x8936B193` | cryptographic | MIT | SipHash 1-3 - SSSE3 optimized |
| 161 | `TSip` | `TinySipHash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x5C2395A5` | 4330.87 | 52.43 | 245.31 (2) | 393 | !msvc | | `#ifndef _MSC_VER` | | 64 bits | Fail | 14/250 | 41.71 | 1.50 | `0x75C732C0` | | MIT | Damian Gryski's Tiny SipHash variant |
| 162 | `seahash` | `seahash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xF0374078` | 8240.58 | 58.61 | 260.35 (1) | 871 | PerlinNoise, !msvc | | `#ifndef _MSC_VER` and `#ifdef HAVE_INT64` | | 64 bits | Fail | 45/250 | 55.06 | 2.67 | `0xF0374078` | | MIT | seahash (64-bit, little-endian) |
| 163 | `seahash32low` | | 32 | 2⁻³² | 77 163 | 64 bits | GOOD | `0x712F0EE8` | 8245.91 | 58.61 | 264.07 (2) | 871 | PerlinNoise 32, !msvc | | `#ifndef _MSC_VER` and `#ifdef HAVE_INT64` | | | | | | | | | | seahash - lower 32bit |
| 164 | `clhash` | `CLhash.bitmix` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 22932.36 | 67.22 | 262.78 (2) | 1167 | PerlinNoise, machine-specific (x64 SSE4.2) | `0xb3816f6a2c68e530`, `711` | `#if defined(HAVE_SSE42) && defined(__x86_64__)` | | none | Fail | 23/250 | 60.80 | 7.33 | `0xAAC87C33` | CLMUL, lookup table | GPL 3.0 | carry-less mult. hash -DBITMIX (64-bit for x64, SSE4.2) |
| 165 | `HighwayHash64` | `HighwayHash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 6242.58 | 99.55 | 248.41 (3) | 2546 | | | `#ifdef HAVE_HIGHWAYHASH` | | over 64 bits | Pass | 0/238 | 73.30 | 2.90 | `0xF3246108` | cryptographic | Apache 2.0 | Google HighwayHash (portable with dylib overhead) |
| 166 | `Murmur3F` | `MurmurHash3-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 32 bits | GOOD | `0x6384BA69` | 7104.08 | 52.69 | 216.34 (2) | 632 | UB | `0x87c37b91114253d5` | `#if __WORDSIZE >= 64` | ✓ | 32 bits | Fail | 87/250 | 47.59 | 2.36 | `0x6384BA69` | | MIT | MurmurHash3 for x64, 128-bit |
| 167 | `MUM` | `mum1.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xA973C6C0` | 9631.26 | 34.78 | 219.14 (2) | 1912 | UB, too many bad seeds, machine-specific (32/64 differs) | `0x0` | | | 64 bits | Fail | 17/250 | 46.60 | 1.21 | `0xA973C6C0` | | MIT | github.com/vnmakarov/mum-hash |
| 168 | `MUMlow` | | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x7F898826` | 8532.17 | 35.95 | 238.37 (2) | 1912 | UB, 5 bad seeds | `0x11fb062a`, `0x3ca9411b`, `0x3edd9a7d` (+2) | | | | | | | | | | | github.com/vnmakarov/mum-hash |
| 169 | `MUMv3` | `mum3.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xB5560703` | 16628.30 | 21.58 | 180.82 (24) | 1177 | PerlinNoise, too many bad seeds, machine-specific (32/64 differs) | `0x0`, `0x8bd72b8c`, `0xf00000ff` | | | 64 bits | Fail | 126/250 | 34.37 | 2.02 | `0xB5560703` | | MIT | github.com/vnmakarov/mum-hash |
| 170 | `xmsx32` | `xmsx` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x6B54E1D4` | 2105.03 | 45.35 | 238.92 (3) | 189 | 2 bad seeds | `0x1505929f`, `0xf0a6a74a` | | | 32 bits | Pass | 0/250 | 42.56 | 0.67 | `0x6B54E1D4` | | BSD | XMSX-32 |
| 171 | `mirhash` | `mir.inexact` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x422A66FC` | 5793.85 | 38.15 | 212.91 (2) | 1112 | UB, 2^36 bad seeds, LongNeighbors, machine-specific (32/64 differs) | `0x0`, `0x5e74c778`, `0xa521f17b` (+1) | `#ifdef HAVE_INT64` | | 64 bits | Fail | 17/250 | 48.62 | 1.33 | `0x422A66FC` | | MIT | mirhash |
| 172 | `mirhash32low` | | 32 | 2⁻³² | 77 163 | 64 bits | POOR | `0xD50D1F09` | 6168.04 | 38.35 | 234.32 (2) | 1112 | UB, 4 bad seeds, Cyclic, LongNeighbors, machine-specific (32/64 differs) | `0x0`, `0x5e74c778`, `0xa521f17b` (+1) | `#ifdef HAVE_INT64` | | | | | | | | | | mirhash - lower 32bit |
| 173 | `mirhashstrict` | `mir.inexact` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x422A66FC` | 3587.79 | 49.85 | 219.49 (2) | 699 | | `0x7fcc747f` | `#ifdef HAVE_INT64` | | 64 bits | Fail | 17/250 | 48.62 | 1.33 | `0x422A66FC` | | MIT | mirhashstrict (portable, 64-bit, little-endian) |
| 174 | `mirhashstrict32low` | | 32 | 2⁻³² | 77 163 | 64 bits | POOR | `0xD50D1F09` | 3642.77 | 50.16 | 233.34 (3) | 1112 | 1 bad seed, MomentChi2 9 | `0x7fcc747f` | `#ifdef HAVE_INT64` | | | | | | | | | | mirhashstrict - lower 32bit |
| 175 | `City64low` | | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x6C4EF416` | 13119.17 | 47.92 | 251.81 (2) | 1120 | | | | | | | | | | | | | Google CityHash64WithSeed (v1.1)(low 32-bits) |
| 176 | `City128` | `CityHash-128.seed1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x305C0D9A` | 14472.20 | 88.71 | 285.61 (10) | 1841 | | | `#if defined(HAVE_SSE42) && defined(__x86_64__)` | ✓ | over 64 bits | | | | | `0x305C0D9A` | | MIT | Google CityHash128WithSeed (v1.1) |
| 177 | `CityCrc128` | `CityHashCrc-128.seed1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x98C09AB4` | 12255.05 | 89.25 | 281.85 (2) | 200 | | | `#if defined(HAVE_SSE42) && defined(__x86_64__)` | | over 64 bits | Fail | 7/250 | 70.24 | 6.00 | `0x98C09AB4` | CRC | MIT | Google CityHashCrc128WithSeed SSE4.2 (v1.1) |
| 178 | `CityCrc256` | `CityHashCrc-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | none (ignored) | GOOD | `0x2A7036C8` | 12428.45 | 164.93 | 358.73 (2) | 1729 | | | `#if defined(HAVE_SSE42) && defined(__x86_64__)` | | none | Fail | 21/238 | 183.24 | 6.01 | `0xB5F99A90` | CRC | MIT | Google CityHashCrc256 SSE4.2 (v1.1) |
| 179 | `FarmHash32` | | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x0` | 22136.14 | 46.75 | 258.73 (2) | 11489 | machine-specific (x64 SSE4/AVX) | `0x2b091701` | | | | | | | | | | | Google FarmHash32WithSeed |
| 180 | `FarmHash64` | `FarmHash-64.NA` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xEBC4A679` | 12929.54 | 46.82 | 238.50 (2) | 3758 | | | | | 64 bits | Fail | 45/250 | 51.35 | 4.69 | `0xEBC4A679` | | MIT | Google FarmHash64WithSeed |
| 181 | `FarmHash128` | `FarmHash-128.CC.seed1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x305C0D9A` | 14454.12 | 68.09 | 254.04 (2) | | | | | | over 64 bits | Fail | 2/250 | 69.48 | 4.85 | `0x305C0D9A` | | MIT | Google FarmHash128WithSeed |
| 182 | `farmhash32_c` | | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x0` | 22150.13 | 46.08 | 251.43 (3) | 762 | machine-specific (x64 SSE4/AVX) | `0x2b091701` | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` | | | | | | | | | | farmhash32\_with\_seed (C99) |
| 183 | `farmhash64_c` | `FarmHash-64.NA` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xEBC4A679` | 12925.73 | 46.76 | 239.47 (2) | 3688 | | | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` | | 64 bits | Fail | 45/250 | 51.35 | 4.69 | `0xEBC4A679` | | MIT | farmhash64\_with\_seed (C99) |
| 184 | `farmhash128_c` | `FarmHash-128.CC.seed1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x305C0D9A` | 14284.69 | 68.80 | 256.66 (3) | 1890 | | | `#if defined(__SSE4_2__) && defined(__x86_64__) && !defined(HAVE_PTRSIZE_4)` | | over 64 bits | Fail | 2/250 | 69.48 | 4.85 | `0x305C0D9A` | | MIT | farmhash128\_with\_seed (C99) |
| 185 | `gxhash32` | | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0xEC19D715` | 47943.08 | 37.71 | 251.38 (2) | 736 | oob UB, AES only | `0xe1c1ec7d`, `0x0376a937`, `0x64ef3cc9` (+7) | `#ifdef HAVE_AESNI` | | | | | | | | | | gxHash, 32-bit, AES-only |
| 186 | `gxhash64` | `gxhash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x9189E456` | 48919.73 | 36.61 | 236.98 (3) | 720 | oob UB, AES only | | `#ifdef HAVE_AESNI` | | 64 bits | Fail | 24/250 | 37.35 | 19.14 | `0x48F84240` | AES | MIT | gxHash, 64-bit, AES-only, unportable |
| 187 | `xxHash64` | `XXH-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x024B7CF4` | 11176.72 | 49.67 | 230.24 (6) | 2783 | | | | | 64 bits | Fail | 9/250 | 52.60 | 3.99 | `0x8F8224C4` | | BSD | xxHash, 64-bit |
| 188 | `lanehash64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xD048C22B` | 130737.76 | 17.22 | 102.33 (9) | | | | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` | | | | | | | | | | lanehash, 64-bit, chained NH-32 stripes + mum short path, no AES |
| 189 | `lanehash128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0xC2F39939` | 130363.17 | 18.65 | 100.17 (1) | | Sparse high32 | | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` | | | | | | | | | | lanehash, 128-bit, chained NH-32 stripes + mum short path, no AES |
| 190 | `lanehash_aes64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x9FF60BEF` | 43802.63 | 21.82 | 119.16 (2) | 3834 | AES only | | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` | | | | | | | | | | lanehash\_aes, 64-bit, AES lanes + mum short path |
| 191 | `lanehash_aes128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x1A79672D` | 44261.24 | 25.31 | 138.73 (2) | 3638 | Sparse high32, AES only | | `#if !(defined(HAVE_PTRSIZE_4) && !defined(HAVE_NEON))` | | | | | | | | | | lanehash\_aes, 128-bit, AES lanes + mum short path |
| 192 | `xxhash256` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | undetermined | GOOD | `0x024B7CF4` | | | | | | | `#if 0` | | | | | | | | | | xxhash256, 64-bit unportable |
| 193 | `Spooky32` | `SpookyHash1-32` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x3F798BBB` | 13623.84 | 55.45 | 248.11 (3) | 2221 | UB | `0x111af082`, `0x26bb3cda`, `0x94c4f96c` (+1) | | ✓ | 64 bits | Pass | 0/250 | 58.84 | 4.40 | `0x3F798BBB` | | public domain | Bob Jenkins' SpookyHash, 32-bit result |
| 194 | `Spooky64` | `SpookyHash1-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xA7F955F1` | 13400.95 | 55.06 | 244.86 (3) | 2221 | UB | | | ✓ | 64 bits | Fail | 6/250 | 58.84 | 4.41 | `0xA7F955F1` | | public domain | Bob Jenkins' SpookyHash, 64-bit result |
| 195 | `Spooky128` | `SpookyHash1-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x8D263080` | 13621.00 | 59.05 | 235.42 (2) | 2221 | UB | | | ✓ | 64 bits | Fail | 14/250 | 63.26 | 4.40 | `0x8D263080` | | public domain | Bob Jenkins' SpookyHash, 128-bit result |
| 196 | `SpookyV2_32` | `SpookyHash2-32` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0xA48BE265` | 13626.51 | 56.19 | 256.57 (19) | 2069 | | | | | 64 bits | Pass | 0/250 | 60.20 | 4.40 | `0xA48BE265` | | public domain | Bob Jenkins' SpookyV2, 32-bit result |
| 197 | `SpookyV2_64` | `SpookyHash2-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x972C4BDC` | 13401.13 | 56.18 | 246.33 (11) | 2069 | | | | | 64 bits | Fail | 6/250 | 60.20 | 4.40 | `0x972C4BDC` | | public domain | Bob Jenkins' SpookyV2, 64-bit result |
| 198 | `SpookyV2_128` | `SpookyHash2-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 128 bits | GOOD | `0x893CFCBE` | 12252.64 | 58.94 | 235.46 (2) | 2069 | | | | | 64 bits | Fail | 15/250 | 64.52 | 4.40 | `0x893CFCBE` | | public domain | Bob Jenkins' SpookyV2, 128-bit result |
| 199 | `pengyhash` | `pengyhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 32 bits | GOOD | `0x1FC2217B` | 13347.17 | 74.79 | 278.74 (3) | 846 | | | | | 64 bits | Fail | 16/250 | 89.81 | 3.80 | `0x861A1254` | | GPL 3.0 | pengyhash |
| 200 | `mx3` | `mx3.v1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x4DB51E5B` | 9332.99 | 47.07 | 221.61 (2) | 766 | UB | `0x10` | | | 64 bits | Fail | 45/250 | 49.48 | 3.21 | `0x4DB51E5B` | | public domain | mx3 64bit |
| 201 | `umash32` | | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0x9451AF3B` | 24556.40 | 40.01 | 490.26 (33) | 1530 | | `0x36ced9cb`, `0x707764c0`, `0x7a1583fe` (+2) | `#ifdef HAVE_UMASH` | | | | | | | | | | umash 32 |
| 202 | `umash32_hi` | | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0x0CC4850F` | 26601.23 | 42.25 | 484.02 (17) | 1530 | | | `#ifdef HAVE_UMASH` | | | | | | | | | | umash 32 hi |
| 203 | `umash64` | `UMASH-64.reseed` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x161495C6` | 26628.37 | 39.87 | 502.33 (38) | 1530 | | | `#ifdef HAVE_UMASH` | | 64 bits | Fail | 36/250 | 41.90 | 6.09 | `0x161495C6` | CLMUL, lookup table | MIT | umash 64 |
| 204 | `umash128` | `UMASH-128.reseed` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | secret key | GOOD | `0x36D4EC95` | 14523.56 | 43.73 | 475.12 (12) | 1530 | | | `#ifdef HAVE_UMASH` | | 64 bits | Fail | 37/250 | 44.95 | 3.79 | `0x36D4EC95` | CLMUL, lookup table | MIT | umash 128 |
| 205 | `halftime_hash64` | `HalftimeHash-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 4801.79 | 99.05 | 310.02 (2) | 444 | | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` | | 64 bits | Fail | 36/250 | 83.75 | 2.02 | `0xED42E424` | lookup table | MIT | NH tree hash variant |
| 206 | `halftime_hash128` | `HalftimeHash-128` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 18220.34 | 94.31 | 307.50 (1) | 444 | UB overflow | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` | | 64 bits | Fail | 48/250 | 95.58 | 6.87 | `0x952DF141` | lookup table | MIT | NH tree hash variant |
| 207 | `halftime_hash256` | `HalftimeHash-256` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 18249.32 | 97.56 | 322.85 (2) | 444 | UB overflow | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` | | 64 bits | Fail | 50/250 | 100.89 | 11.70 | `0x912330EA` | lookup table | MIT | NH tree hash variant |
| 208 | `halftime_hash512` | `HalftimeHash-512` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x0` | 10906.18 | 118.54 | 326.76 (3) | 444 | | `0xc61d672b`, `0xcc70c4c1798e4a6f`, `0xd3833e804f4c574b` (+4) | `#ifndef _MSC_VER` | | 64 bits | Fail | 60/250 | 113.77 | 9.51 | `0x1E0F99EA` | lookup table | MIT | NH tree hash variant |
| 209 | `t1ha2_atonce` | `t1ha2-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x8F16C948` | 14275.60 | 37.02 | 224.12 (2) | 660 | Zeroes low3 | | | | 64 bits | Fail | 3/250 | 40.44 | 4.62 | `0x8F16C948` | | zlib | Fast Positive Hash (portable |
| 210 | `t1ha2_stream` | `t1ha2-64.incr` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | POOR | `0xDED9B580` | 13673.22 | 81.12 | 263.88 (3) | 1643 | Sparse, Permutation, LongNeighbors | | | | 64 bits | Fail | 18/250 | 78.10 | 4.87 | `0xDED9B580` | | zlib | Fast Positive Hash (portable) |
| 211 | `t1ha2_atonce128` | `t1ha2-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0xB44C43A1` | 14059.85 | 56.44 | 251.53 (3) | 692 | LongNeighbors | | | | 64 bits | Fail | 1/250 | 61.28 | 4.89 | `0xB44C43A1` | | zlib | Fast Positive Hash (portable) |
| 212 | `t1ha2_stream128` | `t1ha2-128.incr` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | POOR | `0xE929E756` | 13913.43 | 94.60 | 296.15 (4) | 1570 | Sparse, Permutation, LongNeighbors | | | | 64 bits | Fail | 23/250 | 104.53 | 4.87 | `0xE929E756` | | zlib | Fast Positive Hash (portable) |
| 213 | `t1ha0_aes_noavx` | `t1ha0.aesA` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xF07C4DA5` | 27335.47 | 37.34 | 230.59 (2) | 679 | LongNeighbors, machine-specific (x86 AES-NI) | | `#if T1HA0_AESNI_AVAILABLE` | | 64 bits | Fail | 5/250 | 40.19 | 9.11 | `0xF07C4DA5` | AES | zlib | Fast Positive Hash (AES-NI) |
| 214 | `t1ha0_aes_avx1` | `t1ha0.aesA` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xF07C4DA5` | 27881.52 | 36.78 | 227.13 (2) | 679 | LongNeighbors, machine-specific (x64 AVX) | | `#if T1HA0_AESNI_AVAILABLE` | | 64 bits | Fail | 5/250 | 40.19 | 9.11 | `0xF07C4DA5` | AES | zlib | Fast Positive Hash (AES-NI & AVX) |
| 215 | `t1ha0_aes_avx2` | `t1ha0.aesB` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x8B38C599` | 56581.83 | 36.51 | 223.59 (1) | 628 | LongNeighbors, machine-specific (x64 AVX2) | | `#if T1HA0_AESNI_AVAILABLE` | | 64 bits | Fail | 5/250 | 40.43 | 21.42 | `0x8B38C599` | AES | zlib | Fast Positive Hash (AES-NI & AVX2) |
| 216 | `ahash64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x00000000` | 9862.62 | 27.32 | 181.68 (1) | 412 | rust | | `#ifdef HAVE_AHASH_C` | | | | | | | | | | ahash 64bit |
| 217 | `xxh3` | `XXH3-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x39CD9E4A` | 20853.74 | 29.46 | 220.07 (2) | 744 | DiffDist bit 7 w. 36 bits, BIC | `0x47ebda34` | | | 64 bits | Fail | 27/250 | 30.69 | 12.82 | `0x1AAEE62C` | lookup table | BSD | xxHash v3, 64-bit |
| 218 | `xxh3low` | | 32 | 2⁻³² | 77 163 | 64 bits | GOOD | `0xFAE8467B` | 20568.96 | 29.98 | 243.82 (5) | 756 | | `0x47ebda34` | | | | | | | | | | | xxHash v3, 64-bit, low 32-bits part |
| 219 | `xxh128` | `XXH3-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0xEB61B3A0` | 19259.99 | 31.58 | 228.84 (2) | 1012 | | `0x47ebda34` | | | 64 bits | Fail | 36/250 | 36.13 | 12.72 | `0x288DAA94` | lookup table | BSD | xxHash v3, 128-bit |
| 220 | `xxh128low` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x54D1CC70` | 19555.88 | 30.85 | 227.83 (2) | 1012 | | `0x47ebda34` | | | | | | | | | | | xxHash v3, 128-bit, low 64-bits part |
| 221 | `silicium_hash64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x00000000` | | | | | | | | | | | | | | | | | SiliciumHash 64-bit (AVX2/SWAR) |
| 222 | `silicium_hash128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x00000000` | | | | | | | | | | | | | | | | | SiliciumHash 128-bit (AVX2/SWAR) |
| 223 | `wyhash32` | `wyhash-32` | 32 | 2⁻³² | 77 163 | 32 bits | GOOD | `0x09DE8066` | 2532.89 | 48.40 | 484.57 (1) | 426 | 4 bad and broken seeds, 32-bit | `0x51a43a0f`, `0x522235ae`, `0x99ac2b20` (+1) | `#ifdef HAVE_BIT32` | | 32 bits | Fail | 2/250 | 31.05 | 1.33 | `0x09DE8066` | | public domain | wyhash v4.2 (32-bit native) |
| 224 | `wyhash32low` | | 32 | 2⁻³² | 77 163 | 64 bits | GOOD | `0xC5DF9AA0` | 23104.85 | 28.56 | 239.71 (4) | 532 | 5 bad seeds | `0x1443cf9a`, `0x2ae375a2`, `0x729973d2` (+2) | `#ifdef HAVE_BIT32` is false | | | | | | | | | | wyhash v4.2 lower 32bit |
| 225 | `wyhash` | `wyhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x9DAE7DD3` | 22640.53 | 28.91 | 229.00 (2) | 532 | | | `#ifdef HAVE_BIT32` is false | | 64 bits | Fail | 15/250 | 29.31 | 6.98 | `0x9DAE7DD3` | | public domain | wyhash v4.2 (64-bit) |
| 226 | `w1hash` | `w1hash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x648948F1` | 14208.56 | 26.85 | 221.76 (2) | 1497 | | | `#ifdef HAVE_BIT32` is false | | 64 bits | Fail | 1/250 | 29.34 | 8.15 | `0x648948F1` | | BSD | w1hash (64-bit) |
| 227 | `rapidhash` | `rapidhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x1FDC65EE` | 21513.71 | 22.04 | 169.05 (29) | 487 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/250 | 30.41 | 8.82 | `0x1FDC65EE` | | MIT | rapidhash v3 |
| 228 | `rapidhashMicro` | `rapidhash-micro` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x6F183D61` | 21513.71 | 22.04 | 169.05 (29) | 487 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/250 | 30.21 | 7.94 | `0x6F183D61` | | MIT | rapidhashMicro |
| 229 | `rapidhashNano` | `rapidhash-nano` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x2C200DC7` | 21295.09 | 22.53 | 166.80 (31) | 782 | | | `#ifdef HAVE_INT64` | | 64 bits | Pass | 0/250 | 29.83 | 6.98 | `0x2C200DC7` | | MIT | rapidhashNano |
| 230 | `nmhash32` | `NMHASH` | 32 | 2⁻³² | 77 163 | undetermined | GOOD | | 12676.08 | 57.09 | 255.20 (2) | 2445 | | | | | 32 bits | Fail | 78/250 | 52.79 | 7.71 | `0x12A30553` | lookup table | BSD | nmhash32 |
| 231 | `nmhash32x` | `NMHASHX` | 32 | 2⁻³² | 77 163 | undetermined | GOOD | | 13072.64 | 42.09 | 288.12 (3) | 1494 | | | | | 32 bits | Fail | 100/250 | 39.77 | 7.72 | `0xA8580227` | lookup table | BSD | nmhash32x |
| 232 | `k-hashv32` | `khashv-32` | 32 | 2⁻³² | 77 163 | secret key | GOOD | `0xB69DF8EB` | 8393.09 | 53.95 | 252.98 (3) | 1280 | | | `#ifdef HAVE_KHASHV` | | over 64 bits | Pass | 0/250 | 57.20 | 3.20 | `0x2FBC65F8` | | MIT | Vectorized K-HashV, 32-bit |
| 233 | `k-hashv64` | `khashv-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0xA6B7E55B` | 9251.05 | 51.72 | 251.83 (2) | 1280 | | | `#ifdef HAVE_KHASHV` | | over 64 bits | Pass | 0/250 | 56.17 | 3.20 | `0x8598BACD` | | MIT | Vectorized K-HashV, 64-bit |
| 234 | `komihash` | `komihash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x8157FF6D` | 11143.94 | 33.67 | 483.19 (11) | 1015 | | | | | 64 bits | Pass | 0/250 | 32.62 | 6.24 | `0x8157FF6D` | | MIT | komihash 5.26 |
| 235 | `a5hash` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xBF40CAE1` | 9730.52 | 28.84 | 469.50 (5) | 372 | | | | | | | | | | | | | a5hash 5.13 |
| 236 | `polymur` | `polymurhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | secret key | GOOD | `0x4F894810` | 9913.53 | 41.68 | 232.56 (3) | 1141 | | | | | over 64 bits | Pass | 0/250 | 42.57 | 4.02 | `0x0722B1A7` | | zlib | github.com/orlp/polymur-hash v1, \_\_SIZEOF\_INT128\_\_ |
| 237 | `rainbow` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0x2972C52E` | 6115.72 | 71.13 | 470.84 (12) | 891 | | | | | | | | | | | | | Rainbow 64-bit v1.0.6 (aka Newhash 128-bit) |
| 238 | `rainbow-128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0xFD9BCADD` | 6463.52 | 68.99 | 474.64 (5) | 943 | | | | | | | | | | | | | Rainbow 128-bit v1.0.6 (aka Newhash 128-bit) |
| 239 | `rainbow-256` | | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 64 bits | GOOD | `0xB327563D` | 5455.59 | 86.42 | 522.60 (24) | 1245 | | | | | | | | | | | | | Rainbow 256-bit v1.0.6 (aka Newhash 256-bit) |
| 240 | `rainstorm` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | 64 bits | GOOD | `0xC8DB71D5` | 1848.60 | 181.45 | 657.74 (43) | 2086 | | | | | | | | | | | | | Rainstorm 64-bit v0.0.6 |
| 241 | `rainstorm-128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | 64 bits | GOOD | `0x17E0FC1B` | 1862.69 | 199.59 | 652.74 (10) | 2170 | | | | | | | | | | | | | Rainstorm 128-bit v0.0.6 |
| 242 | `rainstorm-256` | | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | 64 bits | GOOD | `0x8BDBF180` | 1908.41 | 240.20 | 697.98 (26) | 2529 | | | | | | | | | | | | | Rainstorm 256-bit v0.0.6 |
| 243 | `fifty2hash64` | | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | undetermined | GOOD | `0x3C368934` | | | | | | | | | | | | | | | | | 52hash, 64-bit |
| 244 | `fifty2hash128` | | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | undetermined | GOOD | `0x13CC0E73` | | | | | | | | | | | | | | | | | 52hash, 128-bit |
| 245 | | `a5hash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 26.91 | 2.63 | `0xADDE79B3` | | MIT | a5hash v5.21, 64-bit version |
| 246 | | `a5hash-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 30.27 | 6.92 | `0x89406B11` | | MIT | a5hash v5.21, 128-bit version |
| 247 | | `a5hash-128.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 24.64 | 6.92 | `0x14AD402C` | | MIT | a5hash v5.21, 128-bit version, 64-bit truncated (rh==NULL) |
| 248 | | `a5hash-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Pass | 0/250 | 25.81 | 2.36 | `0xA948D11B` | | MIT | a5hash v5.21, 32-bit version |
| 249 | | `Abseil32` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 226/250 | 42.94 | 1.84 | `0x9C56A962` | | Apache 2.0 | Abseil hash (for 32-bit environments) |
| 250 | | `Abseil64-city` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 162/250 | 22.35 | 4.04 | `0xBCA82904` | | Apache 2.0 | Abseil hash (for 64-bit environments, without 128-bit intrinsics) |
| 251 | | `Abseil64-llh` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 191/250 | 21.96 | 7.42 | `0x07203CDB` | | Apache 2.0 | Abseil hash (for 64-bit environments, with 128-bit intrinsics) |
| 252 | | `AquaHash` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | over 64 bits | Fail | 55/250 | 34.31 | 15.84 | `0x9E92BCC4` | AES | Apache 2.0 | AquaHash |
| 253 | | `ascon-CXOF-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/235 | 634.14 | 0.05 | `0xE10FA58E` | cryptographic | public domain | ascon v1.3 (CXOF, 128 bits) |
| 254 | | `ascon-CXOF-160` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | | | | | | | | | | | | none | Pass | 0/235 | 803.23 | 0.05 | `0x11322E56` | cryptographic | public domain | ascon v1.3 (CXOF, 160 bits) |
| 255 | | `ascon-CXOF-224` | 224 | 2⁻²²⁴ | 6.1 × 10³³ | | | | | | | | | | | | none | Pass | 0/235 | 974.19 | 0.05 | `0x785D0A00` | cryptographic | public domain | ascon v1.3 (CXOF, 224 bits) |
| 256 | | `ascon-CXOF-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | none | Pass | 0/235 | 976.36 | 0.05 | `0xA5E1BB45` | cryptographic | public domain | ascon v1.3 (CXOF, 256 bits) |
| 257 | | `ascon-CXOF-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Pass | 0/235 | 468.99 | 0.05 | `0x890656D0` | cryptographic | public domain | ascon v1.3 (CXOF, 32 bits) |
| 258 | | `ascon-CXOF-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | none | Pass | 0/235 | 472.82 | 0.05 | `0xDE139DAD` | cryptographic | public domain | ascon v1.3 (CXOF, 64 bits) |
| 259 | | `ascon-CXOFa-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/235 | 502.78 | 0.08 | `0x1BA0187E` | cryptographic | public domain | ascon v1.3 (CXOFa, 128 bits) |
| 260 | | `ascon-CXOFa-160` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | | | | | | | | | | | | none | Pass | 0/235 | 619.05 | 0.08 | `0x4C497968` | cryptographic | public domain | ascon v1.3 (CXOFa, 160 bits) |
| 261 | | `ascon-CXOFa-224` | 224 | 2⁻²²⁴ | 6.1 × 10³³ | | | | | | | | | | | | none | Pass | 0/235 | 739.19 | 0.08 | `0x3E72C645` | cryptographic | public domain | ascon v1.3 (CXOFa, 224 bits) |
| 262 | | `ascon-CXOFa-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | none | Pass | 0/235 | 742.67 | 0.08 | `0xF16DB475` | cryptographic | public domain | ascon v1.3 (CXOFa, 256 bits) |
| 263 | | `ascon-CXOFa-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Pass | 0/235 | 390.71 | 0.08 | `0x9921AAC3` | cryptographic | public domain | ascon v1.3 (CXOFa, 32 bits) |
| 264 | | `ascon-CXOFa-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | none | Pass | 0/235 | 389.85 | 0.08 | `0x6AE766D0` | cryptographic | public domain | ascon v1.3 (CXOFa, 64 bits) |
| 265 | | `blake2b-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/235 | 588.45 | 0.26 | `0x7DC97611` | cryptographic, lookup table | MIT | BLAKE 2b, 128-bit digest |
| 266 | | `chaskey-12` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/235 | 132.50 | 0.25 | `0x1E983B23` | cryptographic | public domain | Chaskey PRF (12 rounds, 128 bits) |
| 267 | | `chaskey-12.32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Pass | 0/235 | 123.78 | 0.25 | `0x672570CB` | cryptographic | public domain | Chaskey PRF (12 rounds, 32 bits) |
| 268 | | `chaskey-12.64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | none | Pass | 0/235 | 125.71 | 0.25 | `0x919290D6` | cryptographic | public domain | Chaskey PRF (12 rounds, 64 bits) |
| 269 | | `chaskey-8` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/238 | 101.72 | 0.37 | `0x48B645E4` | cryptographic | public domain | Chaskey PRF (8 rounds, 128 bits) |
| 270 | | `chaskey-8.32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Pass | 0/238 | 93.69 | 0.37 | `0xA984B318` | cryptographic | public domain | Chaskey PRF (8 rounds, 32 bits) |
| 271 | | `ChibiHash2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 34.53 | 6.15 | `0x65ED889A` | | public domain | ChibiHash64, v2 |
| 272 | | `CityMurmur.seed1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | | | | | `0x6593FD6D` | | MIT | CityMurmur (seeded low 64 bits) |
| 273 | | `CLhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | none | Fail | 186/250 | 48.19 | 7.36 | `0x2E554CB4` | CLMUL, lookup table | GPL 3.0 | Carryless multiplication hash, without -DBITMIX |
| 274 | | `CrapWow` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Unusable | 166/250 | 25.10 | 2.58 | `0x49ECB015` | | MIT | Noncryptohashzoo's CrapWow hash |
| 275 | | `CrapWow-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Unusable | 137/250 | 30.93 | 4.74 | `0x669D3A9B` | | MIT | Noncryptohashzoo's CrapWow64 hash |
| 276 | | `donothing-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Unusable | 249/250 | 0.00 | 229320.44 | `0x0` | mock | MIT | Do-Nothing function (measure call overhead) |
| 277 | | `donothingOAAT-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Unusable | 249/250 | 38.96 | 3.39 | `0x0` | mock | MIT | Do-Nothing OAAT function (measure call+OAAT overhead) |
| 278 | | `donothingOAAT-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Unusable | 249/250 | 39.10 | 3.41 | `0x0` | mock | MIT | Do-Nothing OAAT function (measure call+OAAT overhead) |
| 279 | | `falkhash1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Fail | 10/250 | 83.97 | 19.88 | `0xAEF96E69` | AES | MIT | Falkhash v1 |
| 280 | | `falkhash2` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Fail | 6/250 | 85.87 | 18.43 | `0x7FA15220` | AES | public domain | Falkhash v2 |
| 281 | | `FarmHash-32.CC` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 50/250 | 42.65 | 1.92 | `0x61DEEE7E` | | MIT | FarmHash Hash32WithSeed (CC version) |
| 282 | | `FarmHash-32.MK` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 42/250 | 42.82 | 1.53 | `0x0DC9AF39` | | MIT | FarmHash Hash32WithSeed (MK version) |
| 283 | | `FarmHash-32.NT` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 6/250 | 51.91 | 7.78 | `0x47AB39AF` | | MIT | FarmHash Hash32WithSeed (NT version) |
| 284 | | `FarmHash-32.SA` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 50/250 | 42.49 | 5.00 | `0x553B1655` | CRC | MIT | FarmHash Hash32WithSeed (SA version) |
| 285 | | `FarmHash-32.SU` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 51/250 | 42.49 | 5.99 | `0xE7A53C98` | AES, CRC | MIT | FarmHash Hash32WithSeed (SU version) |
| 286 | | `FarmHash-64.TE` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 37/250 | 51.87 | 7.78 | `0xF1BF42C3` | | MIT | FarmHash Hash64WithSeed (TE version) |
| 287 | | `FarmHash-64.UO` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 37/250 | 51.54 | 5.09 | `0x5438EF2C` | | MIT | FarmHash Hash64WithSeed (UO version) |
| 288 | | `FARSH-128.tweaked` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Fail | 23/238 | 234.44 | 3.48 | `0x302E1139` | lookup table | MIT | FARSH 128-bit (4 hash outputs, tweaked to include key len) |
| 289 | | `FARSH-256.tweaked` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Fail | 19/235 | 475.90 | 1.74 | `0x7A9B846A` | lookup table | MIT | FARSH 256-bit (8 hash outputs, tweaked to include key len) |
| 290 | | `FARSH-32.tweaked` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 19/250 | 63.86 | 13.78 | `0x866BC8B2` | lookup table | MIT | FARSH 32-bit (1 hash output, tweaked to include key len) |
| 291 | | `FARSH-64.tweaked` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 19/250 | 116.34 | 6.96 | `0x0EB4FFC9` | lookup table | MIT | FARSH 64-bit (2 hash outputs, tweaked to include key len) |
| 292 | | `fibonacci-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Unusable | 247/250 | 24.50 | 15.97 | `0x09952480` | | MIT | 32-bit wordwise Fibonacci hash (Knuth) |
| 293 | | `Fletcher-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Fail | 239/250 | 33.04 | 1.45 | `0x4FE14644` | | MIT | Fletcher's checksum, 32-bit, IV == len |
| 294 | | `Fletcher-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | none | Fail | 242/250 | 33.27 | 2.00 | `0x2E16C3AA` | | MIT | Fletcher's checksum, 64-bit, IV == len |
| 295 | | `fletcher2` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Unusable | 244/250 | 24.01 | 4.93 | `0x70FD3480` | | MIT | fletcher2 from ZFS (one lane, all 128 bits) |
| 296 | | `fletcher4` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | none | Unusable | 247/250 | 27.73 | 1.91 | `0x1F1358EF` | | MIT | fletcher4 from ZFS (one lane, all 256 bits) |
| 297 | | `FNV-1a-32.wordwise` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Unusable | 242/250 | 29.69 | 1.00 | `0x739801C5` | | MIT | 32-bit wordwise hash based on FNV-1a |
| 298 | | `FNV-Mulvey` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | none | Fail | 194/235 | 83.01 | 0.25 | `0x0E256555` | | MIT | FNV-Mulvey 32-bit |
| 299 | | `GoodhartHash1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Unusable | 96/250 | 73.28 | 5.33 | `0x78BE8F44` | | MIT | Goodhart Hash 1 (bad) |
| 300 | | `GoodhartHash2` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Fail | 82/250 | 75.46 | 5.33 | `0x16C82F7A` | | MIT | Goodhart Hash 2 (bad) |
| 301 | | `GoodhartHash3` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/250 | 124.69 | 0.44 | `0x504DEE5A` | | MIT | Goodhart Hash 3 |
| 302 | | `GoodhartHash4` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Fail | 5/250 | 88.87 | 1.32 | `0xE71EE0DC` | | MIT | Goodhart Hash 4 (bad) |
| 303 | | `GoodhartHash5` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/250 | 93.26 | 1.06 | `0x6F8788F7` | | MIT | Goodhart Hash 5 (bad) |
| 304 | | `GoodhartHash6` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/250 | 125.26 | 1.06 | `0x7EE56518` | | MIT | Goodhart Hash 6 (evil) |
| 305 | | `gxhash` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Fail | 25/250 | 37.26 | 19.56 | `0x64A77B47` | AES | MIT | GxHash (ported from Rust) |
| 306 | | `hasshe2.tweaked` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | none | Pass | 0/238 | 97.70 | 0.91 | `0x7FE1B096` | | public domain | hasshe2 (SSE2-oriented hash, tweaked to mix len into hash) |
| 307 | | `HighwayHash-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | over 64 bits | Pass | 0/238 | 99.09 | 2.87 | `0x232D434E` | cryptographic | Apache 2.0 | HighwayHash, 128-bit version |
| 308 | | `HighwayHash-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | over 64 bits | Pass | 0/238 | 154.48 | 2.88 | `0x0D50D328` | cryptographic | Apache 2.0 | HighwayHash, 256-bit version |
| 309 | | `jodyhash-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Unusable | 221/238 | 38.37 | 0.57 | `0x0B6C88D6` | | MIT | jodyhash v7.3, 32-bit |
| 310 | | `lookup3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 122/238 | 36.01 | 0.81 | `0x6AE8AB7C` | | public domain | Bob Jenkins' lookup3 (64-bit output) |
| 311 | | `mir.exact` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 37.80 | 2.20 | `0x00A393C8` | | MIT | MIR-hash, exact 128-bit mult |
| 312 | | `multiply-shift-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 230/250 | 18.61 | 1.51 | `0x34BAD85C` | lookup table, system specific | MIT | Dietzfelbinger Multiply-shift on strings, 32-bit blocks |
| 313 | | `mulxp1-hash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 15/250 | 30.12 | 2.86 | `0xA476BA89` | | Boost | mulxp1\_hash, 64-bit version |
| 314 | | `mulxp1-hash32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 87/250 | 28.88 | 7.82 | `0x59CBE0F0` | | Boost | mulxp1\_hash, 32-bit version |
| 315 | | `mulxp3-hash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 27.89 | 4.99 | `0xB4786096` | | Boost | mulxp3\_hash, 64-bit version |
| 316 | | `mulxp3-hash32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 60/250 | 27.84 | 5.07 | `0x8C944400` | | Boost | mulxp3\_hash, 32-bit version |
| 317 | | `mum1-realign.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x9E323D13` | | MIT | Mum-hash v1, unroll 2^1, exact mult, for aligned-only reads |
| 318 | | `mum1-realign.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x139A630F` | | MIT | Mum-hash v1, unroll 2^2, exact mult, for aligned-only reads |
| 319 | | `mum1-realign.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x0F1AC6C6` | | MIT | Mum-hash v1, unroll 2^3, exact mult, for aligned-only reads |
| 320 | | `mum1-realign.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xF47885FE` | | MIT | Mum-hash v1, unroll 2^4, exact mult, for aligned-only reads |
| 321 | | `mum1-realign.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xE11FC923` | | MIT | Mum-hash v1, unroll 2^1, inexact mult, for aligned-only reads |
| 322 | | `mum1-realign.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xBAFC050E` | | MIT | Mum-hash v1, unroll 2^2, inexact mult, for aligned-only reads |
| 323 | | `mum1-realign.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x56FA3D86` | | MIT | Mum-hash v1, unroll 2^3, inexact mult, for aligned-only reads |
| 324 | | `mum1-realign.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x59787144` | | MIT | Mum-hash v1, unroll 2^4, inexact mult, for aligned-only reads |
| 325 | | `mum1.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 36.67 | 2.57 | `0xCB93DE58` | | MIT | Mum-hash v1, unroll 2^1, exact mult |
| 326 | | `mum1.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 34.79 | 4.12 | `0x3EEAE2D4` | | MIT | Mum-hash v1, unroll 2^2, exact mult |
| 327 | | `mum1.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 34.38 | 4.37 | `0x7C0A2F98` | | MIT | Mum-hash v1, unroll 2^3, exact mult |
| 328 | | `mum1.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 18/250 | 34.36 | 3.95 | `0x280B2CC6` | | MIT | Mum-hash v1, unroll 2^4, exact mult |
| 329 | | `mum1.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 17/250 | 50.31 | 1.15 | `0x689214DF` | | MIT | Mum-hash v1, unroll 2^1, inexact mult |
| 330 | | `mum1.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 17/250 | 46.87 | 1.33 | `0x5FC8FC51` | | MIT | Mum-hash v1, unroll 2^3, inexact mult |
| 331 | | `mum1.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 17/250 | 46.96 | 1.87 | `0x2EF256D3` | | MIT | Mum-hash v1, unroll 2^4, inexact mult |
| 332 | | `mum2-realign.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x3A8751BE` | | MIT | Mum-hash v2, unroll 2^1, exact mult, for aligned-only reads |
| 333 | | `mum2-realign.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x7C85EF5A` | | MIT | Mum-hash v2, unroll 2^2, exact mult, for aligned-only reads |
| 334 | | `mum2-realign.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x93F25600` | | MIT | Mum-hash v2, unroll 2^3, exact mult, for aligned-only reads |
| 335 | | `mum2-realign.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xA0DC8DF8` | | MIT | Mum-hash v2, unroll 2^4, exact mult, for aligned-only reads |
| 336 | | `mum2-realign.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x2D06BA6A` | | MIT | Mum-hash v2, unroll 2^1, inexact mult, for aligned-only reads |
| 337 | | `mum2-realign.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xF645F70A` | | MIT | Mum-hash v2, unroll 2^2, inexact mult, for aligned-only reads |
| 338 | | `mum2-realign.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xA8F0601A` | | MIT | Mum-hash v2, unroll 2^3, inexact mult, for aligned-only reads |
| 339 | | `mum2-realign.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x53A9484D` | | MIT | Mum-hash v2, unroll 2^4, inexact mult, for aligned-only reads |
| 340 | | `mum2.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 45/250 | 32.48 | 2.58 | `0x9B36F94C` | | MIT | Mum-hash v2, unroll 2^1, exact mult |
| 341 | | `mum2.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 62/250 | 30.50 | 4.13 | `0x40427228` | | MIT | Mum-hash v2, unroll 2^2, exact mult |
| 342 | | `mum2.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 70/250 | 30.46 | 4.37 | `0xB5D1CB5C` | | MIT | Mum-hash v2, unroll 2^3, exact mult |
| 343 | | `mum2.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 77/250 | 29.98 | 4.01 | `0x59AEDABF` | | MIT | Mum-hash v2, unroll 2^4, exact mult |
| 344 | | `mum2.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 45/250 | 42.68 | 1.14 | `0x1CC6D1E3` | | MIT | Mum-hash v2, unroll 2^1, inexact mult |
| 345 | | `mum2.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 63/250 | 38.39 | 1.26 | `0x62325A27` | | MIT | Mum-hash v2, unroll 2^2, inexact mult |
| 346 | | `mum2.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 71/250 | 38.46 | 1.33 | `0xF4DD9947` | | MIT | Mum-hash v2, unroll 2^3, inexact mult |
| 347 | | `mum2.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 78/250 | 38.88 | 1.87 | `0x62C46C55` | | MIT | Mum-hash v2, unroll 2^4, inexact mult |
| 348 | | `mum3-realign.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x26B6E56E` | | MIT | Mum-hash v3, unroll 2^1, exact mult, for aligned-only reads |
| 349 | | `mum3-realign.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x6A60097E` | | MIT | Mum-hash v3, unroll 2^2, exact mult, for aligned-only reads |
| 350 | | `mum3-realign.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xD45801EE` | | MIT | Mum-hash v3, unroll 2^3, exact mult, for aligned-only reads |
| 351 | | `mum3-realign.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x65C49B24` | | MIT | Mum-hash v3, unroll 2^4, exact mult, for aligned-only reads |
| 352 | | `mum3-realign.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xFB3DE98D` | | MIT | Mum-hash v3, unroll 2^1, inexact mult, for aligned-only reads |
| 353 | | `mum3-realign.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0xBFD7CE56` | | MIT | Mum-hash v3, unroll 2^2, inexact mult, for aligned-only reads |
| 354 | | `mum3-realign.inexact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x68CB735E` | | MIT | Mum-hash v3, unroll 2^3, inexact mult, for aligned-only reads |
| 355 | | `mum3-realign.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | | | | | `0x4975BD5E` | | MIT | Mum-hash v3, unroll 2^4, inexact mult, for aligned-only reads |
| 356 | | `mum3.exact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 25/250 | 29.63 | 2.65 | `0x3D14C6E2` | | MIT | Mum-hash v3, unroll 2^1, exact mult |
| 357 | | `mum3.exact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 33/250 | 27.31 | 5.07 | `0x3A556EB2` | | MIT | Mum-hash v3, unroll 2^2, exact mult |
| 358 | | `mum3.exact.unroll3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 36/250 | 27.39 | 5.97 | `0x8BD72B8C` | | MIT | Mum-hash v3, unroll 2^3, exact mult |
| 359 | | `mum3.exact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 36/250 | 28.06 | 5.86 | `0x0AD998DF` | | MIT | Mum-hash v3, unroll 2^4, exact mult |
| 360 | | `mum3.inexact.unroll1` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 98/250 | 38.52 | 1.39 | `0x475D666B` | | MIT | Mum-hash v3, unroll 2^1, inexact mult |
| 361 | | `mum3.inexact.unroll2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 116/250 | 33.78 | 1.81 | `0xF6E13B23` | | MIT | Mum-hash v3, unroll 2^2, inexact mult |
| 362 | | `mum3.inexact.unroll4` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 132/250 | 35.17 | 1.97 | `0xE96A20C0` | | MIT | Mum-hash v3, unroll 2^4, inexact mult |
| 363 | | `MuseAir` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 28.45 | 7.19 | `0xF89F1683` | | public domain | MuseAir v0.3 |
| 364 | | `MuseAir-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 30.97 | 7.15 | `0xD3DFE238` | | public domain | MuseAir v0.3, 128 bits |
| 365 | | `MuseAir-128.bfast` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 30.97 | 8.18 | `0x27939BF1` | | public domain | MuseAir v0.3, 128 bits, bfast version |
| 366 | | `MuseAir.bfast` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 26.28 | 8.64 | `0xC61BEE56` | | public domain | MuseAir v0.3, bfast version |
| 367 | | `mx3.v2` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 36/250 | 51.59 | 3.21 | `0x527399AD` | | public domain | mx3 (revision 2) |
| 368 | | `mx3.v3` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 36/250 | 49.38 | 3.76 | `0x7B287B65` | | public domain | mx3 (revision 3) |
| 369 | | `pair-multiply-shift-32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 226/250 | 23.26 | 2.24 | `0xFC284F0F` | lookup table, system specific | MIT | Dietzfelbinger Pair-multiply-shift strings, 32-bit blocks |
| 370 | | `perl-jenkins` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 164/235 | 95.00 | 0.20 | `0xE3ED0E54` | | GPL 3.0 | Bob Jenkins' OAAT hash (from old perl5 code) |
| 371 | | `perl-stadtx` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | over 64 bits | Fail | 11/250 | 38.51 | 4.72 | `0xD983938D` | | GPL 3.0 | Stadtx hash from perl5 |
| 372 | | `perl-zaphod32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | over 64 bits | Fail | 12/250 | 39.48 | 1.30 | `0x2DC19200` | | GPL 3.0 | Zaphod32 hash from perl5 |
| 373 | | `perl-zaphod32.sbox128` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | over 64 bits | Fail | 76/250 | 27.20 | 1.31 | `0x1C203149` | lookup table | GPL 3.0 | Zaphod32 hash with sbox32 and 128-bit seeding from perl5 |
| 374 | | `poly-mersenne.deg0` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 64 bits | Fail | 161/238 | 45.22 | 0.49 | `0x5D4B947A` | system specific | BSD | Degree 0 Hashing mod 2^61-1 |
| 375 | | `rainbow` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 61.63 | 1.28 | `0xED7533D3` | | Apache 2.0 | Rainbow v3.7.1 |
| 376 | | `rainbow-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 62.06 | 1.27 | `0xFF03173F` | | Apache 2.0 | Rainbow 128-bit v3.7.1 |
| 377 | | `rainbow-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 103.65 | 1.28 | `0x65F4A210` | | Apache 2.0 | Rainbow 256-bit v3.7.1 |
| 378 | | `rainstorm` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/238 | 184.06 | 0.58 | `0xCECC05CE` | | Apache 2.0 | Rainstorm v3.7.1 |
| 379 | | `rainstorm-128` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | 64 bits | Pass | 0/238 | 196.96 | 0.58 | `0xD6D12547` | | Apache 2.0 | Rainstorm 128-bit v3.7.1 |
| 380 | | `rainstorm-256` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Pass | 0/238 | 241.15 | 0.59 | `0x72D3356F` | | Apache 2.0 | Rainstorm 256-bit v3.7.1 |
| 381 | | `rapidhash-micro.protected` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 31.99 | 6.47 | `0xC7F9987C` | | MIT | rapidhashMicro v3, 64-bit protected version |
| 382 | | `rapidhash-nano.protected` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 31.36 | 5.69 | `0x7A2FA761` | | MIT | rapidhashNano v3, 64-bit protected version |
| 383 | | `rapidhash.protected` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 31.98 | 6.58 | `0x72C9270A` | | MIT | rapidhash v3, 64-bit protected version |
| 384 | | `RH512` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 79.73 | 2.50 | `0xD5E60849` | | MIT | RH512 Pro Implementation (low 256 bits) |
| 385 | | `RH512-64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 76.05 | 2.50 | `0x3C481EA3` | | MIT | RH512 Pro Implementation (low 64 bits) |
| 386 | | `RH512h` | 256 | 2⁻²⁵⁶ | 4.0 × 10³⁸ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 82.13 | 2.50 | `0xE42C2BF3` | | MIT | RH512 Pro Implementation (high 256 bits) |
| 387 | | `rust-ahash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | over 64 bits | Fail | 8/250 | 69.80 | 2.51 | `0x3BF4383B` | AES | MIT | aHash (ported from Rust, AES-based version) |
| 388 | | `rust-ahash-fb` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | over 64 bits | Pass | 0/250 | 29.14 | 4.52 | `0x53C9F167` | | MIT | aHash (ported from Rust, fallback version) |
| 389 | | `rust-ahash-fb.nofold` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | over 64 bits | Pass | 0/250 | 31.01 | 3.46 | `0x3FDD068C` | | MIT | aHash (ported from Rust, fallback version, folded\_multiply disabled) |
| 390 | | `rust-ahash.noshuf` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | over 64 bits | Fail | 7/250 | 74.39 | 0.66 | `0x84CD29E5` | AES | MIT | aHash (ported from Rust, AES-based version, without shuffle) |
| 391 | | `rust-fxhash32` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 186/250 | 34.94 | 3.94 | `0xC8D7717D` | | MIT | Rust FxHash v2.1.1 32-bit version |
| 392 | | `rust-fxhash32.mix` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | 32 bits | Fail | 14/250 | 40.91 | 3.94 | `0xD2DC6A74` | | MIT | Rust FxHash v2.1.1 32-bit version, with unofficial extra mixing |
| 393 | | `rust-fxhash64` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 165/250 | 30.02 | 4.99 | `0x8F177350` | | MIT | Rust FxHash v2.1.1 64-bit version |
| 394 | | `rust-fxhash64.mix` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 2/250 | 38.02 | 4.99 | `0xFC662413` | | MIT | Rust FxHash v2.1.1 64-bit version, with unofficial extra mixing |
| 395 | | `rust-fxhash64.mult32` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 214/250 | 31.14 | 3.94 | `0x686292BD` | | MIT | Rust FxHash v2.1.1 64-bit version |
| 396 | | `rust-fxhash64.mult32.mix` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 7/250 | 38.76 | 3.94 | `0x9CF6B62E` | | MIT | Rust FxHash v2.1.1 64-bit version, with unofficial extra mixing |
| 397 | | `rust-rapidhash` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 35.01 | 8.66 | `0x562EF848` | | MIT | rapidhash rust, quality::RapidHasher::new() |
| 398 | | `rust-rapidhash.fast` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 132/250 | 24.46 | 8.67 | `0xB891F260` | | MIT | rapidhash rust, fast::RapidHasher::new() |
| 399 | | `rust-rapidhash.p` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Pass | 0/250 | 45.47 | 3.02 | `0x77BCDA91` | | MIT | rapidhash rust, quality::RapidHasher::new(), no wide mult |
| 400 | | `rust-rapidhash.p.fast` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 131/250 | 28.86 | 3.07 | `0x2955B659` | | MIT | rapidhash rust, fast::RapidHasher::new(), no wide mult |
| 401 | | `SHA-1` | 128 | 2⁻¹²⁸ | 2.2 × 10¹⁹ | | | | | | | | | | | | none | Pass | 0/235 | 490.81 | 0.48 | `0xE444A591` | cryptographic, weak cryptographic | MIT | SHA-1 |
| 402 | | `SipHash-1-3.folded` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | over 64 bits | Pass | 0/238 | 111.38 | 0.61 | `0xC7BC11F8` | cryptographic | MIT | SipHash 1-3, XOR folded down to 32 bits |
| 403 | | `SipHash-2-4.folded` | 32 | 2⁻³² | 77 163 | | | | | | | | | | | | over 64 bits | Pass | 0/235 | 154.05 | 0.32 | `0xDD46AB1A` | cryptographic | MIT | SipHash 2-4, XOR folded down to 32 bits |
| 404 | | `TentHash` | 160 | 2⁻¹⁶⁰ | 1.4 × 10²⁴ | | | | | | | | | | | | none | Pass | 0/250 | 84.75 | 1.73 | `0x5FDAF416` | | MIT | TentHash |
| 405 | | `wyhash.strict` | 64 | 2⁻⁶⁴ | 5.1 × 10⁹ | | | | | | | | | | | | 64 bits | Fail | 15/250 | 31.49 | 5.79 | `0x82FE7E2E` | | public domain | wyhash v4.2, 64-bit strict version |
<!-- markdownlint-enable MD013 -->

## Only in the original SMHasher

These functions exist in the original SMHasher by Austin Appleby but not in
rurban/smhasher, at least not under the same name.

<!-- markdownlint-disable MD013 -->
| Name | Bits | Pair collision | 50% collision | Verification | Description |
| :----- | -----: | :--------------- | --------------: | :------------- | :------------ |
| `md5_32a` | 32 | 2⁻³² | 77 163 | `0xC10C356B` | MD5, first 32 bits of result |
| `sha1_32a` | 32 | 2⁻³² | 77 163 | `0xF9376EA7` | SHA1, first 32 bits of result |
| `FNV` | 32 | 2⁻³² | 77 163 | `0xE3CBBE91` | Fowler-Noll-Vo hash, 32-bit |
<!-- markdownlint-enable MD013 -->
