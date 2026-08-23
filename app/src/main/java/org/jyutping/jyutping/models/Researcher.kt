package org.jyutping.jyutping.models

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.strippedSpaces
import org.jyutping.jyutping.extensions.strippedTones
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.extensions.toneDigitOnly
import org.jyutping.jyutping.extensions.latinLetterOnly
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

object Researcher {

        /** Maximum number of syllables in one lexicon entry */
        const val MAX_CHAR_COUNT: Int = 9

        private suspend fun isActive(): Boolean = currentCoroutineContext().isActive

        suspend fun suggest(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
                when (keys.size) {
                        0 -> return emptyList()
                        1 -> when (keys.first()) {
                                VirtualInputKey.letterA -> {
                                        val altKeys = listOf(VirtualInputKey.letterA, VirtualInputKey.letterA)
                                        val input = VirtualInputKey.letterA.text
                                        return spellMatch(keys = keys, complexity = 1, input = input) +
                                                spellMatch(keys = altKeys, complexity = 2, input = input) +
                                                anchorsMatch(keys = keys, input = input)
                                }
                                VirtualInputKey.letterO, VirtualInputKey.letterM -> {
                                        val input = keys.first().text
                                        return spellMatch(keys = keys, complexity = 1, input = input) +
                                                anchorsMatch(keys = keys, input = input)
                                }
                                else -> return anchorsMatch(keys = keys)
                        }
                        else -> return dispatch(keys = keys, segmentation = segmentation)
                }
        }

