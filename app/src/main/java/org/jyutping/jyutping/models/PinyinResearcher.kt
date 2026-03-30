package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.PinyinLexicon
import kotlin.math.max

object PinyinResearcher {
        fun reverseLookup(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation, db: DatabaseHelper): List<Lexicon> {
                return pinyinSearch(keys = keys, segmentation = segmentation, db = db)
                        .flatMap { lexicon ->
                                db.lookupRomanization(lexicon.text)
                                        .map { romanization ->
                                                Lexicon(
                                                        text = lexicon.text,
                                                        romanization = romanization,
                                                        input = lexicon.input,
                                                        mark = lexicon.mark,
                                                        number = lexicon.order
                                                )
                                        }
                        }
        }
}

private fun PinyinResearcher.pinyinSearch(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation, limit: Int? = null, db: DatabaseHelper): List<PinyinLexicon> {
        val inputLength = keys.size
        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val spellMatched = db.pinyinSpellMatch(text = text, limit = limit)
        val anchorsMatched = db.pinyinAnchorsMatch(keys = keys, input = text, limit = limit)
        val queried = pinyinQuery(inputLength = inputLength, segmentation = segmentation, limit = limit, db = db)
        val fetched: List<PinyinLexicon> = run {
                val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.order }.distinct()
                val notIdealQueried = queried.filter { it.inputCount < inputLength } .sorted().distinct()
                val fullInput = (spellMatched + idealQueried + anchorsMatched).distinct()
                val primary = fullInput.take(10)
                val secondary = fullInput.sorted().take(10)
                val tertiary = notIdealQueried.take(10)
                val quaternary = notIdealQueried.sortedBy { it.order }.take(10)
                return@run (primary + secondary + tertiary + quaternary + fullInput + notIdealQueried).distinct()
        }
        return fetched
        /*
        val shouldMatchPrefixes: Boolean = when {
                spellMatched.isNotEmpty() -> false
                queried.none { it.inputCount == inputLength } -> false
                else -> segmentation.none { it.pinyinSchemeLength == inputLength }
        }
        val prefixesLimit: Int = if (limit == null) 500 else 200
        */
}

private fun PinyinResearcher.pinyinQuery(inputLength: Int, segmentation: PinyinSegmentation, limit: Int? = null, db: DatabaseHelper): List<PinyinLexicon> {
        val idealSchemes = segmentation.filter { it.pinyinSchemeLength == inputLength }
        if (idealSchemes.isEmpty()) {
                return segmentation.flatMap { scheme ->
                        return@flatMap db.pinyinSpellMatch(text = scheme.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit)
                }
        } else {
                return idealSchemes.flatMap { scheme ->
                        when (scheme.size) {
                                0 -> return@flatMap emptyList<PinyinLexicon>()
                                1 -> return@flatMap db.pinyinSpellMatch(text = scheme.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit)
                                else -> return@flatMap scheme.size.downTo(1)
                                        .map { scheme.take(it) }
                                        .flatMap { slice -> db.pinyinSpellMatch(text = slice.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit) }
                        }
                }
        }
}

private fun DatabaseHelper.pinyinAnchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<PinyinLexicon> {
        val code = keys.combinedCode()
        if (code <= 0) return emptyList()
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val limitValue: Int = limit ?: 100
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE anchors = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val rowID = cursor.getInt(0)
                val word = cursor.getString(1)
                val pinyin = cursor.getString(2)
                val instance = PinyinLexicon(text = word, pinyin = pinyin, input = inputText, mark = inputText, order = rowID)
                items.add(instance)
        }
        cursor.close()
        return items
}
private fun DatabaseHelper.pinyinSpellMatch(text: String, limit: Int? = null): List<PinyinLexicon> {
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val code: Int = text.hashCode()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE spell = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val rowID = cursor.getInt(0)
                val word = cursor.getString(1)
                val pinyin = cursor.getString(2)
                val instance = PinyinLexicon(text = word, pinyin = pinyin, input = text, mark = pinyin, order = rowID)
                items.add(instance)
        }
        cursor.close()
        return items
}

private fun DatabaseHelper.lookupRomanization(text: String): List<String> {
        val matched = reverseLookup(text)
        if (matched.isNotEmpty()) return matched
        if (text.length == 1) return emptyList()
        fun fetchLeading(word: String): Pair<String?, Int> {
                var chars = word
                var romanization: String? = null
                var matchedCount = 0
                while (romanization == null && chars.isNotEmpty()) {
                        romanization = reverseLookup(chars).firstOrNull()
                        matchedCount = chars.length
                        chars = chars.dropLast(1)
                }
                return romanization?.let { it to matchedCount } ?: (null to 0)
        }
        var chars = text
        val fetches = mutableListOf<String>()
        while (chars.isNotEmpty()) {
                val leading = fetchLeading(chars)
                val romanization = leading.first
                if (romanization != null) {
                        fetches.add(romanization)
                        val length = max(1, leading.second)
                        chars = chars.drop(length)
                } else {
                        fetches.add("?")
                        chars = chars.drop(1)
                }
        }
        if (fetches.isEmpty()) return emptyList()
        val suggestion = fetches.joinToString(separator = PresetString.SPACE)
        return listOf(suggestion)
}
