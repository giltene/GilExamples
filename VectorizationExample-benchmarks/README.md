This is a small set of microbennchmark that can be used demonstrate cool AVX2 and AVX512 vectorization capabilities

To execute:
```
mvn clean package
${JAVA_HOME}/bin/java -jar target/benchmarks.jar
```

When run on a Haswell E5-2670 v3 @ 2.30GHz , I observe the following results:

With Zing 18.06:
```
Benchmark                                         (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven             65536        10000  thrpt    5   3.917 ± 0.025  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536        10000  thrpt    5   3.313 ± 0.158  ops/s
VectorizationExampleBench.doAddX                        65536        10000  thrpt    5   8.586 ± 0.003  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536        10000  thrpt    5  13.674 ± 0.015  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536        10000  thrpt    5   4.212 ± 0.001  ops/s
VectorizationExampleBench.doSumLoop                     65536        10000  thrpt    5  18.733 ± 0.598  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536        10000  thrpt    5  12.249 ± 0.020  ops/s
```

With OpenJDK 8u162:
```
Benchmark                                         (arraySize)  (loopCount)   Mode  Cnt  Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven             65536        10000  thrpt    5  1.917 ± 0.004  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536        10000  thrpt    5  2.206 ± 0.027  ops/s
VectorizationExampleBench.doAddX                        65536        10000  thrpt    5  8.538 ± 0.322  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536        10000  thrpt    5  1.290 ± 0.001  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536        10000  thrpt    5  2.855 ± 1.153  ops/s
VectorizationExampleBench.doSumLoop                     65536        10000  thrpt    5  3.492 ± 0.022  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536        10000  thrpt    5  1.621 ± 0.071  ops/s
```

When run on a Skylake 4116 CPU @ 2.10GHz, I observe the following results:
With Zing 18.06:

```
Benchmark                                         (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven             65536        10000  thrpt    5   8.591 ± 0.169  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536        10000  thrpt    5   7.753 ± 0.014  ops/s
VectorizationExampleBench.doAddX                        65536        10000  thrpt    5  14.120 ± 0.113  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536        10000  thrpt    5  20.057 ± 0.103  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536        10000  thrpt    5  16.016 ± 0.123  ops/s
VectorizationExampleBench.doSumLoop                     65536        10000  thrpt    5  20.462 ± 0.231  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536        10000  thrpt    5  20.060 ± 0.084  ops/s
```

With OpenJDK 8u162:
```
Benchmark                                         (arraySize)  (loopCount)   Mode  Cnt   Score   Error  Units
VectorizationExampleBench.doAddArraysIfEven             65536        10000  thrpt    5   2.292 ± 0.008  ops/s
VectorizationExampleBench.doAddArraysIfPredicate        65536        10000  thrpt    5   2.485 ± 0.025  ops/s
VectorizationExampleBench.doAddX                        65536        10000  thrpt    5  14.727 ± 0.448  ops/s
VectorizationExampleBench.doSumIfEvenLoop               65536        10000  thrpt    5   1.590 ± 0.005  ops/s
VectorizationExampleBench.doSumIfPredicateLoop          65536        10000  thrpt    5   2.528 ± 0.156  ops/s
VectorizationExampleBench.doSumLoop                     65536        10000  thrpt    5   3.181 ± 0.013  ops/s
VectorizationExampleBench.doSumShiftedLoop              65536        10000  thrpt    5   1.731 ± 0.012  ops/s
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