        private suspend fun dispatch(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
                val syllableKeys = keys.filter { it.isSyllableLetter }
                val firstAliasCount: Int = segmentation.firstOrNull()?.firstOrNull()?.alias?.size ?: 0
                val lexicons: List<Lexicon> = when {
                        firstAliasCount == 0 -> ExtraEntry.search(keys = keys) +
                                processSlices(keys = syllableKeys, text = syllableKeys.joinToString(separator = PresetString.EMPTY) { it.text })
                        (firstAliasCount == 1 && syllableKeys.size > 1) || (syllableKeys.size != keys.size) ->
                                search(keys = syllableKeys, segmentation = segmentation) +
                                processSlices(keys = syllableKeys, text = syllableKeys.joinToString(separator = PresetString.EMPTY) { it.text })
                        else -> search(keys = syllableKeys, segmentation = segmentation)
                }
                val hasApostrophe: Boolean = keys.any { it.isApostrophe }
                val hasToneInputKey: Boolean = keys.any { it.isToneInputKey }
                return when {
                        hasApostrophe.negative && hasToneInputKey.negative -> lexicons
                        hasApostrophe && hasToneInputKey -> {
                                val inputText = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                                val text = inputText.toneConverted()
                                return lexicons.mapNotNull { item ->
                                        if (text.startsWith(item.romanization).negative) return@mapNotNull null
                                        item.replacedInput(inputText)
                                }
                        }
                        hasApostrophe.negative && hasToneInputKey -> {
                                val inputText = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                                val toneInput = keys.filter { it.isSyllableLetter.negative }.joinToString(separator = PresetString.EMPTY) { it.text }
                                val text = inputText.toneConverted()
                                val textTones = text.toneDigitOnly
                                val qualified: List<Lexicon> = lexicons.mapNotNull { item ->
                                        val syllableText = item.romanization.strippedSpaces()
                                        if (syllableText.startsWith(text)) return@mapNotNull item.replacedInput(inputText)
                                        val tones = syllableText.toneDigitOnly
                                        when (Pair(textTones.length, tones.length)) {
                                                Pair(1, 1) -> {
                                                        if (textTones != tones) return@mapNotNull null
                                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isCantoneseToneDigit ?: false
                                                        if (isCorrectPosition.negative) return@mapNotNull null
                                                        val combinedInput = item.input + toneInput
                                                        item.replacedInput(combinedInput)
                                                }
                                                Pair(1, 2) -> {
                                                        val isToneLast: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                        if (isToneLast) {
                                                                if (tones.endsWith(textTones).negative) return@mapNotNull null
                                                                val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isCantoneseToneDigit ?: false
                                                                if (isCorrectPosition.negative) return@mapNotNull null
                                                                item.replacedInput(inputText)
                                                        } else {
                                                                if (tones.startsWith(textTones).negative) return@mapNotNull null
                                                                val combinedInput = item.input + toneInput
                                                                item.replacedInput(combinedInput)
                                                        }
                                                }
                                                Pair(2, 1) -> {
                                                        if (textTones.startsWith(tones).negative) return@mapNotNull null
                                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isCantoneseToneDigit ?: false
                                                        if (isCorrectPosition.negative) return@mapNotNull null
                                                        val leadingKeys: MutableList<VirtualInputKey> = mutableListOf()
                                                        for (key in keys) {
                                                                if (key.isSyllableLetter) {
                                                                        leadingKeys.add(key)
                                                                } else {
                                                                        break
                                                                }
                                                        }
                                                        for (key in keys.drop(leadingKeys.size)) {
                                                                if (key.isSyllableLetter.negative) {
                                                                        leadingKeys.add(key)
                                                                } else {
                                                                        break
                                                                }
                                                        }
                                                        item.replacedInput(leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text })
                                                }
                                                Pair(2, 2) -> {
                                                        if (textTones != tones) return@mapNotNull null
                                                        val isToneLast: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                        if (isToneLast) {
                                                                if (item.inputCount != (text.length - 2)) return@mapNotNull null
                                                                item.replacedInput(inputText)
                                                        } else {
                                                                val tail = text.drop(item.inputCount + 1)
                                                                val isCorrectPosition: Boolean = (tail.firstOrNull() == textTones.lastOrNull())
                                                                if (isCorrectPosition.negative) return@mapNotNull null
                                                                val combinedInput = item.input + toneInput
                                                                item.replacedInput(combinedInput)
                                                        }
                                                }
                                                else -> {
                                                        if (inputText.startsWith(syllableText).negative) return@mapNotNull null
                                                        item.replacedInput(syllableText)
                                                }
                                        }
                                }
                                qualified.sortedByDescending { it.inputCount }
                        }
                        else -> {
                                val isHeadingSeparator: Boolean = keys.firstOrNull()?.isApostrophe ?: false
                                val isTrailingSeparator: Boolean = keys.lastOrNull()?.isApostrophe ?: false
                                if (isHeadingSeparator) return emptyList()
                                val inputSeparatorCount: Int = keys.count { it.isApostrophe }
                                val inputLength: Int = keys.size
                                val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                                val textParts: List<String> = text.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
                                val qualified: List<Lexicon> = lexicons.mapNotNull { item ->
                                        val syllables: List<String> = item.romanization.strippedTones().split(PresetCharacter.SPACE)
                                        if (syllables == textParts) return@mapNotNull item.replacedInput(text)
                                        when {
                                                (inputSeparatorCount == 1) && isTrailingSeparator -> {
                                                        if (syllables.size != 1) return@mapNotNull null
                                                        if (item.inputCount != (inputLength - 1)) return@mapNotNull null
                                                        item.replacedInput(text)
                                                }
                                                inputSeparatorCount == 1 -> {
                                                        when (syllables.size) {
                                                                1 -> {
                                                                        if (item.inputCount != textParts.firstOrNull()?.length) return@mapNotNull null
                                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                                        item.replacedInput(combinedInput)
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
                                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                                        item.replacedInput(combinedInput)
                                                                }
                                                                else -> null
                                                        }
                                                }
                                                (inputSeparatorCount == 2) && isTrailingSeparator -> {
                                                        when (syllables.size) {
                                                                1 -> {
                                                                        if (item.inputCount != textParts.firstOrNull()?.length) return@mapNotNull null
                                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                                        item.replacedInput(combinedInput)
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
                                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE + PresetCharacter.APOSTROPHE
                                                                        item.replacedInput(combinedInput)
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
                                                        val combinedInput: String = item.input + tail
                                                        item.replacedInput(combinedInput)
                                                }
                                        }
                                }
                                if (qualified.isNotEmpty()) return qualified.sortedByDescending { it.inputCount }
                                val anchorKeys: List<VirtualInputKey> = keys.splitApostropheSeparated().mapNotNull { it.firstOrNull() }
                                val anchorCount: Int = anchorKeys.size
                                return anchorsMatch(keys = anchorKeys)
                                        .mapNotNull { item ->
                                                val syllables: List<String> = item.romanization.split(PresetCharacter.SPACE).map { it.dropLast(1) }
                                                if (syllables.size != anchorCount) return@mapNotNull null
                                                val isMatched: Boolean = (0 until anchorCount).all { index ->
                                                        val part: String = textParts[index]
                                                        val isAnchorOnly: Boolean = (part.length == 1)
                                                        if (isAnchorOnly) syllables[index].startsWith(part) else (syllables[index] == part)
                                                }
                                                if (isMatched.negative) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                        }
                }
        }

        private suspend fun processSlices(keys: List<VirtualInputKey>, text: String, limit: Int? = null): List<Lexicon> {
                val inputLength: Int = keys.size
                if (inputLength <= 0) return emptyList()
                val adjustedLimit: Int = if (limit == null) 300 else 100
                return (inputLength downTo 1).flatMap { number ->
                        if (isActive().negative) return@flatMap emptyList<Lexicon>()
                        if (number > MAX_CHAR_COUNT) return@flatMap emptyList<Lexicon>()
                        anchorsMatch(keys = keys.take(number), limit = adjustedLimit)
                                .map { modify(item = it, keys = keys, text = text, inputLength = inputLength) }
                                .sorted()
                                .take(50)
                }
                        .distinct()
                        .sorted()
        }

        private fun modify(item: Lexicon, keys: List<VirtualInputKey>, text: String, inputLength: Int): Lexicon {
                if (inputLength <= 1) return item
                if (item.inputCount == inputLength) return item
                val converted: Lexicon by lazy { Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text, number = item.number) }
                if (item.romanization.latinLetterOnly().startsWith(text)) return converted
                val lastSyllable: String = item.romanization.split(PresetCharacter.SPACE).lastOrNull()?.filter { it.isCantoneseToneDigit.negative } ?: return item
                val tail: List<VirtualInputKey> = keys.drop(item.inputCount - 1)
                if (tail.size > 6) return item
                val tailSyllable: String? = Segmenter.syllableText(keys = tail)
                return if (tailSyllable != null) {
                        if (lastSyllable == tailSyllable) converted else item
                } else {
                        val tailText: String = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        if (lastSyllable.startsWith(tailText)) converted else item
                }
        }

        private suspend fun search(keys: List<VirtualInputKey>, segmentation: Segmentation, limit: Int? = null): List<Lexicon> {
                if (isActive().negative) return emptyList()
                val inputLength: Int = keys.size
                val text: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val anchorsMatched = anchorsMatch(keys = keys, input = text, limit = limit)
                val queried = query(inputLength = inputLength, segmentation = segmentation, limit = limit)
                val shouldMatchPrefixes: Boolean = run {
                        if (inputLength <= 2 || inputLength >= 25) return@run false
                        if ((keys.lastOrNull() == VirtualInputKey.letterM) || (keys.firstOrNull() == VirtualInputKey.letterM)) return@run true
                        if (queried.any { it.inputCount == inputLength }) return@run false
                        segmentation.any { it.length == inputLength }.negative
                }
                val prefixesLimit: Int = if (limit == null) 500 else 200
                val prefixMatched: List<Lexicon> = if (shouldMatchPrefixes.negative) emptyList() else segmentation.flatMap { scheme ->
                        if (isActive().negative) return@flatMap emptyList<Lexicon>()
                        val leadingCharCount: Int = scheme.size
                        if (leadingCharCount <= 0 || leadingCharCount > MAX_CHAR_COUNT) return@flatMap emptyList<Lexicon>()
                        val tail: List<VirtualInputKey> = keys.drop(scheme.length)
                        val lastAnchor = tail.firstOrNull() ?: return@flatMap emptyList<Lexicon>()
                        val schemeAnchors: List<VirtualInputKey> = scheme.aliasAnchors
                        val conjoined: List<VirtualInputKey> = schemeAnchors + tail
                        val anchors: List<VirtualInputKey> = schemeAnchors + listOf(lastAnchor)
                        val schemeSyllableText: String = scheme.syllableText
                        val mark: String = scheme.mark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        val tailAsAnchorText: List<Char> = tail.mapNotNull { if (it == VirtualInputKey.letterY) VirtualInputKey.letterJ.text.firstOrNull() else it.text.firstOrNull() }
                        val conjoinedMatched = anchorsMatch(keys = conjoined, limit = prefixesLimit)
                                .mapNotNull { item ->
                                        val toneFreeRomanization: String = item.romanization.strippedTones()
                                        if (toneFreeRomanization.startsWith(schemeSyllableText).negative) return@mapNotNull null
                                        val suffixAnchorText: List<Char> = toneFreeRomanization.drop(schemeSyllableText.length).split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }
                                        if (suffixAnchorText != tailAsAnchorText) return@mapNotNull null
                                        Lexicon(text = item.text, romanization = item.romanization, input = text, mark = mark, number = item.number)
                                }
                        val transformedTailText: String = tail.mapIndexed { index, value -> if (index == 0 && value == VirtualInputKey.letterY) VirtualInputKey.letterJ.text else value.text }.joinToString(separator = PresetString.EMPTY)
                        val syllables: String = schemeSyllableText + PresetString.SPACE + transformedTailText
                        val anchorsMatchedInScheme = anchorsMatch(keys = anchors, limit = prefixesLimit)
                                .mapNotNull { item ->
                                        if (item.romanization.strippedTones().startsWith(syllables).negative) return@mapNotNull null
                                        Lexicon(text = item.text, romanization = item.romanization, input = text, mark = mark, number = item.number)
                                }
                        conjoinedMatched + anchorsMatchedInScheme
                }
                val gainedMatched: List<Lexicon> = if (shouldMatchPrefixes.negative) emptyList() else ((inputLength - 1) downTo 1).flatMap { number ->
                        if (isActive().negative || (number > MAX_CHAR_COUNT)) return@flatMap emptyList<Lexicon>()
                        val leadingKeys: List<VirtualInputKey> = keys.take(number)
                        val leadingText: String = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                        anchorsMatch(keys = leadingKeys, input = leadingText, limit = 300)
                }.mapNotNull { item ->
                        val tail: List<VirtualInputKey> = keys.drop(item.inputCount - 1)
                        if (tail.size > 6) return@mapNotNull null
                        val converted: Lexicon by lazy { Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text, number = item.number) }
                        if (item.romanization.latinLetterOnly().startsWith(text)) return@mapNotNull converted
                        val lastSyllable: String = item.romanization.split(PresetCharacter.SPACE).lastOrNull()?.filter { it.isCantoneseToneDigit.negative } ?: return@mapNotNull null
                        val tailSyllable: String? = Segmenter.syllableText(keys = tail)
                        if (tailSyllable != null) {
                                if (lastSyllable == tailSyllable) converted else null
                        } else {
                                val tailText: String = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                                if (lastSyllable.startsWith(tailText)) converted else null
                        }
                }
                val fetched: List<Lexicon> = run {
                        val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.number }.distinct()
                        val notIdealQueried = queried.filter { it.inputCount < inputLength }.sorted().distinct()
                        val extra: List<Lexicon> = if (idealQueried.isNotEmpty()) emptyList() else ExtraEntry.search(keys = keys)
                        val fullInput = (idealQueried + extra + anchorsMatched + prefixMatched + gainedMatched).distinct()
                        val primary = fullInput.take(10)
                        val secondary = fullInput.sorted().take(10)
                        val tertiary = notIdealQueried.take(10)
                        val quaternary = notIdealQueried.sortedBy { it.number }.take(10)
                        (primary + secondary + tertiary + quaternary + fullInput + notIdealQueried).distinct()
                }
                val firstInputCount: Int = fetched.firstOrNull()?.inputCount
                        ?: return processSlices(keys = keys, text = text, limit = limit)
                if (firstInputCount >= inputLength) return fetched
                val headInputLengths: List<Int> = fetched.map { it.inputCount }.distinct()
                val concatenated: List<Lexicon> = headInputLengths.mapNotNull { headLength ->
                        val tailKeys: List<VirtualInputKey> = keys.drop(headLength)
                        val tailSegmentation: Segmentation = Segmenter.segment(keys = tailKeys)
                        val tailLexicon: Lexicon = search(keys = tailKeys, segmentation = tailSegmentation, limit = 50).firstOrNull() ?: return@mapNotNull null
                        val headLexicon: Lexicon = fetched.first { it.inputCount == headLength }
                        headLexicon + tailLexicon
                }
                        .distinct()
                        .sorted()
                        .take(1)
                return concatenated + fetched
        }

        private suspend fun query(inputLength: Int, segmentation: Segmentation, limit: Int? = null): List<Lexicon> {
                val idealSchemes = segmentation.filter { it.length == inputLength }
                return if (idealSchemes.isEmpty()) {
                        segmentation.flatMap { scheme ->
                                if (isActive().negative || (scheme.size > MAX_CHAR_COUNT)) return@flatMap emptyList<Lexicon>()
                                spellMatch(keys = scheme.originKeys, complexity = scheme.complexity, input = scheme.aliasText, mark = scheme.mark, limit = limit)
                        }
                } else {
                        idealSchemes.flatMap { scheme ->
                                if (isActive().negative) return@flatMap emptyList<Lexicon>()
                                when (scheme.size) {
                                        0 -> emptyList()
                                        1 -> spellMatch(keys = scheme.originKeys, complexity = scheme.complexity, input = scheme.aliasText, mark = scheme.mark, limit = limit)
                                        else -> (scheme.size downTo 1).flatMap { number ->
                                                if (isActive().negative || (number > MAX_CHAR_COUNT)) return@flatMap emptyList<Lexicon>()
                                                val slice: Scheme = scheme.take(number)
                                                spellMatch(keys = slice.originKeys, complexity = slice.complexity, input = slice.aliasText, mark = slice.mark, limit = limit)
                                        }
                                }
                        }
                }
        }

        fun anchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<Lexicon> {
                val charCount: Long = keys.size.toLong()
                if (charCount > MAX_CHAR_COUNT) return emptyList()
                val anchorsCode: Long = keys.anchorNormalized.conjoinedCode
                val limitValue: Long = limit?.toLong() ?: 100L
                val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM lexicon_core WHERE anchors = $anchorsCode AND char_count = $charCount ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val number = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val romanization = cursor.getString(2)
                                val instance = Lexicon(text = word, romanization = romanization, input = inputText, mark = inputText, number = number)
                                items.add(instance)
                        }
                }
                return items
        }

        fun spellMatch(keys: List<VirtualInputKey>, complexity: Long, input: String? = null, mark: String? = null, limit: Int? = null): List<Lexicon> {
                val spell: Long = keys.conjoinedCode
                val complexityValue: Long = complexity
                val limitValue: Long = limit?.toLong() ?: -1L
                val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val items: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, romanization FROM lexicon_core WHERE spell = $spell AND complexity = $complexityValue ORDER BY rowid LIMIT $limitValue;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val number = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val romanization = cursor.getString(2)
                                val markText: String = mark ?: romanization.strippedTones()
                                val instance = Lexicon(text = word, romanization = romanization, input = inputText, mark = markText, number = number)
                                items.add(instance)
                        }
                }
                return items
        }
}

private fun List<VirtualInputKey>.splitApostropheSeparated(): List<List<VirtualInputKey>> {
        val parts: MutableList<List<VirtualInputKey>> = mutableListOf()
        var current: MutableList<VirtualInputKey> = mutableListOf()
        for (key in this) {
                if (key.isApostrophe) {
                        if (current.isNotEmpty()) {
                                parts.add(current)
                                current = mutableListOf()
                        }
                } else {
                        current.add(key)
                }
        }
        if (current.isNotEmpty()) parts.add(current)
        return parts
}
