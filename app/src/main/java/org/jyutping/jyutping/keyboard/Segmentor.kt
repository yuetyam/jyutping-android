package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.min

data class SegmentToken(
        /// Token
        val text: String,
        /// Regular Jyutping Syllable
        val origin: String
)

typealias SegmentScheme = List<SegmentToken>
typealias Segmentation = List<SegmentScheme>

fun SegmentScheme.length(): Int = this.map { it.text.length }.reduce { acc, i -> acc + i }
fun Segmentation.maxLength(): Int = this.firstOrNull()?.length() ?: 0

private fun SegmentScheme.isValid(): Boolean {
        // REASON: *am => [*aa, m] => *aam
        val regex = Regex("aa(m|ng)")
        val origin = this.joinToString(separator = String.empty) { it.origin }
        val originNumber = regex.findAll(origin).count()
        if (originNumber < 1) return true
        val text = this.joinToString(separator = String.empty) { it.text }
        val tokenNumber = regex.findAll(text).count()
        if (tokenNumber < 1) return false
        return originNumber == tokenNumber
}
private fun Segmentation.descended(): Segmentation = run {
        val comparator = Comparator<SegmentScheme> { lhs, rhs ->
                val lengthComparison = lhs.length().compareTo(rhs.length())
                if (lengthComparison != 0) return@Comparator -lengthComparison
                val elementSizeComparison = lhs.size.compareTo(rhs.size)
                return@Comparator elementSizeComparison
        }
        return this.sortedWith(comparator)
}

object Segmentor {
        fun segment(text: String, db: DatabaseHelper): Segmentation {
                return when (text.length) {
                        0 -> emptyList()
                        1 -> when (text) {
                                "a" -> letterA
                                "o" -> letterO
                                "m" -> letterM
                                else -> emptyList()
                        }
                        4 -> when (text) {
                                "mama" -> mama
                                "mami" -> mami
                                else -> split(text, db)
                        }
                        else -> split(text, db)
                }
        }
        private fun split(text: String, db: DatabaseHelper): Segmentation {
                val leadingTokens = splitLeading(text, db)
                if (leadingTokens.isEmpty()) return emptyList()
                val textLength = text.length
                var segmentation = leadingTokens.map { listOf(it) }
                var previousSubelementCount = segmentation.map { it.length() }.reduce { acc, i -> acc + i }
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
                        val currentSubelementCount = segmentation.map { it.length() }.reduce { acc, i -> acc + i }
                        if (currentSubelementCount != previousSubelementCount) {
                                previousSubelementCount = currentSubelementCount
                        } else {
                                shouldContinue = false
                        }
                }
                return segmentation.filter { it.isValid() }.descended()
        }
        private fun splitLeading(text: String, db: DatabaseHelper): SegmentScheme {
                val maxLength = min(text.length, 6)
                if (maxLength < 1) return emptyList()
                return (maxLength downTo 1).mapNotNull { db.matchSyllable(text.take(it)) }
        }

        private val letterA: Segmentation = listOf(listOf(SegmentToken(text = "a", origin = "aa")))
        private val letterO: Segmentation = listOf(listOf(SegmentToken(text = "o", origin = "o")))
        private val letterM: Segmentation = listOf(listOf(SegmentToken(text = "m", origin = "m")))
        private val mama: Segmentation = listOf(listOf(SegmentToken(text = "ma", origin = "maa"), SegmentToken(text = "ma", origin = "maa")))
        private val mami: Segmentation = listOf(listOf(SegmentToken(text = "ma", origin = "maa"), SegmentToken(text = "mi", origin = "mi")))
}
