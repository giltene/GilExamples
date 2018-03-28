This is a small set of microbennchmark that can be used demonstrate cool AVX2 and AVX512 vectorization capabilities

To execute:
```
mvn clean package
${JAVA_HOME}/bin/java -jar VectorizationExample-benchmarks/target/benchmarks.jar
```

When run on a Haswell E5-2670 v3 @ 2.30GHz , I observe the following results:

With Zing 17.03.01:
```
Benchmark                                    (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven        65536        10000  thrpt    5   3.431 ± 0.023  ops/s
VectorizationExampleBench.doAddX                   65536        10000  thrpt    5   8.476 ± 0.326  ops/s
VectorizationExampleBench.doSumIfEvenLoop          65536        10000  thrpt    5  11.629 ± 0.153  ops/s
VectorizationExampleBench.doSumLoop                65536        10000  thrpt    5  18.143 ± 0.026  ops/s
VectorizationExampleBench.doSumShiftedLoop         65536        10000  thrpt    5  11.112 ± 0.013  ops/s
```

With HotSpot 8u131:
```
Benchmark                                    (arraySize)  (loopCount)   Mode  Cnt  Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven        65536        10000  thrpt    5  1.907 ± 0.079  ops/s
VectorizationExampleBench.doAddX                   65536        10000  thrpt    5  8.513 ± 0.305  ops/s
VectorizationExampleBench.doSumIfEvenLoop          65536        10000  thrpt    5  1.285 ± 0.013  ops/s
VectorizationExampleBench.doSumLoop                65536        10000  thrpt    5  3.479 ± 0.035  ops/s
VectorizationExampleBench.doSumShiftedLoop         65536        10000  thrpt    5  1.623 ± 0.077  ops/s
```

When run on a Skylake SP @2.0GHz (Google Cloud Engine), I observe the following results:
With Zing 17.08:

-XX:ARTAPort=33333 -XX:+UseTickProfiler -XX:+UseLLVMBasedDisassembler

```
Benchmark                                    (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven        65536        10000  thrpt   15   9.548 ± 0.140  ops/s
VectorizationExampleBench.doAddX                   65536        10000  thrpt   15  15.377 ± 0.290  ops/s
VectorizationExampleBench.doSumIfEvenLoop          65536        10000  thrpt   15  19.343 ± 0.438  ops/s
VectorizationExampleBench.doSumLoop                65536        10000  thrpt   15  20.517 ± 0.157  ops/s
VectorizationExampleBench.doSumShiftedLoop         65536        10000  thrpt   15  20.373 ± 0.359  ops/s
```

With -XX:-UseFalcon:
```
Benchmark                                    (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven        65536        10000  thrpt   15   2.808 ± 0.120  ops/s
VectorizationExampleBench.doAddX                   65536        10000  thrpt   15  14.668 ± 0.735  ops/s
VectorizationExampleBench.doSumIfEvenLoop          65536        10000  thrpt   15   1.613 ± 0.010  ops/s
VectorizationExampleBench.doSumLoop                65536        10000  thrpt   15   3.030 ± 0.137  ops/s
VectorizationExampleBench.doSumShiftedLoop         65536        10000  thrpt   15   1.712 ± 0.076  ops/s
```

With HotSpot 8u144:
```
Benchmark                                    (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven        65536        10000  thrpt   15   2.610 ± 0.084  ops/s
VectorizationExampleBench.doAddX                   65536        10000  thrpt   15  16.584 ± 0.904  ops/s
VectorizationExampleBench.doSumIfEvenLoop          65536        10000  thrpt   15   1.917 ± 0.041  ops/s
VectorizationExampleBench.doSumLoop                65536        10000  thrpt   15   3.972 ± 0.031  ops/s
VectorizationExampleBench.doSumShiftedLoop         65536        10000  thrpt   15   1.937 ± 0.048  ops/s
```
