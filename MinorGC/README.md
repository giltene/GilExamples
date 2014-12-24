# MinorGC
----------------------------------------------------------------------------

Written by Gil Tene of Azul Systems, and released to the public domain
as explained at http://creativecommons.org/publicdomain/zero/1.0

----------------------------------------------------------------------------

MinorGC is a simple tool that forces frequent minor GC events to occur
(by constantly allocating objects that immediately die) while also forcing
the OldGen to collect periodically. It is useful for demonstrating the range
of newgen pause times across the full lifecycle of oldgen promotion/collection.


# Example uses:

## Standalone:

To simply demonstrate the "best possible" newgen collection times [in your JVM and
on your machine, and for your selected heap size] across full oldgen lifecycles,
you can use the following command. It will show newgen pause times on an effectively
empty newgen, and with an OldGen that has no references to any newgen objects in
it [newgen collections can't be faster than that]:

    java -Xmx8g -Xms8g -Xmn64m -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails -verbose:gc -jar MinorGC.jar

To demonstrate the pause time of a newgen when a certain percentage of the
oldgen is occupied by reference (dead or alive doesn't matter, and non of them
refer to newgen objects). You can use the [-d refsFraction] option. For example,
you can see what the newgen pauses are when only 2% of the oldgen heap is made up
of reference fields use the following command:

    java -Xmx8g -Xms8g -Xmn64m -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails -verbose:gc -jar MinorGC.jar -r 0.02


----------------------------------------------------------------------------
## As a java agent:

To demonstrate the behavior of newgens in your own application, you can use
MinorGC as a java agent:

    java ... -javaagent:MinorGC.jar ...

    or with a -r argument :

    java ... -javaagent:MinorGC.jar="-r 0.02" ...

MinorGC will perform the same work as in the standalone case, but will do so with
your application running. Note that because of the high newgen pressure, running
with MinorGC as a javaagent will likely increase promotion rates significantly,
and slow down your application. This mode is simply intended to measure the behavior
of newgen GC actual pauses with your application's actual heap population, and is obviously
not a means for measuring other realistic application performance.

MinorGC comes with an Idle class (that takes a <-t millisecondsToIdle> arg), which
can be used to conveniently test the java agent setup. E.g. The following command
will run MinorGC for 60 seconds:

    java -Xmx8g -Xms8g -Xmn64m -XX:+UseConcMarkSweepGC -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDetails -verbose:gc -javaagent:MinorGC.jar="-r 0.02" Idle -t 60000


