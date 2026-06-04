package org.jyutping.jyutping.models

import org.jyutping.jyutping.Elephant
import kotlin.math.min

data class PinyinSyllable(
        val code: Long,
        val keys: List<VirtualInputKey>,
        val text: String
): Comparable<PinyinSyllable> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinSyllable) return false
                return code == other.code
        }
        override fun hashCode(): Int = code.hashCode()
        override fun compareTo(other: PinyinSyllable): Int = (code / other.code).toInt()
}

typealias PinyinScheme = List<PinyinSyllable>
typealias PinyinSegmentation = List<PinyinScheme>

val PinyinScheme.pinyinSchemeLength: Int
        get() = this.map { it.keys.size }.fold(0) { acc, i -> acc + i }

object PinyinSegmenter {
        fun segment(keys: List<VirtualInputKey>): PinyinSegmentation {
                if (keys.isEmpty()) return emptyList()
                val headSyllables = splitLeading(keys)
                if (headSyllables.isEmpty()) return emptyList()
                val inputLength = keys.size
                val segmentation: HashSet<PinyinScheme> = headSyllables.map { listOf(it) }.toHashSet()
                var previousSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i }
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation.toList()) {
                                val schemeLength = scheme.pinyinSchemeLength
                                if (schemeLength >= inputLength) continue
                                val tailKeys = keys.drop(schemeLength)
                                val tailSyllables = splitLeading(tailKeys)
                                if (tailSyllables.isEmpty()) continue
                                val newSegmentation = tailSyllables.map { scheme + it }
                                segmentation += newSegmentation
                        }
                        val currentSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i }
                        if (currentSyllableCount != previousSyllableCount) {
                                previousSyllableCount = currentSyllableCount
                        } else {
                                shouldContinue = false
                        }
                }
                return segmentation.sortedWith(
                        compareBy(
                        { -(it.pinyinSchemeLength) },
                        { -(it.size) }
                ))
        }

        private fun splitLeading(keys: List<VirtualInputKey>): List<PinyinSyllable> {
                val maxLength = min(keys.size, 6)
                if (maxLength < 1) return emptyList()
                return (maxLength downTo 1).mapNotNull { number ->
                        val leadingKeys = keys.take(number)
                        val code = leadingKeys.combinedCode()
                        val text = pinyinSyllableMatch(code = code)
                        if (text == null) null else PinyinSyllable(code = code, keys = leadingKeys, text = text)
                }
        }

        private fun pinyinSyllableMatch(code: Long): String? {
                val command = "SELECT syllable FROM pinyin_syllable_table WHERE code = $code LIMIT 1;"
                var syllable: String? = null
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                                syllable = cursor.getString(0)
                        }
                }
                return syllable
        }
}
