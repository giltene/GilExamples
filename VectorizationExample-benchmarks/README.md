This is a small set of microbennchmark that can be used demonstrate cool AVX2 and AVX512 vectorization capabilities

To execute:
```
mvn clean package
${JAVA_HOME}/bin/java -jar target/benchmarks.jar
```

When run on a Haswell E5-2670 v3 @ 2.30GHz , I observe the following results:

With Zing 18.06:
```
Benchmark                                         (arraySize)  (elementsPerLoop)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven              1024         1000000000  thrpt    5   3.633 ± 0.007  ops/s
VectorizationExampleBench.doAddArraysIfEven             16384         1000000000  thrpt    5   3.292 ± 0.011  ops/s
VectorizationExampleBench.doAddArraysIfEven             65536         1000000000  thrpt    5   2.516 ± 0.089  ops/s
VectorizationExampleBench.doAddArraysIfEven            524288         1000000000  thrpt    5   2.437 ± 0.326  ops/s
VectorizationExampleBench.doAddArraysIfEven          67108864         1000000000  thrpt    5   1.149 ± 0.023  ops/s
VectorizationExampleBench.doAddArraysIfPredicate         1024         1000000000  thrpt    5   2.564 ± 0.016  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        16384         1000000000  thrpt    5   2.399 ± 0.005  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536         1000000000  thrpt    5   2.160 ± 0.182  ops/s
VectorizationExampleBench.doAddArraysIfPredicate       524288         1000000000  thrpt    5   2.243 ± 0.002  ops/s
VectorizationExampleBench.doAddArraysIfPredicate     67108864         1000000000  thrpt    5   1.043 ± 0.056  ops/s
VectorizationExampleBench.doAddX                         1024         1000000000  thrpt    5   8.198 ± 0.018  ops/s
VectorizationExampleBench.doAddX                        16384         1000000000  thrpt    5   5.629 ± 0.013  ops/s
VectorizationExampleBench.doAddX                        65536         1000000000  thrpt    5   5.601 ± 0.008  ops/s
VectorizationExampleBench.doAddX                       524288         1000000000  thrpt    5   4.273 ± 0.018  ops/s
VectorizationExampleBench.doAddX                     67108864         1000000000  thrpt    5   1.783 ± 0.024  ops/s
VectorizationExampleBench.doSumIfEvenLoop                1024         1000000000  thrpt    5  10.266 ± 0.160  ops/s
VectorizationExampleBench.doSumIfEvenLoop               16384         1000000000  thrpt    5   8.540 ± 1.164  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536         1000000000  thrpt    5   9.071 ± 0.025  ops/s
VectorizationExampleBench.doSumIfEvenLoop              524288         1000000000  thrpt    5   6.495 ± 0.052  ops/s
VectorizationExampleBench.doSumIfEvenLoop            67108864         1000000000  thrpt    5   2.426 ± 0.042  ops/s
VectorizationExampleBench.doSumIfPredicateLoop           1024         1000000000  thrpt    5   2.731 ± 0.002  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          16384         1000000000  thrpt    5   2.735 ± 0.005  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536         1000000000  thrpt    5   2.745 ± 0.031  ops/s
VectorizationExampleBench.doSumIfPredicateLoop         524288         1000000000  thrpt    5   2.762 ± 0.025  ops/s
VectorizationExampleBench.doSumIfPredicateLoop       67108864         1000000000  thrpt    5   1.987 ± 0.219  ops/s
VectorizationExampleBench.doSumLoop                      1024         1000000000  thrpt    5  15.634 ± 0.979  ops/s
VectorizationExampleBench.doSumLoop                     16384         1000000000  thrpt    5  12.406 ± 0.619  ops/s
VectorizationExampleBench.doSumLoop                     65536         1000000000  thrpt    5  12.218 ± 0.026  ops/s
VectorizationExampleBench.doSumLoop                    524288         1000000000  thrpt    5   6.947 ± 0.007  ops/s
VectorizationExampleBench.doSumLoop                  67108864         1000000000  thrpt    5   2.466 ± 0.012  ops/s
VectorizationExampleBench.doSumShiftedLoop               1024         1000000000  thrpt    5   8.244 ± 0.325  ops/s
VectorizationExampleBench.doSumShiftedLoop              16384         1000000000  thrpt    5   7.866 ± 0.602  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536         1000000000  thrpt    5   7.914 ± 0.732  ops/s
VectorizationExampleBench.doSumShiftedLoop             524288         1000000000  thrpt    5   6.322 ± 0.317  ops/s
VectorizationExampleBench.doSumShiftedLoop           67108864         1000000000  thrpt    5   1.907 ± 0.049  ops/s
```

