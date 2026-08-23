package org.jyutping.jyutping.models

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.strippedSpaces
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.decimalCombinedCode
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

object PinyinResearcher {

        private suspend fun isActive(): Boolean = currentCoroutineContext().isActive

        suspend fun reverseLookup(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation): List<Lexicon> {
                val hasSeparators: Boolean = keys.any { it.isApostrophe }
                return if (hasSeparators) {
                        letterKeysSearch(keys = keys.filter { it.isLetter }, segmentation = segmentation)
                                .filteredSyllableSeparators(keys = keys)
                                .flatMap { Elephant.reveresLookup(text = it.text, input = it.input, mark = it.mark) }
                } else {
                        letterKeysSearch(keys = keys, segmentation = segmentation)
                                .flatMap { Elephant.reveresLookup(text = it.text, input = it.input, mark = it.mark) }
                }
        }

        private suspend fun letterKeysSearch(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation): List<PinyinLexicon> {
                val canSegment: Boolean = segmentation.isNotEmpty()
                return if (canSegment) {
                        pinyinSearch(keys = keys, segmentation = segmentation)
                } else {
                        processPinyinSlices(keys = keys, text = keys.joinToString(separator = PresetString.EMPTY) { it.text })
                }
        }

        private suspend fun processPinyinSlices(keys: List<VirtualInputKey>, text: String, limit: Int? = null): List<PinyinLexicon> {
                val adjustedLimit: Int = if (limit == null) 300 else 100
                val inputLength: Int = keys.size
                return (0 until inputLength).flatMap { number ->
                        val leadingKeys: List<VirtualInputKey> = keys.dropLast(number)
                        val leadingText: String = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                        val spellMatched = pinyinSpellMatch(keys = leadingKeys, complexity = leadingKeys.size.toLong(), input = leadingText, limit = limit)
                                .map { modify(item = it, text = text, textLength = inputLength) }
                        val anchorsMatched = pinyinAnchorsMatch(keys = leadingKeys, input = leadingText, limit = adjustedLimit)
                                .map { modify(item = it, text = text, textLength = inputLength) }
                                .sorted()
                                .take(72)
                        spellMatched + anchorsMatched
                }
                        .distinct()
                        .sorted()
        }

        private fun modify(item: PinyinLexicon, text: String, textLength: Int): PinyinLexicon {
                if (item.inputCount == textLength) return item
                if (item.pinyin.strippedSpaces().startsWith(text)) {
                        return PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
                }
                val syllables: List<String> = item.pinyin.split(PresetCharacter.SPACE)
                val lastSyllable: String = syllables.lastOrNull() ?: return item
                if (text.endsWith(lastSyllable).negative) return item
                val isMatched: Boolean = ((syllables.size - 1) + lastSyllable.length) == textLength
                if (isMatched.negative) return item
                return PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
        }

