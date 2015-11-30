#JEP XYZ: Spin Wait hinting

(suggested content for some JEP fields):

| field         | suggested contents |
| ------------- | ------------- |
| Authors       | Gil Tene      |  
| Owner         | Gil Tene      |
| Type	        | Feature       | 
| Status        | Draft         |
| Component     | core-libs     |
| Scope         | JDK           |
| Discussion    | core dash libs dash dev at openjdk dot java dot net | 
| Effort        | S             |
| Duration	    | S             |


##Summary

Add an API that would allow Java code to indicate that a spin wait loop is being executed.

##Goals

Provide an API that would allow Java code to indicate to the runtime that it is in a spin wait
loop. The API would act as a pure hint, and will carry no semantic behavior requirements (i.e.
a no-op is a valid implementation). Allow the JVM to benefit from spin wait loop specific
behaviors that may be useful on certain hardware platforms. Provide both a no-op implementation
and an intrinsic implementation in the JDK, and demonstrate an execution benefit on at least one
major hardware platform.

##Non-Goals

It is NOT a goal to look at performance hints beyond spin wait loops. Other performance hints,
such as prefetch hints, are outside the scope of this JEP.

##Motivation

Some hardware platforms benefit from software indication that a spin wait loop is in progress.
Some common execution benefits may be observed:

A) The reaction time of a spin wait loop construct may be improved when a spin wait hinting
is used due to various factors, reducing thread-to-thread latencies in spinning wait situations.

and

B) The power consumed by the core or hardware thread involved in the spin wait loop construct
may be reduced, benefitting overall power consumption of a program, and possibly allowing other
cores or hardware threads to execute at faster speeds within the same power consumption envelope. 

While long term spinning is often discouraged as a general user-mode programming practice,
short term spinning prior to blocking is a common practice (both inside and outside of the JDK).
Furthermore, as core-rich computing platforms are commonly available, many performance and/or
latency sensitive applications use a pattern that dedicates a spinning thread to a latency
critical function [1], and may involve long term spinning as well.  

As a practical example and use case, current x86 processors support a PAUSE instruction that
can be used to indicate spinning behavior. Using a PAUSE instruction demonstrably reduces
thread-to-thread round trips. Due to it's benefits and commonly recommended use, the x86 PAUSE
instruction is commonly used in kernel spinlocks, in POSIX libraries that perform heuristic
spins prior to blocking, and even by the JVM itself. However, due to the inability to hint
or indicate that a Java loop is spinning, it's benefits are not available to regular Java code.

We include specific supporting evidence: In simple tests [2] performed on a E5-2697 v2,
measuring the round trip latency behavior between two threads that communicate by spinning
on a volatile field, round-trip latencies were demonstrably reduced by 18-20nsec across a
wide percentile spectrum (from the 10%'ile to the 99.9%'ile). This reduction can represent
an improvement as high as 35%-50% in best-case thread-to-thread communication latency.
E.g. when two spinning threads execute on two hardware threads that share a physical CPU
core and an L1 data cache. See example latency measurement results comparing the reaction
latency of a spin loop that includes an intrinsified onSpinWait() call (intrinsified as
a PAUSE instruction) to the same loop executed without using a PAUSE instruction [3], along
with the measurements of the it takes to perform an actual System.nantoTime() call to
measure time.

![example results]

##Description

We propose to add a method to the JDK which would indicate to the runtime that a
spin loop is being performed: e.g. Runtime.onSpinWait(). We intended this method
to become a Java SE API. The specific name space location, class name, and method
name will be determined as part of development of this JEP.

An empty method would be a valid implementation of the Runtime.onSpinWait() method,
but intrisic implementation is the obvious goal for hardware platforms that can benefit
from it. We intend to produce an intrinsic x86 implementation for OpenJDK as part
of developing this JEP. A prototype implementation already exists [4] [5] [6] [7] and
results from initial testing show promise.

##Alternatives

JNI can be used to spin loop with a spin-loop-hinting CPU instruction, but the
JNI-boundary crossing overhead tends to be larger than the benefit provided by
the instruction, at least where latency is concerned. 

We could attempt to have the JIT compilers deduce spin-wait-loop situations and
code and choose to automatically include a spin-loop-hinting CPU instructions
with no Java code hints required. We expect that the complexity of automatically and
reliably detecting spinning situations, coupled with questions about potential
tradeoffs in using the hints on some platform to delay the availability of viable
implementations significantly.

##Testing

Testing of a "vanilla" no-op implementation will obviously be fairly simple. 

We believe that given the vey small footprint of this API, testing of an
intrinsified x86 implementation in OpenJDK will also be straightforward. We expect
testing to focus on confirming both the code generation correctness and latency
benefits of using an intrinsic implementation of Runtime.onSpinWait().

Should this API be proposed as a Java SE API (e.g. for inclusion in the
java.* namespace in a future Java SE 9 or Java SE 10), we expect to develop an
associated TCK tests for the API for potential inclusion in the Java SE TCK. 

##Risks and Assumptions

The "vanilla" no-op implementation is obviously fairly low risk. An intrinsic x86
implementation will involve modifications to multiple JVM components and as such
they carry some risks, but no more than other simple intrinsics added to the JDK.

[1] The LMAX Disruptor [https://lmax-exchange.github.io/disruptor/]  
[2] [https://github.com/giltene/GilExamples/tree/master/SpinWaitTest]    
[3] Chart depicting onSpinWait() intrinsification impact [https://github.com/giltene/GilExamples/blob/master/SpinWaitTest/SpinLoopLatency_E5-2697v2_sharedCore.png]    
[4] HotSpot WebRevs for prototype implementation which intrinsifies org.performancehints.Runtime.onSpinWait() [http://ivankrylov.github.io/onspinwait/9b94.hs.webrev/]    
[5] JDK WebRevs for prototype intrinsifying implementation: [http://ivankrylov.github.io/onspinwait/9b94.jdk.webrev/]    
[6] Link to a working Linux protoype OpenJDK9-based JDK (accepts optional -XX:++UseOnSpinWaitIntrinsic) [https://goo.gl/v3G30r]    
[6] Link to a working Mac OS protoype OpenJDK9-based JDK (accepts optional -XX:++UseOnSpinWaitIntrinsic) [https://goo.gl/LTlyRd]    

[example results]:https://raw.github.com/giltene/GilExamples/master/SpinWaitTest/SpinLoopLatency_E5-2697v2_sharedCore.png "Example Results on E5-2697v2"