With OpenJDK 8u162:
```
Benchmark                                         (arraySize)  (elementsPerLoop)   Mode  Cnt   Score    Error  Units
VectorizationExampleBench.doAddArraysIfEven              1024         1000000000  thrpt    5   1.232 ±  0.002  ops/s
VectorizationExampleBench.doAddArraysIfEven             16384         1000000000  thrpt    5   1.257 ±  0.001  ops/s
VectorizationExampleBench.doAddArraysIfEven             65536         1000000000  thrpt    5   1.257 ±  0.005  ops/s
VectorizationExampleBench.doAddArraysIfEven            524288         1000000000  thrpt    5   1.257 ±  0.002  ops/s
VectorizationExampleBench.doAddArraysIfEven          67108864         1000000000  thrpt    5   0.938 ±  0.375  ops/s
VectorizationExampleBench.doAddArraysIfPredicate         1024         1000000000  thrpt    5   1.561 ±  0.003  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        16384         1000000000  thrpt    5   1.527 ±  0.002  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536         1000000000  thrpt    5   1.381 ±  0.438  ops/s
VectorizationExampleBench.doAddArraysIfPredicate       524288         1000000000  thrpt    5   1.438 ±  0.001  ops/s
VectorizationExampleBench.doAddArraysIfPredicate     67108864         1000000000  thrpt    5   0.945 ±  0.002  ops/s
VectorizationExampleBench.doAddX                         1024         1000000000  thrpt    5  10.066 ±  0.048  ops/s
VectorizationExampleBench.doAddX                        16384         1000000000  thrpt    5   5.599 ±  0.209  ops/s
VectorizationExampleBench.doAddX                        65536         1000000000  thrpt    5   5.610 ±  0.038  ops/s
VectorizationExampleBench.doAddX                       524288         1000000000  thrpt    5   4.312 ±  0.004  ops/s
VectorizationExampleBench.doAddX                     67108864         1000000000  thrpt    5   1.928 ±  0.004  ops/s
VectorizationExampleBench.doSumIfEvenLoop                1024         1000000000  thrpt    5   0.848 ±  0.001  ops/s
VectorizationExampleBench.doSumIfEvenLoop               16384         1000000000  thrpt    5   0.844 ±  0.003  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536         1000000000  thrpt    5   0.843 ±  0.001  ops/s
VectorizationExampleBench.doSumIfEvenLoop              524288         1000000000  thrpt    5   0.842 ±  0.003  ops/s
VectorizationExampleBench.doSumIfEvenLoop            67108864         1000000000  thrpt    5   0.812 ±  0.105  ops/s
VectorizationExampleBench.doSumIfPredicateLoop           1024         1000000000  thrpt    5   2.029 ±  0.004  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          16384         1000000000  thrpt    5   2.104 ±  0.023  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536         1000000000  thrpt    5   2.093 ±  0.003  ops/s
VectorizationExampleBench.doSumIfPredicateLoop         524288         1000000000  thrpt    5   2.095 ±  0.067  ops/s
VectorizationExampleBench.doSumIfPredicateLoop       67108864         1000000000  thrpt    5   1.734 ±  0.089  ops/s
VectorizationExampleBench.doSumLoop                      1024         1000000000  thrpt    5   2.283 ±  0.013  ops/s
VectorizationExampleBench.doSumLoop                     16384         1000000000  thrpt    5   2.284 ±  0.003  ops/s
VectorizationExampleBench.doSumLoop                     65536         1000000000  thrpt    5   2.287 ±  0.004  ops/s
VectorizationExampleBench.doSumLoop                    524288         1000000000  thrpt    5   2.286 ±  0.016  ops/s
VectorizationExampleBench.doSumLoop                  67108864         1000000000  thrpt    5   1.861 ±  0.471  ops/s
VectorizationExampleBench.doSumShiftedLoop               1024         1000000000  thrpt    5   1.059 ±  0.001  ops/s
VectorizationExampleBench.doSumShiftedLoop              16384         1000000000  thrpt    5   1.076 ±  0.002  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536         1000000000  thrpt    5   1.075 ±  0.008  ops/s
VectorizationExampleBench.doSumShiftedLoop             524288         1000000000  thrpt    5   1.074 ±  0.018  ops/s
VectorizationExampleBench.doSumShiftedLoop           67108864         1000000000  thrpt    5   1.033 ±  0.003  ops/s
```

