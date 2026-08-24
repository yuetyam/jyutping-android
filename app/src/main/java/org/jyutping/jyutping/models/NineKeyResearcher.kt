package org.jyutping.jyutping.models

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.strippedTones
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.decimalCombinedCode
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

object NineKeyResearcher {

        private suspend fun isActive(): Boolean = currentCoroutineContext().isActive

        suspend fun suggest(combos: List<Combo>, segmentation: NineKeySegmentation): List<Lexicon> {
                val shouldProcessSlices: Boolean = (segmentation.firstOrNull()?.firstOrNull()?.alias?.size ?: 0) == 0
                return if (shouldProcessSlices) {
                        processSlices(combos = combos)
                } else {
                        search(combos = combos, segmentation = segmentation)
                }
        }

        private suspend fun processSlices(combos: List<Combo>, limit: Int? = null): List<Lexicon> {
                if (combos.isEmpty()) return emptyList()
                return (combos.size downTo 1).flatMap { number ->
                        if (isActive().negative) return@flatMap emptyList<Lexicon>()
                        if (number > Researcher.MAX_CHAR_COUNT) return@flatMap emptyList<Lexicon>()
                        anchorsMatch(combos = combos.take(number), limit = limit)
                }
        }

        private suspend fun search(combos: List<Combo>, segmentation: NineKeySegmentation, limit: Int? = null): List<Lexicon> {
                if (isActive().negative) return emptyList()
                val inputLength: Int = combos.size
                if (inputLength <= 1) return anchorsMatch(combos = combos, limit = limit)
                val anchorsMatched = anchorsMatch(combos = combos, limit = limit)
                val queried = query(inputLength = inputLength, segmentation = segmentation, limit = limit)
                val fetched: List<Lexicon> = run {
                        val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.number }.distinct()
                        val notIdealQueried = queried.filter { it.inputCount < inputLength }.sorted().distinct()
                        val extra: List<Lexicon> = if (idealQueried.isNotEmpty()) emptyList() else ExtraEntry.nineKeySearch(combos = combos)
                        (idealQueried + extra + anchorsMatched.take(4) + notIdealQueried).distinct()
                }
                val firstInputCount: Int = fetched.firstOrNull()?.inputCount
                        ?: return processSlices(combos = combos, limit = limit)
                if (firstInputCount >= inputLength) return fetched
                val tailCombos: List<Combo> = combos.drop(firstInputCount)
                val tailSegmentation: NineKeySegmentation = NineKeySegmenter.segment(combos = tailCombos)
                val tailLexicons: List<Lexicon> = search(combos = tailCombos, segmentation = tailSegmentation, limit = 20)
                if (tailLexicons.isEmpty()) return fetched
                val head: Lexicon = fetched.first()
                val concatenated: List<Lexicon> = tailLexicons.mapNotNull { head + it }
                        .sorted()
                        .take(1)
                return concatenated + fetched
        }

        private suspend fun query(inputLength: Int, segmentation: NineKeySegmentation, limit: Int? = null): List<Lexicon> {
                val idealSchemes = segmentation.filter { it.schemeLength == inputLength }
                return if (idealSchemes.isEmpty()) {
                        segmentation.flatMap { scheme -> perform(scheme = scheme, limit = limit) }
                } else {
                        idealSchemes.flatMap { scheme ->
                                when (scheme.size) {
                                        0 -> emptyList()
                                        1 -> perform(scheme = scheme, limit = limit)
                                        else -> (scheme.size downTo 1).flatMap { number -> perform(scheme = scheme.take(number), limit = limit) }
                                }
                        }
                }
        }

        private suspend fun perform(scheme: NineKeyScheme, limit: Int? = null): List<Lexicon> {
                if (isActive().negative || (scheme.size > Researcher.MAX_CHAR_COUNT)) return emptyList()
                val containsIrregular: Boolean = scheme.any { it.isIrregular }
                return if (containsIrregular) {
                        serialMatch(keys = scheme.serialOriginKeys, complexity = scheme.complexity, limit = limit)
                } else {
                        spellMatch(combos = scheme.originCombos, complexity = scheme.complexity, limit = limit)
                }
        }

        fun anchorsMatch(combos: List<Combo>, limit: Int? = null): List<Lexicon> {
                val charCount: Long = combos.size.toLong()
                if (charCount > Researcher.MAX_CHAR_COUNT) return emptyList()
                val anchorsCode: Long = combos.decimalCombinedCode
                val limitValue: Long = limit?.toLong() ?: 100L
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM lexicon_core WHERE anchors_9key = $anchorsCode AND char_count = $charCount ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val number = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val romanization = cursor.getString(2)
                                val anchors = romanization.split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }
                                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                                val instance = Lexicon(text = word, romanization = romanization, input = anchorText, mark = anchorText, number = number)
                                items.add(instance)
                        }
                }
                return items
        }

        fun spellMatch(combos: List<Combo>, complexity: Long, input: String? = null, mark: String? = null, limit: Int? = null): List<Lexicon> {
                val code: Long = combos.decimalCombinedCode
                val complexityValue: Long = complexity
                val limitValue: Long = limit?.toLong() ?: -1L
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM lexicon_core WHERE spell_9key = $code AND complexity = $complexityValue ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val number = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val romanization = cursor.getString(2)
                                val markText: String = mark ?: romanization.strippedTones()
                                val inputText: String = input ?: markText.filterNot { it.isSpace }
                                val instance = Lexicon(text = word, romanization = romanization, input = inputText, mark = markText, number = number)
                                items.add(instance)
                        }
                }
                return items
        }

        fun serialMatch(keys: List<VirtualInputKey>, complexity: Long, input: String? = null, mark: String? = null, limit: Int? = null): List<Lexicon> {
                val spell: Long = keys.conjoinedCode
                val complexityValue: Long = complexity
                val limitValue: Long = limit?.toLong() ?: -1L
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM lexicon_core WHERE spell = $spell AND complexity = $complexityValue ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val number = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val romanization = cursor.getString(2)
                                val markText: String = mark ?: romanization.strippedTones()
                                val inputText: String = input ?: markText.filterNot { it.isSpace }
                                val instance = Lexicon(text = word, romanization = romanization, input = inputText, mark = markText, number = number)
                                items.add(instance)
                        }
                }
                return items
        }
}
