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

###Example results plot (two threads on a shared core on a Xeon E5-2697v2): 
![example results] 

###Running:

This test is obviously intended to be run on machines with 2 or more vcores (tests on single vcore machines will
produce understandably outrageously long runtimes). 

The simplest way to run this test is:

    % ${JAVA_HOME}/bin/java -jar SpinLoopHint.jar

Since the test is intended to highlight the benefits of an intrinsic spinLoopHint(), using a prototype JDK
that that intrinsifies org.performancehintsSpinHint.spinLoopHint() as a PAUSE instruction
(see links below), is obviously recommended. Using such a JDK, you can compare the output of:

    % ${JAVA_HOME}/bin/java -XX:-UseSpinLoopHintIntrinsic -jar SpinLoopHint.jar > intrinsicSpinHint.hgrm

and 
    
    % ${JAVA_HOME}/bin/java -XX:+UseSpinLoopHintIntrinsic -jar SpinLoopHint.jar > vanilla.hgrm

By plotting them both with [HdrHistogram's online percentile plotter] (http://hdrhistogram.github.io/HdrHistogram/plotFiles.html)

On moden x86-64 sockets, comparisions seem to show an 18-20nsec difference in the round trip latency.  

For consistent measurement, it is recommended that this test be executed while
binding the process to specific cores. E.g. on a Linux system, the following
command can be used:

    % taskset -c 23, 47 ${JAVA_HOME}/bin/java -XX:+UseSpinLoopHintIntrinsic -jar SpinLoopHint.jar
    
To place the spinning threads on the same core. (the choice of cores 23 and 47 is specific
to a 48 vcore system where cores 23 and 47 represent two hyper-threads on a common core. You will want
to identofy a matching pair on your specific system).
 
###Plotting results:
 
SpinLoopHint outputs a percentile histogram distribution in [HdrHistogram](http://hdrhistogram.org)'s common
.hgrm format. This output can/shuld be redirected to an .hgrm file (e.g. vanilla.hgrm),
which can then be directly plotted using tools like [HdrHistogram's online percentile plotter] (http://hdrhistogram.github.io/HdrHistogram/plotFiles.html)

 
###Prototype intrinsifying implementations

A prototype OpenJDK implementation that implements org.performancehintsSpinHint.spinLoopHint() as a PAUSE instruction
on x86-64 is available. Relevant Webrevs can be found here:  
- HotSpot: [http://ivankrylov.github.io/spinloophint/webrev/]  
- JDK: [http://ivankrylov.github.io/spinloophint/webrev.jdk/]  
- Build environment: [http://ivankrylov.github.io/spinloophint/webrev.main/]  
      
A downloadable working OpenJDK9-based JDK (which accepts an optional -XX:+UseSpinLoopHintIntrinsic flag to turn the
feature on) can be found here:   
- Linux: [https://www.dropbox.com/s/r2w1s1jykr2qs01/slh-openjdk-9-b70-bin-linux-x64.tar.gz?dl=0]  
- Mac: [https://www.dropbox.com/s/h11zcyjhyq2q358/slh-openjdk-9-b70-bin-mac-x64.tar.gz?dl=0]  
- Windows: [https://www.dropbox.com/s/j6p1y4sixc84xzu/slh-openjdk-9-b70-bin-win64-x64.tar.gz?dl=0]  

[example results]:https://raw.github.com/giltene/GilExamples/master/SpinHintTest/SpinLoopLatency_E5-2697v2_sharedCore.png "Example Results on E5-2697v2"