When run on a Skylake 4116 CPU @ 2.10GHz, I observe the following results:
With Zing 18.06:

```
Benchmark                                         (arraySize)  (elementsPerLoop)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven              1024         1000000000  thrpt    5  11.143 ± 0.011  ops/s
VectorizationExampleBench.doAddArraysIfEven             16384         1000000000  thrpt    5   5.676 ± 0.057  ops/s
VectorizationExampleBench.doAddArraysIfEven             65536         1000000000  thrpt    5   5.714 ± 0.016  ops/s
VectorizationExampleBench.doAddArraysIfEven            524288         1000000000  thrpt    5   2.518 ± 0.037  ops/s
VectorizationExampleBench.doAddArraysIfEven          67108864         1000000000  thrpt    5   1.167 ± 0.050  ops/s
VectorizationExampleBench.doAddArraysIfPredicate         1024         1000000000  thrpt    5   9.392 ± 0.024  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        16384         1000000000  thrpt    5   5.058 ± 0.025  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536         1000000000  thrpt    5   5.077 ± 0.008  ops/s
VectorizationExampleBench.doAddArraysIfPredicate       524288         1000000000  thrpt    5   2.245 ± 0.004  ops/s
VectorizationExampleBench.doAddArraysIfPredicate     67108864         1000000000  thrpt    5   1.051 ± 0.046  ops/s
VectorizationExampleBench.doAddX                         1024         1000000000  thrpt    5  13.973 ± 0.026  ops/s
VectorizationExampleBench.doAddX                        16384         1000000000  thrpt    5   9.097 ± 0.210  ops/s
VectorizationExampleBench.doAddX                        65536         1000000000  thrpt    5   9.210 ± 0.195  ops/s
VectorizationExampleBench.doAddX                       524288         1000000000  thrpt    5   4.853 ± 0.014  ops/s
VectorizationExampleBench.doAddX                     67108864         1000000000  thrpt    5   2.141 ± 0.040  ops/s
VectorizationExampleBench.doSumIfEvenLoop                1024         1000000000  thrpt    5  18.309 ± 0.496  ops/s
VectorizationExampleBench.doSumIfEvenLoop               16384         1000000000  thrpt    5  12.977 ± 0.051  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536         1000000000  thrpt    5  13.132 ± 0.080  ops/s
VectorizationExampleBench.doSumIfEvenLoop              524288         1000000000  thrpt    5   5.370 ± 0.009  ops/s
VectorizationExampleBench.doSumIfEvenLoop            67108864         1000000000  thrpt    5   2.345 ± 0.009  ops/s
VectorizationExampleBench.doSumIfPredicateLoop           1024         1000000000  thrpt    5  14.385 ± 0.299  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          16384         1000000000  thrpt    5  10.385 ± 0.298  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536         1000000000  thrpt    5  10.487 ± 0.211  ops/s
VectorizationExampleBench.doSumIfPredicateLoop         524288         1000000000  thrpt    5   4.071 ± 0.008  ops/s
VectorizationExampleBench.doSumIfPredicateLoop       67108864         1000000000  thrpt    5   1.984 ± 0.037  ops/s
VectorizationExampleBench.doSumLoop                      1024         1000000000  thrpt    5  26.082 ± 0.048  ops/s
VectorizationExampleBench.doSumLoop                     16384         1000000000  thrpt    5  13.129 ± 0.577  ops/s
VectorizationExampleBench.doSumLoop                     65536         1000000000  thrpt    5  13.378 ± 0.103  ops/s
VectorizationExampleBench.doSumLoop                    524288         1000000000  thrpt    5   6.131 ± 2.387  ops/s
VectorizationExampleBench.doSumLoop                  67108864         1000000000  thrpt    5   2.379 ± 0.010  ops/s
VectorizationExampleBench.doSumShiftedLoop               1024         1000000000  thrpt    5  18.493 ± 0.055  ops/s
VectorizationExampleBench.doSumShiftedLoop              16384         1000000000  thrpt    5  12.969 ± 0.048  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536         1000000000  thrpt    5  13.147 ± 0.037  ops/s
VectorizationExampleBench.doSumShiftedLoop             524288         1000000000  thrpt    5   5.027 ± 0.011  ops/s
VectorizationExampleBench.doSumShiftedLoop           67108864         1000000000  thrpt    5   2.352 ± 0.062  ops/s
```

