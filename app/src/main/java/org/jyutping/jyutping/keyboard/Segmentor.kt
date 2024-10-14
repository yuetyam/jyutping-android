package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.presets.PresetString
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

/// All token text character count
fun SegmentScheme.length(): Int = this.map { it.text.length }.fold(0) { acc, i -> acc + i }

/// Longest scheme token text character count
fun Segmentation.maxSchemeLength(): Int = this.firstOrNull()?.length() ?: 0

/// All token count
private fun Segmentation.tokenCount(): Int = this.map { it.size }.fold(0) { acc, i -> acc + i }

private fun SegmentScheme.isValid(): Boolean {
        // REASON: *am => [*aa, m] => *aam
        val regex = Regex("aa(m|ng)")
        val origin = this.joinToString(separator = PresetString.EMPTY) { it.origin }
        val originNumber = regex.findAll(origin).count()
        if (originNumber < 1) return true
        val text = this.joinToString(separator = PresetString.EMPTY) { it.text }
        val tokenNumber = regex.findAll(text).count()
        if (tokenNumber < 1) return false
        return originNumber == tokenNumber
}

private fun Segmentation.descended(): Segmentation = this.sortedWith(compareBy({-it.length()}, {it.size}))

object Segmentor {
        fun segment(text: String, db: DatabaseHelper): Segmentation {
                val textLength = text.length
                return when {
                        textLength == 0 -> emptyList()
                        textLength == 1 -> when (text) {
                                "a" -> letterA
                                "o" -> letterO
                                "m" -> letterM
                                else -> emptyList()
                        }
                        textLength == 4 && text == "mama" -> mama
                        textLength == 4 && text == "mami" -> mami
                        else -> {
                                val rawText: String = text.filter { it.isLetter() }
                                val key: Int = rawText.hashCode()
                                val cached = cachedSegmentations[key]
                                if (cached != null) {
                                        cached
                                } else {
                                        val segmented = split(rawText, db)
                                        cache(key, segmented)
                                        segmented
                                }
                        }
                }
        }
        private fun split(text: String, db: DatabaseHelper): Segmentation {
                val leadingTokens = splitLeading(text, db)
                if (leadingTokens.isEmpty()) return emptyList()
                val textLength = text.length
                var segmentation = leadingTokens.map { listOf(it) }
                var previousSubelementCount = segmentation.tokenCount()
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
                        val currentSubelementCount = segmentation.tokenCount()
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
                return (maxLength downTo 1).mapNotNull { db.syllableMatch(text.take(it)) }
        }

        private val letterA: Segmentation = listOf(listOf(SegmentToken(text = "a", origin = "aa")))
        private val letterO: Segmentation = listOf(listOf(SegmentToken(text = "o", origin = "o")))
        private val letterM: Segmentation = listOf(listOf(SegmentToken(text = "m", origin = "m")))
        private val mama: Segmentation = listOf(listOf(SegmentToken(text = "ma", origin = "maa"), SegmentToken(text = "ma", origin = "maa")))
        private val mami: Segmentation = listOf(listOf(SegmentToken(text = "ma", origin = "maa"), SegmentToken(text = "mi", origin = "mi")))

        private const val MAX_CACHE_COUNT: Int = 1000
        private val cachedSegmentations: HashMap<Int, Segmentation> = hashMapOf()
        private fun cache(key: Int, segmentation: Segmentation) {
                if (cachedSegmentations.size > MAX_CACHE_COUNT) {
                        cachedSegmentations.clear()
                }
                cachedSegmentations[key] = segmentation
        }
}
