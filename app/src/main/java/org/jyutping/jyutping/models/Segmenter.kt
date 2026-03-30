package org.jyutping.jyutping.models

import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.min

typealias Segmentation = List<Scheme>

fun Segmentation.descended(): Segmentation = this.sortedWith(compareBy({it.schemeLength.unaryMinus()}, {it.size}))

object Segmenter {
        private fun splitLeading(keys: List<VirtualInputKey>, db: DatabaseHelper): List<Syllable> {
                val maxLength = min(keys.size, 6)
                if (maxLength < 1) return emptyList()
                return (maxLength downTo 1).mapNotNull { db.syllableMatch(keys.take(it).combinedCode()) }
        }
        private fun split(keys: List<VirtualInputKey>, db: DatabaseHelper): Segmentation {
                val headSyllables = splitLeading(keys, db)
                if (headSyllables.isEmpty()) return emptyList()
                val inputLength = keys.size
                val segmentation: HashSet<Scheme> = headSyllables.map { listOf(it) }.toHashSet()
                var previousSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i}
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation.toList()) {
                                val schemeLength = scheme.schemeLength
                                if (schemeLength >= inputLength) continue
                                val tailKeys = keys.drop(schemeLength)
                                val tailSyllables = splitLeading(tailKeys, db)
                                if (tailSyllables.isEmpty()) continue
                                val newSegmentation = tailSyllables.map { scheme + it }
                                segmentation += newSegmentation
                        }
                        val currentSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i}
                        if (currentSyllableCount != previousSyllableCount) {
                                previousSyllableCount = currentSyllableCount
                        } else {
                                shouldContinue = false
                        }
                }
                return segmentation.filter { it.isValid() }.descended()
        }
        fun segment(keys: List<VirtualInputKey>, db: DatabaseHelper): Segmentation {
                return when (keys.size) {
                        0 -> emptyList()
                        1 -> when (keys.first()) {
                                VirtualInputKey.letterA -> letterA
                                VirtualInputKey.letterO -> letterO
                                VirtualInputKey.letterM -> letterM
                                else -> emptyList()
                        }
                        4 -> when (keys.combinedCode()) {
                                32203220L -> mama
                                32203228L -> mami
                                else -> {
                                        val syllableKeys = keys.filter { it.isSyllableLetter }
                                        val code = syllableKeys.combinedCode()
                                        cachedSegmentations[code]
                                        val segmented = split(syllableKeys, db)
                                        if (code > 0L) {
                                                cache(code, segmented)
                                        }
                                        segmented
                                }
                        }
                        else -> {
                                val syllableKeys = keys.filter { it.isSyllableLetter }
                                val code = syllableKeys.combinedCode()
                                cachedSegmentations[code]
                                val segmented = split(syllableKeys, db)
                                if (code > 0L) {
                                        cache(code, segmented)
                                }
                                segmented
                        }
                }
        }

        private const val MAX_CACHE_COUNT: Int = 500
        private val cachedSegmentations: HashMap<Long, Segmentation> = hashMapOf()
        private fun cache(code: Long, segmentation: Segmentation) {
                if (cachedSegmentations.size > MAX_CACHE_COUNT) {
                        cachedSegmentations.clear()
                }
                cachedSegmentations[code] = segmentation
        }

        private val letterA: Segmentation = listOf(listOf(Syllable(aliasCode = 20, originCode = 2020)))
        private val letterO: Segmentation = listOf(listOf(Syllable(aliasCode = 34, originCode = 34)))
        private val letterM: Segmentation = listOf(listOf(Syllable(aliasCode = 32, originCode = 32)))
        private val mama: Segmentation = listOf(
                listOf(
                        Syllable(aliasCode = 3220, originCode = 322020),
                        Syllable(aliasCode = 3220, originCode = 322020)
                )
        )
        private val mami: Segmentation = listOf(
                listOf(
                        Syllable(aliasCode = 3220, originCode = 322020),
                        Syllable(aliasCode = 3228, originCode = 3228)
                )
        )
}

private fun DatabaseHelper.syllableMatch(code: Long): Syllable? {
        val command = "SELECT origin_code FROM syllable_table WHERE alias_code = $code LIMIT 1;"
        val cursor = this.readableDatabase.rawQuery(command, null)
        if (cursor.moveToFirst()) {
                val originCode = cursor.getLong(0)
                val syllable = Syllable(aliasCode = code, originCode = originCode)
                cursor.close()
                return syllable
        } else {
                cursor.close()
                return null
        }
}

fun Segmenter.syllableText(keys: List<VirtualInputKey>, db: DatabaseHelper): String? {
        if (keys.size > 6) return null
        return db.syllableMatch(code = keys.combinedCode())?.originText
}