With OpenJDK 8u162:
```
Benchmark                                         (arraySize)  (elementsPerLoop)   Mode  Cnt   Score    Error  Units
VectorizationExampleBench.doAddArraysIfEven              1024         1000000000  thrpt    5   1.468 ±  0.019  ops/s
VectorizationExampleBench.doAddArraysIfEven             16384         1000000000  thrpt    5   1.510 ±  0.006  ops/s
VectorizationExampleBench.doAddArraysIfEven             65536         1000000000  thrpt    5   1.508 ±  0.005  ops/s
VectorizationExampleBench.doAddArraysIfEven            524288         1000000000  thrpt    5   1.498 ±  0.004  ops/s
VectorizationExampleBench.doAddArraysIfEven          67108864         1000000000  thrpt    5   1.062 ±  0.095  ops/s
VectorizationExampleBench.doAddArraysIfPredicate         1024         1000000000  thrpt    5   1.494 ±  0.048  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        16384         1000000000  thrpt    5   1.565 ±  0.032  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536         1000000000  thrpt    5   1.572 ±  0.018  ops/s
VectorizationExampleBench.doAddArraysIfPredicate       524288         1000000000  thrpt    5   1.449 ±  0.024  ops/s
VectorizationExampleBench.doAddArraysIfPredicate     67108864         1000000000  thrpt    5   1.013 ±  0.083  ops/s
VectorizationExampleBench.doAddX                         1024         1000000000  thrpt    5  10.035 ±  0.026  ops/s
VectorizationExampleBench.doAddX                        16384         1000000000  thrpt    5   9.533 ±  0.080  ops/s
VectorizationExampleBench.doAddX                        65536         1000000000  thrpt    5   9.751 ±  0.074  ops/s
VectorizationExampleBench.doAddX                       524288         1000000000  thrpt    5   4.939 ±  0.067  ops/s
VectorizationExampleBench.doAddX                     67108864         1000000000  thrpt    5   2.311 ±  0.197  ops/s
VectorizationExampleBench.doSumIfEvenLoop                1024         1000000000  thrpt    5   1.044 ±  0.001  ops/s
VectorizationExampleBench.doSumIfEvenLoop               16384         1000000000  thrpt    5   1.043 ±  0.001  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536         1000000000  thrpt    5   1.041 ±  0.016  ops/s
VectorizationExampleBench.doSumIfEvenLoop              524288         1000000000  thrpt    5   1.034 ±  0.010  ops/s
VectorizationExampleBench.doSumIfEvenLoop            67108864         1000000000  thrpt    5   0.998 ±  0.017  ops/s
VectorizationExampleBench.doSumIfPredicateLoop           1024         1000000000  thrpt    5   1.615 ±  0.001  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          16384         1000000000  thrpt    5   1.666 ±  0.002  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536         1000000000  thrpt    5   1.668 ±  0.008  ops/s
VectorizationExampleBench.doSumIfPredicateLoop         524288         1000000000  thrpt    5   1.751 ±  0.060  ops/s
VectorizationExampleBench.doSumIfPredicateLoop       67108864         1000000000  thrpt    5   1.420 ±  0.087  ops/s
VectorizationExampleBench.doSumLoop                      1024         1000000000  thrpt    5   2.102 ±  0.009  ops/s
VectorizationExampleBench.doSumLoop                     16384         1000000000  thrpt    5   2.085 ±  0.002  ops/s
VectorizationExampleBench.doSumLoop                     65536         1000000000  thrpt    5   2.087 ±  0.002  ops/s
VectorizationExampleBench.doSumLoop                    524288         1000000000  thrpt    5   2.081 ±  0.007  ops/s
VectorizationExampleBench.doSumLoop                  67108864         1000000000  thrpt    5   1.714 ±  0.159  ops/s
VectorizationExampleBench.doSumShiftedLoop               1024         1000000000  thrpt    5   1.114 ±  0.029  ops/s
VectorizationExampleBench.doSumShiftedLoop              16384         1000000000  thrpt    5   1.132 ±  0.009  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536         1000000000  thrpt    5   1.134 ±  0.003  ops/s
VectorizationExampleBench.doSumShiftedLoop             524288         1000000000  thrpt    5   1.131 ±  0.001  ops/s
VectorizationExampleBench.doSumShiftedLoop           67108864         1000000000  thrpt    5   1.051 ±  0.045  ops/s
```

