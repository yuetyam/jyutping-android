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
                var previousSubelementCount = segmentation.syllableCount()
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation) {
                                val schemeLength = scheme.length()
                                if (schemeLength >= textLength) continue
                                val tailText = text.drop(schemeLength)
                                val tailTokens = splitLeading(tailText, db)
                                if (tailTokens.isEmpty()) continue
                                val newSegmentation = tailTokens.map { scheme + listOf(it) }
                                segmentation += newSegmentation
                        }
                        segmentation = segmentation.distinct()
                        val currentSubelementCount = segmentation.syllableCount()
                        if (currentSubelementCount != previousSubelementCount) {
                                previousSubelementCount = currentSubelementCount
                        } else {
                                shouldContinue = false
                        }
                }
                return segmentation.sortedWith(compareBy(
                        { -(it.length()) },
                        { -(it.size) }
                ))
        }
        private fun splitLeading(text: String, db: DatabaseHelper): List<String> {
                val maxLength = min(text.length, 6)
                if (maxLength < 1) return emptyList()
                val syllables = (maxLength downTo 1).mapNotNull { db.pinyinSyllableMatch(text.take(it)) }
                return syllables
        }
}

private fun List<String>.length(): Int = this.map { it.length }.fold(0) { acc, i -> acc + i }
private fun List<List<String>>.syllableCount(): Int = this.map { it.size }.fold(0) { acc, i -> acc + i }
