package org.jyutping.jyutping.extensions

import java.util.PriorityQueue

fun <T : Comparable<T>> List<T>.top(n: Int = 1): List<T> {
        if (n < 1 || isEmpty()) return emptyList()
        if (size <= n) return this.sorted()
        val heap = PriorityQueue<T>(n, Comparator.reverseOrder())
        for (item in this) {
                if (heap.size < n) {
                        heap.add(item)
                } else if (item < heap.peek()!!) {
                        heap.poll()
                        heap.add(item)
                }
        }
        val result = ArrayList<T>(heap.size)
        while (heap.isNotEmpty()) result.add(heap.poll()!!)
        result.reverse()
        return result
}

inline fun <T> List<T>.topBy(n: Int = 1, crossinline selector: (T) -> Int): List<T> {
        if (n < 1 || isEmpty()) return emptyList()
        if (size <= n) return this.sortedBy(selector)
        val cmp = Comparator<T> { a, b -> selector(b).compareTo(selector(a)) }
        val heap = PriorityQueue(n, cmp)
        for (item in this) {
                if (heap.size < n) {
                        heap.add(item)
                } else if (selector(item) < selector(heap.peek()!!)) {
                        heap.poll()
                        heap.add(item)
                }
        }
        val result = ArrayList<T>(heap.size)
        while (heap.isNotEmpty()) result.add(heap.poll()!!)
        result.reverse()
        return result
}