When running with Zing, the following jvm flags are useful for getting assembly output via Zvision:
-XX:ARTAPort=33333 -XX:+UseTickProfiler -XX:+UseLLVMBasedDisassembler

e.g.:
```
${JAVA_HOME}/bin/java -jar target/benchmarks.jar -i 10000 -jvmArgs "-XX:ARTAPort=33333 -XX:+UseTickProfiler -XX:+UseLLVMBasedDisassembler" doAddArraysIfPredicate
```

Will keep doAddArraysIfPredicate hot and allow you to drill into it's tick profile using Zvision

On a Skylake 4116 CPU @ 2.10GHz running Zing 18.06 I see (when drilling down):
```
		0x30018fdb	nopl	(%rax,%rax)	0x0f1f440000
1.75%	427	0x30018fe0	vpcmpneqb	12(%rax,%r8), %xmm0, %k1	0x62b37d083f8c000c00000004
1.58%	384	0x30018fec	vpcmpneqb	28(%rax,%r8), %xmm0, %k2	0x62b37d083f94001c00000004
2.26%	551	0x30018ff8	vpcmpneqb	44(%rax,%r8), %xmm0, %k3	0x62b37d083f9c002c00000004
1.41%	344	0x30019004	vpcmpneqb	60(%rax,%r8), %xmm0, %k4	0x62b37d083fa4003c00000004
12.05%	2,933	0x30019010	vmovdqu32	12(%rcx,%rdi), %zmm1 {%k1} {z}	0x62f17ec96f8c390c000000
3.17%	773	0x3001901b	vmovdqu32	76(%rcx,%rdi), %zmm2 {%k2} {z}	0x62f17eca6f94394c000000
5.68%	1,382	0x30019026	vmovdqu32	140(%rcx,%rdi), %zmm3 {%k3} {z}	0x62f17ecb6f9c398c000000
5.03%	1,224	0x30019031	vmovdqu32	204(%rcx,%rdi), %zmm4 {%k4} {z}	0x62f17ecc6fa439cc000000
17.25%	4,201	0x3001903c	vmovdqu32	12(%rdx,%rdi), %zmm5 {%k1} {z}	0x62f17ec96fac3a0c000000
3.98%	968	0x30019047	vpaddd	%zmm1, %zmm5, %zmm1	0x62f15548fec9
1.29%	313	0x3001904d	vmovdqu32	76(%rdx,%rdi), %zmm5 {%k2} {z}	0x62f17eca6fac3a4c000000
5.19%	1,263	0x30019058	vpaddd	%zmm2, %zmm5, %zmm2	0x62f15548fed2
2.91%	709	0x3001905e	vmovdqu32	140(%rdx,%rdi), %zmm5 {%k3} {z}	0x62f17ecb6fac3a8c000000
5.38%	1,311	0x30019069	vpaddd	%zmm3, %zmm5, %zmm3	0x62f15548fedb
1.79%	437	0x3001906f	vmovdqu32	204(%rdx,%rdi), %zmm5 {%k4} {z}	0x62f17ecc6fac3acc000000
15.50%	3,775	0x3001907a	vmovdqu32	%zmm1, 12(%rcx,%rdi) {%k1}	0x62f17e497f8c390c000000
0.64%	157	0x30019085	vmovdqu32	%zmm2, 76(%rcx,%rdi) {%k2}	0x62f17e4a7f94394c000000
0.32%	77	0x30019090	vmovdqu32	%zmm3, 140(%rcx,%rdi) {%k3}	0x62f17e4b7f9c398c000000
0.60%	147	0x3001909b	vpaddd	%zmm4, %zmm5, %zmm1	0x62f15548fecc
2.89%	704	0x300190a1	vmovdqu32	%zmm1, 204(%rcx,%rdi) {%k4}	0x62f17e4c7f8c39cc000000
8.34%	2,030	0x300190ac	addq	$64, %r8	0x4983c040
0.33%	80	0x300190b0	addq	$256, %rdi	0x4881c700010000
0.59%	143	0x300190b7	cmpq	%r8, %rsi	0x4c39c6
                0x300190ba	jne	-224 ; ABS: 0x30018fe0	0x0f8520ffffff
```

