package org.jyutping.jyutping.models

import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.min

typealias NewSegmentation = List<Scheme>

fun NewSegmentation.descended(): NewSegmentation = this.sortedWith(compareBy({it.length.unaryMinus()}, {it.size}))

object Segmenter {
        private fun splitLeading(events: List<InputKeyEvent>, db: DatabaseHelper): List<Syllable> {
                val maxLength = min(events.size, 6)
                if (maxLength < 1) return emptyList()
                return (maxLength downTo 1).mapNotNull { db.syllableMatch(events.take(it).combinedCode()) }
        }
        private fun split(events: List<InputKeyEvent>, db: DatabaseHelper): NewSegmentation {
                val headSyllables = splitLeading(events, db)
                if (headSyllables.isEmpty()) return emptyList()
                val eventCount = events.size
                val segmentation: HashSet<Scheme> = headSyllables.map { listOf(it) }.toHashSet()
                var previousSyllableCount = segmentation.map { it.size }.fold(0) { acc, i -> acc + i}
                var shouldContinue = true
                while (shouldContinue) {
                        for (scheme in segmentation.toList()) {
                                val schemeLength = scheme.length
                                if (schemeLength >= eventCount) continue
                                val tailEvent = events.drop(schemeLength)
                                val tailSyllables = splitLeading(tailEvent, db)
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
        fun segment(events: List<InputKeyEvent>, db: DatabaseHelper): NewSegmentation {
                return when (events.size) {
                        0 -> emptyList()
                        1 -> when (events.first()) {
                                InputKeyEvent.letterA -> letterA
                                InputKeyEvent.letterO -> letterO
                                InputKeyEvent.letterM -> letterM
                                else -> emptyList()
                        }
                        4 -> when (events.combinedCode()) {
                                32203220L -> mama
                                32203228L -> mami
                                else -> {
                                        val syllableEvents = events.filter { it.isSyllableLetter }
                                        val key = syllableEvents.combinedCode()
                                        cachedSegmentations[key]
                                        val segmented = split(syllableEvents, db)
                                        if (key > 0L) {
                                                cache(key, segmented)
                                        }
                                        segmented
                                }
                        }
                        else -> {
                                val syllableEvents = events.filter { it.isSyllableLetter }
                                val key = syllableEvents.combinedCode()
                                cachedSegmentations[key]
                                val segmented = split(syllableEvents, db)
                                if (key > 0L) {
                                        cache(key, segmented)
                                }
                                segmented
                        }
                }
        }

        private const val MAX_CACHE_COUNT: Int = 500
        private val cachedSegmentations: HashMap<Long, NewSegmentation> = hashMapOf()
        private fun cache(key: Long, segmentation: NewSegmentation) {
                if (cachedSegmentations.size > MAX_CACHE_COUNT) {
                        cachedSegmentations.clear()
                }
                cachedSegmentations[key] = segmentation
        }

        private val letterA: NewSegmentation = listOf(listOf(Syllable(aliasCode = 20, originCode = 2020)))
        private val letterO: NewSegmentation = listOf(listOf(Syllable(aliasCode = 34, originCode = 34)))
        private val letterM: NewSegmentation = listOf(listOf(Syllable(aliasCode = 32, originCode = 32)))
        private val mama: NewSegmentation = listOf(
                listOf(
                        Syllable(aliasCode = 3220, originCode = 322020),
                        Syllable(aliasCode = 3220, originCode = 322020)
                )
        )
        private val mami: NewSegmentation = listOf(
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
