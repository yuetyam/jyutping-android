package org.jyutping.jyutping.models

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.presets.PresetString

object PinyinResearcher {
        fun reverseLookup(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation): List<Lexicon> {
                val isLetterKeyOnly: Boolean = keys.none { it.isLetter.negative }
                // TODO: Handle separators
                if (isLetterKeyOnly.negative) return emptyList()
                val canSegment: Boolean = segmentation.fold(0) { acc, scheme -> acc + scheme.size} > 0
                return if (canSegment) {
                        pinyinSearch(keys = keys, segmentation = segmentation)
                                .flatMap { lexicon ->
                                        Elephant.lookupRomanization(lexicon.text)
                                                .map { romanization ->
                                                        Lexicon(
                                                                text = lexicon.text,
                                                                romanization = romanization,
                                                                input = lexicon.input,
                                                                mark = lexicon.mark,
                                                                number = lexicon.number
                                                        )
                                                }
                                }
                } else {
                        processPinyinSlices(keys = keys, text = keys.joinToString(separator = PresetString.EMPTY) { it.text })
                                .flatMap { lexicon ->
                                        Elephant.lookupRomanization(lexicon.text)
                                                .map { romanization ->
                                                        Lexicon(
                                                                text = lexicon.text,
                                                                romanization = romanization,
                                                                input = lexicon.input,
                                                                mark = lexicon.mark,
                                                                number = lexicon.number
                                                        )
                                                }
                                }
                }
        }
        fun nineKeyReverseLookup(combos: List<Combo>): List<Lexicon> {
                return pinyinNineKeySearch(combos)
                        .flatMap { lexicon ->
                                Elephant.lookupRomanization(lexicon.text)
                                        .map { romanization ->
                                                Lexicon(
                                                        text = lexicon.text,
                                                        romanization = romanization,
                                                        input = lexicon.input,
                                                        mark = lexicon.mark,
                                                        number = lexicon.number
                                                )
                                        }
                        }
        }
}

private fun PinyinResearcher.processPinyinSlices(keys: List<VirtualInputKey>, text: String, limit: Int? = null): List<PinyinLexicon> {
        val adjustedLimit: Int = if (limit == null) 300 else 100
        val inputLength = keys.size
        return 0.rangeUntil(inputLength).flatMap<Int, PinyinLexicon> { number ->
                val leadingKeys = keys.dropLast(number)
                val leadingText = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                val spellMatched = pinyinSpellMatch(text = leadingText, limit = limit)
                        .map { modify(item = it, text = text, textLength = inputLength) }
                val anchorsMatched = pinyinAnchorsMatch(keys = leadingKeys, input = leadingText, limit = adjustedLimit)
                        .map { modify(item = it, text = text, textLength = inputLength) }
                        .sorted()
                        .take(72)
                return@flatMap spellMatched + anchorsMatched
        }.distinct().sorted()
}
private fun PinyinResearcher.modify(item: PinyinLexicon, text: String, textLength: Int): PinyinLexicon {
        if (item.inputCount == textLength) return item
        if (item.pinyin.filterNot { it.isSpace }.startsWith(text)) return PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
        val syllables = item.pinyin.split(PresetString.SPACE)
        val lastSyllable = syllables.lastOrNull() ?: return item
        if (text.endsWith(lastSyllable).negative) return item
        val isMatched: Boolean = ((syllables.size - 1) + lastSyllable.length) == textLength
        if (isMatched.negative) return item
        return PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
}

