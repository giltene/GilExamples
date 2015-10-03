#SpinHintTest

A simple thread-to-thread communication latency test that measures and reports on the
behavior of thread-to-thread ping-pong latencies when spinning using a shared volatile
field, aling with the impact of using a spinLoo[Hint() call on that latency behavior.

This test can be used to measure and document the impact of spinLoopHint behavior
on thread-to-thread communication latencies. E.g. when the two threads are pinned to
the two hardware threads of a shared x86 core (with a shared L1), this test will
demonstrate an estimate the best case thread-to-thread latencies possible on the
platform, if the latency of measuring time with System.nanoTime() is discounted
(nanoTime latency can be separtely estimated across the percentile spectrum using
the NanoTimeLatency test in this package).

For consistent measurement, it is recommedned that this test be executed while
binding the process to specific cores. E.g. on a Linux system, the following
command can be used:

    % taskset -c 23, 47 java -jar SpinLoopHint.jar
(the choice of cores 23 and 47 is specific to a 48 vcore system where cores
 23 and 47 represent two hyper-threads on a common core).
 
###Example results (two threads on a shared core on a Xeon E5-2697v2): 
![example results] 
 
[example results]:https://raw.github.com/giltene/GilExamples/master/SpinHintTest/SpinLoopLatency_E5-2697v2_sharedCore.png "Example Results on E5-2697v2"
 