        private suspend fun pinyinSearch(keys: List<VirtualInputKey>, segmentation: PinyinSegmentation, limit: Int? = null): List<PinyinLexicon> {
                val inputLength: Int = keys.size
                val text: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val anchorsMatched = pinyinAnchorsMatch(keys = keys, input = text, limit = limit)
                val queried = pinyinQuery(inputLength = inputLength, segmentation = segmentation, limit = limit)
                val shouldMatchPrefixes: Boolean = run {
                        if (queried.any { it.inputCount == inputLength }) return@run false
                        segmentation.any { it.length == inputLength }.negative
                }
                val prefixesLimit: Int = if (limit == null) 500 else 200
                val prefixMatched: List<PinyinLexicon> = if (shouldMatchPrefixes.negative) emptyList() else segmentation.flatMap { scheme ->
                        val tail: List<VirtualInputKey> = keys.drop(scheme.length)
                        val lastAnchor = tail.firstOrNull() ?: return@flatMap emptyList<PinyinLexicon>()
                        val schemeAnchors: List<VirtualInputKey> = scheme.mapNotNull { it.keys.firstOrNull() }
                        val conjoined: List<VirtualInputKey> = schemeAnchors + tail
                        val anchors: List<VirtualInputKey> = schemeAnchors + listOf(lastAnchor)
                        val schemeMark: String = scheme.joinToString(separator = PresetString.SPACE) { it.text }
                        val mark: String = schemeMark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        val conjoinedMatched = pinyinAnchorsMatch(keys = conjoined, limit = prefixesLimit)
                                .mapNotNull { item ->
                                        if (item.pinyin.startsWith(schemeMark).negative) return@mapNotNull null
                                        val tailAnchors: List<Char> = item.pinyin.drop(schemeMark.length).split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }
                                        if (tailAnchors != tail.mapNotNull { it.text.firstOrNull() }) return@mapNotNull null
                                        PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = mark, number = item.number)
                                }
                        val anchorsMatchedInScheme = pinyinAnchorsMatch(keys = anchors, limit = prefixesLimit)
                                .mapNotNull { item ->
                                        if (item.pinyin.startsWith(mark).negative) return@mapNotNull null
                                        PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = mark, number = item.number)
                                }
                        conjoinedMatched + anchorsMatchedInScheme
                }
                val gainedMatched: List<PinyinLexicon> = if (shouldMatchPrefixes.negative) emptyList() else (1 until inputLength).flatMap { number ->
                        val leadingKeys: List<VirtualInputKey> = keys.dropLast(number)
                        val leadingText: String = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                        pinyinAnchorsMatch(keys = leadingKeys, input = leadingText, limit = 300)
                }.mapNotNull { item ->
                        if (item.pinyin.strippedSpaces().startsWith(text)) {
                                return@mapNotNull PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
                        }
                        val syllables: List<String> = item.pinyin.split(PresetCharacter.SPACE)
                        val lastSyllable: String = syllables.lastOrNull() ?: return@mapNotNull null
                        if (text.endsWith(lastSyllable).negative) return@mapNotNull null
                        val isMatched: Boolean = ((syllables.size - 1) + lastSyllable.length) == inputLength
                        if (isMatched.negative) return@mapNotNull null
                        PinyinLexicon(text = item.text, pinyin = item.pinyin, input = text, mark = text, number = item.number)
                }
                val fetched: List<PinyinLexicon> = run {
                        val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.number }.distinct()
                        val notIdealQueried = queried.filter { it.inputCount < inputLength }.sorted().distinct()
                        val fullInput = (idealQueried + anchorsMatched + prefixMatched + gainedMatched).distinct()
                        val primary = fullInput.take(10)
                        val secondary = fullInput.sorted().take(10)
                        val tertiary = notIdealQueried.take(10)
                        val quaternary = notIdealQueried.sortedBy { it.number }.take(10)
                        (primary + secondary + tertiary + quaternary + fullInput + notIdealQueried).distinct()
                }
                val firstInputCount: Int = fetched.firstOrNull()?.inputCount
                        ?: return processPinyinSlices(keys = keys, text = text, limit = limit)
                if (firstInputCount >= inputLength) return fetched
                val headInputLengths: List<Int> = fetched.map { it.inputCount }.distinct()
                val concatenated: List<PinyinLexicon> = headInputLengths.mapNotNull { headLength ->
                        val tailKeys: List<VirtualInputKey> = keys.drop(headLength)
                        val tailSegmentation: PinyinSegmentation = PinyinSegmenter.segment(keys = tailKeys)
                        val tailLexicon: PinyinLexicon = pinyinSearch(keys = tailKeys, segmentation = tailSegmentation, limit = 50).firstOrNull() ?: return@mapNotNull null
                        val headLexicon: PinyinLexicon = fetched.first { it.inputCount == headLength }
                        headLexicon + tailLexicon
                }
                        .distinct()
                        .sorted()
                        .take(1)
                return concatenated + fetched
        }

        private fun pinyinQuery(inputLength: Int, segmentation: PinyinSegmentation, limit: Int? = null): List<PinyinLexicon> {
                val idealSchemes = segmentation.filter { it.length == inputLength }
                return if (idealSchemes.isEmpty()) {
                        segmentation.flatMap { scheme ->
                                pinyinSpellMatch(keys = scheme.keys, complexity = scheme.complexity, mark = scheme.mark, limit = limit)
                        }
                } else {
                        idealSchemes.flatMap { scheme ->
                                when (scheme.size) {
                                        0 -> emptyList()
                                        1 -> pinyinSpellMatch(keys = scheme.keys, complexity = scheme.complexity, mark = scheme.mark, limit = limit)
                                        else -> (1..scheme.size).reversed().map { scheme.take(it) }.flatMap { slice ->
                                                pinyinSpellMatch(keys = slice.keys, complexity = slice.complexity, mark = slice.mark, limit = limit)
                                        }
                                }
                        }
                }
        }

        private fun pinyinSpellMatch(keys: List<VirtualInputKey>, complexity: Long, input: String? = null, mark: String? = null, limit: Int? = null): List<PinyinLexicon> {
                val spell: Long = keys.conjoinedCode
                val complexityValue: Long = complexity
                val limitValue: Long = limit?.toLong() ?: -1L
                val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val instances: MutableList<PinyinLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE spell = $spell AND complexity = $complexityValue ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val pinyin = cursor.getString(2) ?: continue
                                instances.add(PinyinLexicon(text = word, pinyin = pinyin, input = inputText, mark = mark ?: pinyin, number = rowId))
                        }
                }
                return instances
        }

        private fun pinyinAnchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<PinyinLexicon> {
                val code: Long = keys.conjoinedCode
                val charCount: Long = keys.size.toLong()
                val limitValue: Long = limit?.toLong() ?: 100L
                val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val instances: MutableList<PinyinLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE anchors = $code AND char_count = $charCount ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val romanization = cursor.getString(2) ?: continue
                                instances.add(PinyinLexicon(text = word, pinyin = romanization, input = inputText, mark = inputText, number = rowId))
                        }
                }
                return instances
        }

        suspend fun nineKeyReverseLookup(combos: List<Combo>, segmentation: PinyinNineKeySegmentation): List<Lexicon> {
                return pinyinNineKeySearch(combos = combos, segmentation = segmentation)
                        .flatMap { Elephant.reveresLookup(text = it.text, input = it.input, mark = it.mark) }
        }

        private suspend fun pinyinNineKeySearch(combos: List<Combo>, segmentation: PinyinNineKeySegmentation, limit: Int? = null): List<PinyinLexicon> {
                val inputLength: Int = combos.size
                if (segmentation.isEmpty()) {
                        return processPinyinNineKeySlices(combos = combos, limit = limit)
                }
                val codeMatched = pinyinNineKeyQuery(inputLength = inputLength, segmentation = segmentation, limit = limit)
                val anchorsMatched = pinyinNineKeyAnchorsMatch(code = combos.decimalCombinedCode, charCount = inputLength.toLong(), limit = limit)
                val fetched = (codeMatched + anchorsMatched).ordered(textCount = inputLength)
                val firstInputCount: Int = fetched.firstOrNull()?.inputCount
                        ?: return processPinyinNineKeySlices(combos = combos, limit = limit)
                if (firstInputCount >= inputLength) return fetched
                val headInputLengths: List<Int> = fetched.map { it.inputCount }.distinct()
                val concatenated: List<PinyinLexicon> = headInputLengths.mapNotNull { headLength ->
                        val tailCombos: List<Combo> = combos.drop(headLength)
                        val tailSegmentation: PinyinNineKeySegmentation = PinyinNineKeySegmenter.segment(combos = tailCombos)
                        val tailLexicon: PinyinLexicon = pinyinNineKeySearch(combos = tailCombos, segmentation = tailSegmentation, limit = 20).firstOrNull() ?: return@mapNotNull null
                        val headLexicon: PinyinLexicon = fetched.first { it.inputCount == headLength }
                        headLexicon + tailLexicon
                }
                        .distinct()
                        .sorted()
                        .take(1)
                return concatenated + fetched
        }

        private fun processPinyinNineKeySlices(combos: List<Combo>, limit: Int? = null): List<PinyinLexicon> {
                return (0 until combos.size).flatMap { number ->
                        val leadingCombos: List<Combo> = combos.dropLast(number)
                        val code: Long = leadingCombos.decimalCombinedCode
                        val length: Long = leadingCombos.size.toLong()
                        pinyinNineKeyCodeMatch(code = code, complexity = length, limit = limit) + pinyinNineKeyAnchorsMatch(code = code, charCount = length, limit = limit)
                }.ordered(textCount = combos.size)
        }

        private fun pinyinNineKeyQuery(inputLength: Int, segmentation: PinyinNineKeySegmentation, limit: Int? = null): List<PinyinLexicon> {
                val idealSchemes = segmentation.filter { it.length == inputLength }
                return if (idealSchemes.isEmpty()) {
                        segmentation.flatMap { pinyinNineKeyCodeMatch(scheme = it, limit = limit) }
                } else {
                        idealSchemes.flatMap { scheme ->
                                when (scheme.size) {
                                        0 -> emptyList()
                                        1 -> pinyinNineKeyCodeMatch(scheme = scheme, limit = limit)
                                        else -> (1..scheme.size).reversed().map { scheme.take(it) }.flatMap { pinyinNineKeyCodeMatch(scheme = it, limit = limit) }
                                }
                        }
                }
        }

        private fun pinyinNineKeyCodeMatch(scheme: PinyinNineKeyScheme, limit: Int? = null): List<PinyinLexicon> {
                return pinyinNineKeyCodeMatch(code = scheme.combos.decimalCombinedCode, complexity = scheme.complexity, limit = limit)
        }

        private fun pinyinNineKeyAnchorsMatch(code: Long, charCount: Long, limit: Int? = null): List<PinyinLexicon> {
                val limitValue: Long = limit?.toLong() ?: 30L
                val instances: MutableList<PinyinLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE anchors_9key = $code AND char_count = $charCount ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val romanization = cursor.getString(2) ?: continue
                                val anchors = romanization.split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }
                                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                                instances.add(PinyinLexicon(text = word, pinyin = romanization, input = anchorText, mark = anchorText, number = rowId))
                        }
                }
                return instances
        }

        private fun pinyinNineKeyCodeMatch(code: Long, complexity: Long, limit: Int? = null): List<PinyinLexicon> {
                val limitValue: Long = limit?.toLong() ?: -1L
                val instances: MutableList<PinyinLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM pinyin_lexicon WHERE spell_9key = $code AND complexity = $complexity ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val romanization = cursor.getString(2) ?: continue
                                instances.add(PinyinLexicon(text = word, pinyin = romanization, input = romanization.strippedSpaces(), mark = romanization, number = rowId))
                        }
                }
                return instances
        }
}

