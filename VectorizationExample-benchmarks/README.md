This is a small set of microbennchmark that can be used demonstrate cool AVX2 vectorization capabilities

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