While on a Haswell E5-2670 v3 @ 2.30GHz running Zing 18.06 I see (when drilling down):
```
		0x300195d9	nopl	(%rax)	0x0f1f8000000000
0.10%	25	0x300195e0	vpmovzxbw	-24(%r8), %xmm2	0xc4c2793050e8
2.33%	587	0x300195e6	vpmovzxbw	-16(%r8), %xmm3	0xc4c2793058f0
2.42%	609	0x300195ec	vpmovzxbw	-8(%r8), %xmm4	0xc4c2793060f8
0.38%	95	0x300195f2	vpmovzxbw	(%r8), %xmm5	0xc4c2793028
0.11%	27	0x300195f7	vpcmpeqw	%xmm0, %xmm2, %xmm2	0xc5e975d0
2.03%	513	0x300195fb	vpxor	%xmm1, %xmm2, %xmm2	0xc5e9efd1
0.69%	175	0x300195ff	vpmovzxwd	%xmm2, %ymm2	0xc4e27d33d2
0.39%	98	0x30019604	vpslld	$31, %ymm2, %ymm2	0xc5ed72f21f
0.23%	58	0x30019609	vpsrad	$31, %ymm2, %ymm2	0xc5ed72e21f
1.93%	486	0x3001960e	vpcmpeqw	%xmm0, %xmm3, %xmm3	0xc5e175d8
0.62%	157	0x30019612	vpxor	%xmm1, %xmm3, %xmm3	0xc5e1efd9
0.36%	90	0x30019616	vpmovzxwd	%xmm3, %ymm3	0xc4e27d33db
0.55%	139	0x3001961b	vpslld	$31, %ymm3, %ymm3	0xc5e572f31f
2.18%	549	0x30019620	vpsrad	$31, %ymm3, %ymm3	0xc5e572e31f
1.09%	274	0x30019625	vpcmpeqw	%xmm0, %xmm4, %xmm4	0xc5d975e0
0.20%	51	0x30019629	vpxor	%xmm1, %xmm4, %xmm4	0xc5d9efe1
0.07%	17	0x3001962d	vpmovzxwd	%xmm4, %ymm4	0xc4e27d33e4
1.78%	450	0x30019632	vpslld	$31, %ymm4, %ymm4	0xc5dd72f41f
1.03%	260	0x30019637	vpsrad	$31, %ymm4, %ymm4	0xc5dd72e41f
0.68%	171	0x3001963c	vpcmpeqw	%xmm0, %xmm5, %xmm5	0xc5d175e8
0.08%	19	0x30019640	vpxor	%xmm1, %xmm5, %xmm5	0xc5d1efe9
1.71%	431	0x30019644	vpmaskmovd	12(%rcx,%rdi), %ymm2, %ymm6	0xc4e26d8c74390c
9.77%	2,464	0x3001964b	vpmovzxwd	%xmm5, %ymm5	0xc4e27d33ed
1.46%	368	0x30019650	vpmaskmovd	44(%rcx,%rdi), %ymm3, %ymm7	0xc4e2658c7c392c
2.75%	693	0x30019657	vpslld	$31, %ymm5, %ymm5	0xc5d572f51f
1.23%	310	0x3001965c	vpmaskmovd	76(%rcx,%rdi), %ymm4, %ymm8	0xc4625d8c44394c
10.57%	2,666	0x30019663	vpsrad	$31, %ymm5, %ymm5	0xc5d572e51f
1.14%	288	0x30019668	vpmaskmovd	108(%rcx,%rdi), %ymm5, %ymm9	0xc462558c4c396c
3.51%	885	0x3001966f	vpmaskmovd	12(%rdx,%rdi), %ymm2, %ymm10	0xc4626d8c543a0c
9.00%	2,268	0x30019676	vpaddd	%ymm6, %ymm10, %ymm6	0xc5adfef6
1.60%	404	0x3001967a	vpmaskmovd	44(%rdx,%rdi), %ymm3, %ymm10	0xc462658c543a2c
2.72%	686	0x30019681	vpaddd	%ymm7, %ymm10, %ymm7	0xc5adfeff
1.76%	444	0x30019685	vpmaskmovd	76(%rdx,%rdi), %ymm4, %ymm10	0xc4625d8c543a4c
10.60%	2,673	0x3001968c	vpaddd	%ymm8, %ymm10, %ymm8	0xc4412dfec0
1.91%	481	0x30019691	vpmaskmovd	108(%rdx,%rdi), %ymm5, %ymm10	0xc462558c543a6c
2.40%	604	0x30019698	vpaddd	%ymm9, %ymm10, %ymm9	0xc4412dfec9
1.92%	483	0x3001969d	vpmaskmovd	%ymm6, %ymm2, 12(%rcx,%rdi)	0xc4e26d8e74390c
4.08%	1,028	0x300196a4	vpmaskmovd	%ymm7, %ymm3, 44(%rcx,%rdi)	0xc4e2658e7c392c
3.05%	769	0x300196ab	vpmaskmovd	%ymm8, %ymm4, 76(%rcx,%rdi)	0xc4625d8e44394c
5.59%	1,410	0x300196b2	vpmaskmovd	%ymm9, %ymm5, 108(%rcx,%rdi)	0xc462558e4c396c
3.05%	768	0x300196b9	addq	$32, %r8	0x4983c020
0.61%	154	0x300196bd	subq	$-128, %rdi	0x4883ef80
0.27%	68	0x300196c1	addq	$-32, %r10	0x4983c2e0
                0x300196c5	jne	-235 ; ABS: 0x300195e0	0x0f8515ffffff
```

