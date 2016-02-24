/*
 * Written by Gil Tene, and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/
 */
/**
 * This package captures possible performance hints that may be used by some
 * runtimes to improve code performance. It is intended to provide a portable
 * means for using performance hinting APIs across Java and JDK versions,
 * such that calling code can avoid maintaining version-specific sources
 * for various JDK or Java version capabilities.
 *
 * All the features supported by this package are (by definition) hints.
 * A no-op implementation of a hint is always considered valid.
 *
 * When executing on Java versions for which corresponding capabilities exist
 * and are specified in the Java version spec, the hint immplementations in this
 * package will attempt to use the appropriate JDK calls.
 *
 * Some JDKs may choose to "intrinsify" some APIs in this package to e.g.
 * provide runtime support for certain hints on java versions that do not
 * yet have specified support for those capabilities.
 *
 * A good example of the purpose of this package and an example of how it may
 * be used can be found with
 * {@link org.performancehints.ThreadHints#onSpinWait ThreadHintsonSpinWait()}:
 * It is anticipated that Java SE 9 may include a <code>Thread.onSpinWait()</code>
 * method with specified behavior identical to
 * {@link org.performancehints.ThreadHints#onSpinWait ThreadHintsonSpinWait()}.
 * However, earlier Java SE versions do not include this behavior, forcing code
 * that wants to make use of this hinting capability to be written and
 * maintained separately when targettting Java versions before and after Java SE 9.
 *
 * The implementation
 * of {@link org.performancehints.ThreadHints#onSpinWait ThreadHintsonSpinWait()}
 * resolves this problem by calling <code>Thread.onSpinWait()</code> if it exists,
 * and doing nothing if it does not. This allows code that wants to include spin wait
 * hinting to portably work across Java versions, without needing separate
 * implementations for versions prior to and following Java SE 9. The machanism
 * used to conditionally make this call was specifically designed and tested for
 * efficient inlining by common JVMs, such that there is extra no overhead
 * associated with making the hint call.
 *
 * In addition, JDKs that wish to introduce support for newer hinting capabilities
 * in their implementations of older Java SE versions can do so by "inrinsifying"
 * asscoaied org.performancehints classes and methods. Code that makes use of
 * org.performancehints hinting methods will then benefit from potential
 * performance improvements even on prior java SE versions. E.g. the
 * example onSpinWait capability discussed above can be added in such a way to
 * Java SE 6, 7, 8 by JDKs who wish to do so.
 *
 */