private fun PinyinResearcher.pinyinSearch(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation, limit: Int? = null): List<PinyinLexicon> {
        val inputLength = keys.size
        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val spellMatched = pinyinSpellMatch(text = text, limit = limit)
        val anchorsMatched = pinyinAnchorsMatch(keys = keys, input = text, limit = limit)
        val queried = pinyinQuery(inputLength = inputLength, segmentation = segmentation, limit = limit)
        val shouldMatchPrefixes: Boolean = when {
                spellMatched.isNotEmpty() -> false
                queried.any { it.inputCount == inputLength } -> false
                else -> segmentation.none { it.pinyinSchemeLength == inputLength }
        }
        val prefixesLimit: Int = if (limit == null) 500 else 200
        val prefixMatched: List<PinyinLexicon> = if (shouldMatchPrefixes.negative) emptyList() else segmentation.flatMap { scheme ->
                val tail = keys.drop(scheme.pinyinSchemeLength)
                val lastAnchor = tail.firstOrNull() ?: return@flatMap emptyList<PinyinLexicon>()
                val schemeAnchors = scheme.mapNotNull { it.keys.firstOrNull() }
                val conjoined = schemeAnchors + tail
                val anchors = schemeAnchors + lastAnchor
                val schemeMark: String = scheme.joinToString(separator = PresetString.SPACE) { it.text }
                val mark: String = schemeMark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                val conjoinedMatched: List<PinyinLexicon> = pinyinAnchorsMatch(keys = conjoined, limit = prefixesLimit)
                        .mapNotNull { item ->
                                if (item.pinyin.startsWith(schemeMark).negative) return@mapNotNull null
                                val tailAnchors = item.pinyin.drop(schemeMark.length).split(PresetString.SPACE).mapNotNull { it.firstOrNull() }
                                if (tailAnchors != tail.mapNotNull { it.text.firstOrNull() }) return@mapNotNull null
                                return@mapNotNull PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = mark, number = item.number)
                        }
                val anchorsMatched: List<PinyinLexicon> = pinyinAnchorsMatch(keys = anchors, limit = prefixesLimit)
                        .mapNotNull { item ->
                                if (item.pinyin.startsWith(mark).negative) null else PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = mark, number = item.number)
                        }
                return@flatMap conjoinedMatched + anchorsMatched
        }
        val gainedMatched: List<PinyinLexicon> = if (shouldMatchPrefixes.negative) emptyList() else 1.rangeUntil(inputLength).reversed().flatMap { number ->
                val leadingKeys = keys.dropLast(number)
                val leadingText = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                return@flatMap pinyinAnchorsMatch(keys = leadingKeys, input = leadingText, limit = 300)
        }.mapNotNull { item ->
                if (item.pinyin.filterNot { it.isSpace }.startsWith(text)) {
                        return@mapNotNull PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
                }
                val syllables = item.pinyin.split(PresetString.SPACE)
                val lastSyllable = syllables.lastOrNull() ?: return@mapNotNull null
                if (text.endsWith(lastSyllable).negative) return@mapNotNull null
                val isMatched: Boolean = ((syllables.size - 1) + lastSyllable.length) == inputLength
                if (isMatched.negative) return@mapNotNull null
                return@mapNotNull PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
        }
        val fetched: List<PinyinLexicon> = run {
                val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.number }.distinct()
                val notIdealQueried = queried.filter { it.inputCount < inputLength } .sorted().distinct()
                val fullInput = (spellMatched + idealQueried + anchorsMatched + prefixMatched + gainedMatched).distinct()
                val primary = fullInput.take(10)
                val secondary = fullInput.sorted().take(10)
                val tertiary = notIdealQueried.take(10)
                val quaternary = notIdealQueried.sortedBy { it.number }.take(10)
                return@run (primary + secondary + tertiary + quaternary + fullInput + notIdealQueried).distinct()
        }
        val firstInputCount = fetched.firstOrNull()?.inputCount ?: return processPinyinSlices(keys = keys, text = text, limit = limit)
        if (firstInputCount >= inputLength) return fetched
        val headInputLengths = fetched.map { it.inputCount }.distinct()
        val concatenated: List<PinyinLexicon> = headInputLengths.mapNotNull { headLength ->
                val tailKeys = keys.drop(headLength)
                val tailSegmentation = PinyinSegmenter.segment(tailKeys)
                val tailLexicon = pinyinSearch(keys = tailKeys, segmentation = tailSegmentation, limit = 50).firstOrNull() ?: return@mapNotNull null
                val headLexicon = fetched.firstOrNull { it.inputCount == headLength } ?: return@mapNotNull null
                return@mapNotNull headLexicon + tailLexicon
        }.distinct().sorted().take(1)
        return concatenated + fetched
}

private fun PinyinResearcher.pinyinQuery(inputLength: Int, segmentation: PinyinSegmentation, limit: Int? = null): List<PinyinLexicon> {
        val idealSchemes = segmentation.filter { it.pinyinSchemeLength == inputLength }
        if (idealSchemes.isEmpty()) {
                return segmentation.flatMap { scheme ->
                        return@flatMap pinyinSpellMatch(text = scheme.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit)
                }
        } else {
                return idealSchemes.flatMap { scheme ->
                        when (scheme.size) {
                                0 -> return@flatMap emptyList<PinyinLexicon>()
                                1 -> return@flatMap pinyinSpellMatch(text = scheme.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit)
                                else -> return@flatMap scheme.size.downTo(1)
                                        .map { scheme.take(it) }
                                        .flatMap { slice -> pinyinSpellMatch(text = slice.joinToString(separator = PresetString.EMPTY) { it.text }, limit = limit) }
                        }
                }
        }
}

