# Shah

[![EO principles respected
here](https://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![We recommend IntelliJ
IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/fabriciofx/shah/blob/main/LICENSE.txt)
[![Hits-of-Code](https://hitsofcode.com/github/fabriciofx/shah)](https://hitsofcode.com/view/github/fabriciofx/shah)

## Introduction

**Shah** is a collection of non-cryptographic hash functions and a quality
test suite for them, written in object-oriented Java.

**Motivation**.
Non-cryptographic hash functions are everywhere: hash tables, bloom filters,
checksums, and sharding. Their quality is usually measured by
[SMHasher](https://github.com/aappleby/smhasher), a C++ test suite. Shah
brings the same SMHasher tests to Java, so any hash function written in Java
can be measured without leaving the JVM.

**Principles**.
These are the [design principles](http://www.elegantobjects.org#principles)
behind Shah.

## Features

- Hash functions:
  - [MurmurHash3](https://github.com/aappleby/smhasher) 32-bit
  - [Jenkins](https://en.wikipedia.org/wiki/Jenkins_hash_function)
    one-at-a-time 32-bit
  - [Jenkins](https://en.wikipedia.org/wiki/Jenkins_hash_function)
    lookup2 32-bit
  - "Do nothing" 32, 64, 128 and 256-bit baselines
- SMHasher tests: Avalanche, BIC, Collision, Distribution, Differential,
  DiffDist, Sanity, Verification, Seed, Bad Seeds, Sparse Key, Cyclic Key,
  Windowed Key, Permutation, Text, Words, Two Bytes, Zeroes, Appended
  Zeroes, PRNG, Perlin Noise and Moment Chi2
- Hash outputs of any width (32, 64, 128 and 256 bits)
- JMH benchmarks

All hash functions tested by SMHasher and SMHasher3, with their output width,
seed and known problems, are listed in [ALGORITHMS.md](ALGORITHMS.md).

## How to use

Shah is not published on Maven Central yet. Build and install it locally:

```bash
mvn clean install
```

Then add it to your `pom.xml`:

```xml
<dependency>
  <groupId>com.github.fabriciofx</groupId>
  <artifactId>shah</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Java version required: 21+.

### Usage

The API is built around four concepts:

- A `Key` is the input of a hash function;
- A `Seed` initializes a hash function;
- A `Func` is a hash function that produces a `Hash`;
- A `Measure` measures the quality of a hash function and returns a metric.

Let's hash a string with MurmurHash3:

```java
final String hex = new Murmur3Hash32(
    new KeyOf("hello"),
    new Seed32(42)
).hash().asString();
```

### Testing a hash function

Every test receives the hash function as a `(key, seed) -> hash` lambda.
Let's compute the avalanche bias of MurmurHash3 with 8-byte keys:

```java
final double bias = new AvalancheTest(
    (key, seed) -> new Murmur3Hash32(key, seed).hash(),
    new Seed32(12_345),
    8,
    new Seed32(54_321),
    500_000
).metric().bias().mean();
```

Or the collision ratio (actual / expected) of one million 16-byte keys:

```java
final double ratio = new CollisionTest(
    (key, seed) -> new Murmur3Hash32(key, seed).hash(),
    new Seed32(67_890),
    16,
    new Seed32(12_345),
    1_000_000
).metric().ratio().value();
```

Or the SMHasher verification code, to check if the implementation is
correct:

```java
final long code = new VerificationTest(
    (key, seed) -> new Murmur3Hash32(key, seed).hash()
).metric();
```

To see all tests applied to a hash function, please take a look in
[Murmur3Hash32Test](https://github.com/fabriciofx/shah/blob/main/src/test/java/com/github/fabriciofx/shah/func/appleby/Murmur3Hash32Test.java).

### Writing a hash function

To add a new hash function, implement `Func`, receiving a `Key` and a `Seed`,
and return one of `Hash32`, `Hash64`, `Hash128` or `Hash256`:

```java
public final class MyHash32 implements Func {
    private final Key key;
    private final Seed seed;

    public MyHash32(final Key key, final Seed seed) {
        this.key = key;
        this.seed = seed;
    }

    @Override
    public Hash hash() {
        ...
    }
}
```

### Benchmarks

To run the JMH benchmarks and generate a report:

```bash
mvn clean verify -Pbenchmarks
```

## Contributions

Contributions are welcome! Please, open an issue before submitting any kind
(ideas, documentation, code, ...) of contribution.

### How to compile it?

```bash
mvn clean install -Pqulice
```

## License

The MIT License (MIT)

Copyright (C) 2026 Fabrício Barros Cabral

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Acknowledgements

[@aappleby](https://github.com/aappleby) as Austin Appleby for:

- [SMHasher](https://github.com/aappleby/smhasher) test suite and
  MurmurHash3

[@rurban](https://github.com/rurban) as Reini Urban for:

- [SMHasher](https://github.com/rurban/smhasher) extended test suite

[@yegor256](https://github.com/yegor256) as Yegor Bugayenko
([Blog](https://www.yegor256.com)) for:

- [Elegant Objects](https://www.yegor256.com/elegant-objects.html) [Vol.
  1](http://amzn.to/2BXdZSs) and [Vol. 2](http://amzn.to/2BuFFP4) books