private data class PinyinLexicon(
        /** Cantonese Chinese word */
        val text: String,

        /** Mandarin Pinyin romanization for the `word` */
        val pinyin: String,

        /** User input */
        val input: String,

        /** Character count of the `input` */
        val inputCount: Int = input.length,

        /** Formatted user input for pre-edit display */
        val mark: String,

        /** Rank; order. Smaller is preferred */
        val number: Int = 0
) : Comparable<PinyinLexicon> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinLexicon) return false
                return (text == other.text) && (pinyin == other.pinyin)
        }
        override fun hashCode(): Int {
                return text.hashCode() * 31 + pinyin.hashCode()
        }
        override fun compareTo(other: PinyinLexicon): Int {
                return inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: number.compareTo(other.number)
        }

        operator fun plus(another: PinyinLexicon): PinyinLexicon {
                val newText: String = text + another.text
                val newPinyin: String = pinyin + PresetString.SPACE + another.pinyin
                val newInput: String = input + another.input
                val newMark: String = mark + PresetString.SPACE + another.mark
                return PinyinLexicon(text = newText, pinyin = newPinyin, input = newInput, mark = newMark)
        }

        fun replacedInput(newInput: String): PinyinLexicon = PinyinLexicon(text = text, pinyin = pinyin, input = newInput, mark = mark, number = number)
}