private fun PinyinResearcher.pinyinAnchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<PinyinLexicon> {
        val code = keys.combinedCode()
        if (code <= 0) return emptyList()
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val limitValue: Int = limit ?: 100
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE anchors = $code LIMIT ${limitValue};"
        Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val rowID = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val pinyin = cursor.getString(2)
                        val instance = PinyinLexicon(text = word, pinyin = pinyin, input = inputText, mark = inputText, number = rowID)
                        items.add(instance)
                }
        }
        return items
}
private fun PinyinResearcher.pinyinSpellMatch(text: String, limit: Int? = null): List<PinyinLexicon> {
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val code: Int = text.hashCode()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE spell = $code LIMIT ${limitValue};"
        Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val rowID = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val pinyin = cursor.getString(2)
                        val instance = PinyinLexicon(text = word, pinyin = pinyin, input = text, mark = pinyin, number = rowID)
                        items.add(instance)
                }
        }
        return items
}

private fun PinyinResearcher.pinyinNineKeySearch(combos: List<Combo>, limit: Int? = null): List<PinyinLexicon> {
        val inputLength: Int = combos.size
        val fullCode: Long = combos.map { it.digit }.decimalCombined()
        when (inputLength) {
                0 -> return emptyList()
                1 -> return pinyinNineKeyCodeMatch(fullCode, limit) + pinyinNineKeyAnchorsMatch(fullCode, 100)
                else -> {}
        }
        val fullMatched = pinyinNineKeyCodeMatch(fullCode, limit)
        val idealAnchorsMatched = pinyinNineKeyAnchorsMatch(fullCode, 4)
        val codeMatched: List<PinyinLexicon> = 1.rangeUntil(inputLength).flatMap { number ->
                val code = combos.dropLast(number).map { it.digit }.decimalCombined()
                return@flatMap if (code < 1) emptyList() else pinyinNineKeyCodeMatch(code, limit)
        }
        val anchorsMatched: List<PinyinLexicon> = 0.rangeUntil(inputLength).flatMap { number ->
                val code = combos.dropLast(number).map { it.digit }.decimalCombined()
                return@flatMap if (code < 1) emptyList() else pinyinNineKeyAnchorsMatch(code, limit)
        }
        val queried = (fullMatched + idealAnchorsMatched + codeMatched + anchorsMatched)
        val firstInputCount = queried.firstOrNull()?.inputCount ?: 0
        if (firstInputCount >= inputLength) return queried
        val tailCombos = combos.drop(firstInputCount)
        val tailCode = tailCombos.map { it.digit }.decimalCombined()
        if (tailCode < 1) return queried
        val tailLexicons = pinyinNineKeyCodeMatch(tailCode, 20) + pinyinNineKeyAnchorsMatch(tailCode, 20)
        if (tailLexicons.isEmpty()) return queried
        val head = queried.firstOrNull() ?: return queried
        val concatenated = tailLexicons.map { head + it }.sorted().take(1)
        return concatenated + queried
}
private fun PinyinResearcher.pinyinNineKeyAnchorsMatch(code: Long, limit: Int? = null): List<PinyinLexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val limitValue: Int = limit ?: 30
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE nine_key_anchors = $code LIMIT ${limitValue};"
        Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val rowID = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val pinyin = cursor.getString(2)
                        val anchors = pinyin.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                        val instance = PinyinLexicon(text = word, pinyin = pinyin, input = anchors, mark = anchors, number = rowID)
                        items.add(instance)
                }
        }
        return items
}
private fun PinyinResearcher.pinyinNineKeyCodeMatch(code: Long, limit: Int? = null): List<PinyinLexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<PinyinLexicon> = mutableListOf()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE nine_key_code = $code LIMIT ${limitValue};"
        Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val rowID = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val pinyin = cursor.getString(2)
                        val instance = PinyinLexicon(text = word, pinyin = pinyin, input = pinyin.filterNot { it.isSpace }, mark = pinyin, number = rowID)
                        items.add(instance)
                }
        }
        return items
}
