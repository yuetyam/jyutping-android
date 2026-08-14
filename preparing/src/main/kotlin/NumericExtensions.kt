package org.jyutping.preparing

/**
 * Combines the elements as base-100 digits using wrapping arithmetic.
 *
 * For example, `[20, 21, 22]` produces `202122L`.
 * An empty iterable produces `0L`.
 *
 * If the result exceeds the range of [Long], the arithmetic wraps around according to Kotlin's two's-complement integer overflow semantics.
 *
 * @return The base-100 representation of the elements.
 */
fun Iterable<Int>.radix100Overflowed(): Long = fold(0L) { acc, i -> acc * 100L + i }


/**
 * Combines the elements as decimal digits using wrapping arithmetic.
 *
 * For example, `[2, 3, 4]` produces `234L`.
 * An empty iterable produces `0L`.
 *
 * If the result exceeds the range of [Long], the arithmetic wraps around according to Kotlin's two's-complement integer overflow semantics.
 *
 * @return The decimal representation of the elements.
 */
fun Iterable<Int>.decimalOverflowed(): Long = fold(0L) { acc, i -> acc * 10L + i }