private fun List<PinyinLexicon>.ordered(textCount: Int): List<PinyinLexicon> {
        val matched = filter { it.inputCount == textCount }.sortedBy { it.number }.distinct()
        val others = filter { it.inputCount != textCount }.sorted().distinct()
        val primary = matched.take(15)
        val secondary = others.take(10)
        val tertiary = others.sortedBy { it.number }.take(7)
        return (primary + secondary + tertiary + matched + others).distinct()
}

private fun List<PinyinLexicon>.filteredSyllableSeparators(keys: List<VirtualInputKey>): List<PinyinLexicon> {
        val hasLeadingSeparator: Boolean = keys.firstOrNull()?.isApostrophe ?: false
        if (hasLeadingSeparator) return emptyList()
        val hasTrailingSeparator: Boolean = keys.lastOrNull()?.isApostrophe ?: false
        val inputSeparatorCount: Int = keys.count { it.isApostrophe }
        val inputLength: Int = keys.size
        val text: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val textParts: List<String> = text.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
        val filtered: List<PinyinLexicon> = mapNotNull { item ->
                val syllables: List<String> = item.pinyin.split(PresetCharacter.SPACE)
                if (syllables == textParts) return@mapNotNull item.replacedInput(text)
                when {
                        (inputSeparatorCount == 1) && hasTrailingSeparator -> {
                                if (syllables.size != 1) return@mapNotNull null
                                if (item.inputCount != (inputLength - 1)) return@mapNotNull null
                                item.replacedInput(text)
                        }
                        inputSeparatorCount == 1 -> {
                                when (syllables.size) {
                                        1 -> {
                                                if (item.inputCount != textParts.firstOrNull()?.length) return@mapNotNull null
                                                item.replacedInput(item.input + PresetCharacter.APOSTROPHE)
                                        }
                                        2 -> {
                                                val isMatched: Boolean = run {
                                                        if (inputLength == 3) return@run true
                                                        if (syllables.first() == textParts.first()) return@run true
                                                        if (textParts.firstOrNull()?.length != 1) return@run false
                                                        if (textParts.firstOrNull()?.firstOrNull() != syllables.firstOrNull()?.firstOrNull()) return@run false
                                                        val lastSyllable = syllables.lastOrNull() ?: return@run false
                                                        textParts.lastOrNull()?.startsWith(lastSyllable) ?: false
                                                }
                                                if (isMatched.negative) return@mapNotNull null
                                                item.replacedInput(item.input + PresetCharacter.APOSTROPHE)
                                        }
                                        else -> null
                                }
                        }
                        (inputSeparatorCount == 2) && hasTrailingSeparator -> {
                                when (syllables.size) {
                                        1 -> {
                                                if (item.inputCount != textParts.firstOrNull()?.length) return@mapNotNull null
                                                item.replacedInput(item.input + PresetCharacter.APOSTROPHE)
                                        }
                                        2 -> {
                                                if (item.inputCount != (inputLength - 2)) return@mapNotNull null
                                                val isMatched: Boolean = run {
                                                        if (inputLength == 4) return@run true
                                                        if (syllables.first() == textParts.first()) return@run true
                                                        if (textParts.firstOrNull()?.length != 1) return@run false
                                                        if (textParts.firstOrNull()?.firstOrNull() != syllables.firstOrNull()?.firstOrNull()) return@run false
                                                        textParts.lastOrNull() == syllables.lastOrNull()
                                                }
                                                if (isMatched.negative) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                                        else -> null
                                }
                        }
                        (inputSeparatorCount == 2 && inputLength == 5 && textParts.size == 3) ||
                                (inputSeparatorCount == 3 && inputLength == 6 && textParts.size == 3) -> {
                                when (syllables.size) {
                                        1 -> {
                                                if (item.inputCount != 1) return@mapNotNull null
                                                item.replacedInput(item.input + PresetCharacter.APOSTROPHE)
                                        }
                                        2 -> {
                                                if (item.inputCount != 2) return@mapNotNull null
                                                item.replacedInput(item.input + PresetCharacter.APOSTROPHE + PresetCharacter.APOSTROPHE)
                                        }
                                        3 -> item.replacedInput(text)
                                        else -> null
                                }
                        }
                        else -> {
                                val textPartCount: Int = textParts.size
                                val syllableCount: Int = syllables.size
                                if (syllableCount >= textPartCount) return@mapNotNull null
                                val isMatched: Boolean = (0 until syllableCount).all { index -> syllables[index] == textParts[index] }
                                if (isMatched.negative) return@mapNotNull null
                                val separatorCount: Int = syllableCount - 1
                                val tail: String = "i".repeat(separatorCount)
                                item.replacedInput(item.input + tail)
                        }
                }
        }
        return filtered.sorted()
}
