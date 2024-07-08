package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.min

object PinyinSegmentor {
        fun segment(text: String, db: DatabaseHelper): List<List<String>> = when (text.length) {
                0 -> emptyList()
                1 -> if (text == "a" || text == "o" || text == "e") listOf(listOf(text)) else emptyList()
                else -> split(text, db)
        }
        private fun split(text: String, db: DatabaseHelper): List<List<String>> {
                val leadingTokens = splitLeading(text, db)
                if (leadingTokens.isEmpty()) return emptyList()
                val textLength = text.length
                var segmentation = leadingTokens.map { listOf(it) }
                var previousSubelementCount = segmentation.map { scheme -> scheme.map { it.length } }.flatten().reduce { acc, i -> acc + i }
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation) {
                                val schemeLength = scheme.map { it.length }.reduce { acc, i -> acc + i }
                                if (schemeLength >= textLength) continue
                                val tailText = text.drop(schemeLength)
                                val tailTokens = splitLeading(tailText, db)
                                if (tailTokens.isEmpty()) continue
                                val newSegmentation = tailTokens.map { scheme + listOf(it) }
                                segmentation += newSegmentation
                        }
                        segmentation = segmentation.distinct()
                        val currentSubelementCount = segmentation.map { scheme -> scheme.map { it.length } }.flatten().reduce { acc, i -> acc + i }
                        if (currentSubelementCount != previousSubelementCount) {
                                previousSubelementCount = currentSubelementCount
                        } else {
                                shouldContinue = false
                        }
                }
                val sortedSegmentation: List<List<String>> = run {
                        val comparator = Comparator<List<String>> { lhs, rhs ->
                                val lhsLength = lhs.map { it.length }.reduce { acc, i -> acc + i }
                                val rhsLength = rhs.map { it.length }.reduce { acc, i -> acc + i }
                                if (lhsLength == rhsLength) {
                                        val sizeCompare = lhs.size.compareTo(rhs.size)
                                        return@Comparator -sizeCompare
                                } else {
                                        val lengthCompare = lhsLength.compareTo(rhsLength)
                                        return@Comparator -lengthCompare
                                }
                        }
                        segmentation.sortedWith(comparator)
                }
                return sortedSegmentation
        }
        private fun splitLeading(text: String, db: DatabaseHelper): List<String> {
                val maxLength = min(text.length, 6)
                if (maxLength < 1) return emptyList()
                val syllables = (maxLength downTo 1).mapNotNull { db.pinyinSyllableMatch(text.take(it)) }
                return syllables
        }
}
