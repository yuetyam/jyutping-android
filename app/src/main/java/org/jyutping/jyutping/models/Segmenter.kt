package org.jyutping.jyutping.models

import org.jyutping.jyutping.Elephant
import kotlin.math.min

typealias Segmentation = List<Scheme>

fun Segmentation.descended(): Segmentation = this.sortedWith(compareBy({ it.schemeLength.unaryMinus() }, { it.size }))

object Segmenter {
        fun prepare() {
                val command = "SELECT alias_code, origin_code FROM core_syllable_table;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val aliasCode = cursor.getLong(0)
                                val originCode = cursor.getLong(1)
                                syllableCodeMap[aliasCode] = Syllable(aliasCode = aliasCode, originCode = originCode)
                        }
                }
        }
        fun needsPreparation(): Boolean = syllableCodeMap.isEmpty()
        private val syllableCodeMap: HashMap<Long, Syllable> = hashMapOf()
        private fun lookup(code: Long): Syllable? = syllableCodeMap[code]

        private const val MAX_SYLLABLE_LENGTH: Int = 6
        private fun split(keys: List<VirtualInputKey>): Segmentation {
                val inputLength = keys.size
                if (inputLength == 0) return emptyList()
                val segmentations = Array(inputLength + 1) { mutableListOf<Scheme>() }
                val segmentation = mutableListOf<Scheme>()
                segmentations[0].add(emptyList())
                for (startIndex in 0.rangeUntil(inputLength)) {
                        val leadingSchemes = segmentations[startIndex]
                        if (leadingSchemes.isEmpty()) continue
                        var code = 0L
                        val endLimit = min(inputLength, startIndex + MAX_SYLLABLE_LENGTH)
                        for (endIndex in startIndex.rangeUntil(endLimit)) {
                                code = code * 100L + keys[endIndex].code.toLong()
                                val syllable = lookup(code = code) ?: continue
                                val targetSchemes = segmentations[endIndex + 1]
                                for (scheme in leadingSchemes) {
                                        val newScheme = scheme + syllable
                                        targetSchemes.add(newScheme)
                                        segmentation.add(newScheme)
                                }
                        }
                }
                return segmentation.filter { it.isValid() }.descended()
        }

        fun segment(keys: List<VirtualInputKey>): Segmentation = when (keys.size) {
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
                                cachedSegmentations[code]?.let { return it }
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
                        cachedSegmentations[code]?.let { return it }
                        val segmented = split(syllableKeys)
                        if (code > 0L) {
                                cache(code, segmented)
                        }
                        segmented
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
