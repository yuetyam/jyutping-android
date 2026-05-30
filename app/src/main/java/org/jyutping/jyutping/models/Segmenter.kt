package org.jyutping.jyutping.models

import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.min

typealias Segmentation = List<Scheme>

fun Segmentation.descended(): Segmentation = this.sortedWith(compareBy({it.schemeLength.unaryMinus()}, {it.size}))

object Segmenter {
        fun prepare(db: DatabaseHelper) {
                val command = "SELECT alias_code, origin_code FROM core_syllable_table;"
                val cursor = db.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val aliasCode = cursor.getLong(0)
                        val originCode = cursor.getLong(1)
                        syllableCodeMap[aliasCode] = Syllable(aliasCode = aliasCode, originCode = originCode)
                }
                cursor.close()
        }
        fun needsPreparation(): Boolean = syllableCodeMap.isEmpty()
        private val syllableCodeMap: HashMap<Long, Syllable> = hashMapOf()
        private fun lookup(code: Long): Syllable? = syllableCodeMap[code]
        private fun splitLeading(keys: List<VirtualInputKey>): List<Syllable> {
                val maxLength = min(keys.size, 6)
                if (maxLength < 1) return emptyList()
                return (maxLength downTo 1).mapNotNull { lookup(code = keys.take(it).combinedCode()) }
        }
        private fun split(keys: List<VirtualInputKey>): Segmentation {
                val headSyllables = splitLeading(keys)
                if (headSyllables.isEmpty()) return emptyList()
                val inputLength = keys.size
                val segmentation: HashSet<Scheme> = headSyllables.map { listOf(it) }.toHashSet()
                var previousSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i }
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation.toList()) {
                                val schemeLength = scheme.schemeLength
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
                return segmentation.filter { it.isValid() }.descended()
        }
        fun segment(keys: List<VirtualInputKey>): Segmentation {
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
                                        val segmented = split(syllableKeys)
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
                                val segmented = split(syllableKeys)
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

        fun syllableText(keys: List<VirtualInputKey>): String? {
                if (keys.size > 6) return null
                return lookup(code = keys.combinedCode())?.originText
        }
}
