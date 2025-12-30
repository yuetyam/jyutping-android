package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.isApostrophe
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Researcher {
        fun suggest(keys: List<VirtualInputKey>, segmentation: NewSegmentation, db: DatabaseHelper): List<Lexicon> {
                when (keys.size) {
                        0 -> return emptyList()
                        1 -> when (keys.first()) {
                                VirtualInputKey.letterA -> {
                                        val text = VirtualInputKey.letterA.text
                                        return db.spellMatch(text = text, input = text, mark = text) +
                                                db.spellMatch(text = text + text, input = text, mark = text) +
                                                db.anchorsMatch(keys = keys, input = text)
                                }
                                VirtualInputKey.letterO, VirtualInputKey.letterM -> {
                                        val text = keys.first().text
                                        return db.spellMatch(text = text, input = text, mark = text) +
                                                db.anchorsMatch(keys = keys, input = text)
                                }
                                else -> return db.anchorsMatch(keys = keys)
                        }
                        else -> return dispatch(keys = keys, segmentation = segmentation, db = db)
                }
        }
}
private fun Researcher.dispatch(keys: List<VirtualInputKey>, segmentation: NewSegmentation, db: DatabaseHelper): List<Lexicon> {
        val syllableKeys = keys.filter { it.isSyllableLetter }
        val firstSyllableLength = segmentation.firstOrNull()?.firstOrNull()?.alias?.size ?: 0
        val lexicons: List<Lexicon> = when {
                (firstSyllableLength == 0) -> {
                        val text = syllableKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                        processSlices(keys = syllableKeys, text = text, db = db)
                }
                (firstSyllableLength == 1 && syllableKeys.size > 1) || (syllableKeys.size != keys.size) -> {
                        val text = syllableKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                        search(keys = syllableKeys, segmentation = segmentation, db = db) + processSlices(keys = syllableKeys, text = text, db = db)
                }
                else -> search(keys = syllableKeys, segmentation = segmentation, db = db)
        }
        val hasApostrophes = keys.any { it.isApostrophe }
        val hasTones = keys.any { it.isToneEvent }
        return when {
                hasApostrophes && hasTones -> {
                        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        val convertedText = text.toneConverted()
                        lexicons.mapNotNull {
                                if (convertedText.startsWith(it.romanization)) Lexicon(text = it.text, romanization = it.romanization, input = text) else null
                        }
                }
                !hasApostrophes && hasTones -> {
                        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        oldProcessWithTones(text = text.toneConverted(), lexicons = lexicons)
                }
                hasApostrophes && !hasTones -> {
                        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        oldProcessWithSeparators(text = text, lexicons = lexicons, db = db)
                }
                else -> lexicons
        }
}

private fun Researcher.oldProcessWithTones(text: String, lexicons: List<Lexicon>): List<Lexicon> {
        val textTones = text.filter { it.isCantoneseToneDigit }
        val textToneCount = textTones.length
        val qualified: MutableList<Lexicon> = mutableListOf()
        for (item in lexicons) {
                val continuous = item.romanization.filterNot { it.isSpace }
                val continuousTones = continuous.filter { it.isCantoneseToneDigit }
                val continuousToneCount = continuousTones.length
                when {
                        textToneCount == 1 && continuousToneCount == 1 -> {
                                if (textTones != continuousTones) continue
                                val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                if (!isCorrectPosition) continue
                                val combinedInput = item.input + textTones
                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                qualified.add(newItem)
                        }
                        textToneCount == 1 && continuousToneCount == 2 -> {
                                val isToneLast: Boolean = text.lastOrNull()?.isDigit() == true
                                if (isToneLast) {
                                        if (!(continuousTones.endsWith(textTones))) continue
                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                        if (!isCorrectPosition) continue
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text)
                                        qualified.add(newItem)
                                } else {
                                        if (!continuous.startsWith(text)) continue
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text)
                                        qualified.add(newItem)
                                }
                        }
                        textToneCount == 2 && continuousToneCount == 1 -> {
                                if (!(textTones.startsWith(continuousTones))) continue
                                val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                if (!isCorrectPosition) continue
                                val combinedInput = item.input + continuousTones
                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                qualified.add(newItem)
                        }
                        textToneCount == 2 && continuousToneCount == 2 -> {
                                if (textTones != continuousTones) continue
                                val isLastTone: Boolean = text.lastOrNull()?.isDigit() == true
                                if (isLastTone) {
                                        if (item.inputCount != (text.length - 2)) continue
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text)
                                        qualified.add(newItem)
                                } else {
                                        val tail = text.drop(item.inputCount + 1)
                                        val isCorrectPosition: Boolean = tail.firstOrNull() == textTones.lastOrNull()
                                        if (!isCorrectPosition) continue
                                        val combinedInput = item.input + textTones
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput, mark = item.input)
                                        qualified.add(newItem)
                                }
                        }
                        else -> {
                                if (continuous.startsWith(text)) {
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text)
                                        qualified.add(newItem)
                                } else if (text.startsWith(continuous)) {
                                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = continuous, mark = item.input)
                                        qualified.add(newItem)
                                } else {
                                        continue
                                }
                        }
                }
        }
        return qualified
}

private fun Researcher.oldProcessWithSeparators(text: String, lexicons: List<Lexicon>, db: DatabaseHelper): List<Lexicon> {
        val separatorCount = text.count { it.isApostrophe }
        val textParts = text.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
        val isHeadingSeparator: Boolean = text.firstOrNull()?.isApostrophe ?: false
        val isTrailingSeparator: Boolean = text.lastOrNull()?.isApostrophe ?: false
        val qualified: MutableList<Lexicon> = mutableListOf()
        for (item in lexicons) {
                val syllables = item.romanization.filterNot { it.isCantoneseToneDigit }.split(PresetString.SPACE)
                if (syllables == textParts) {
                        val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text)
                        qualified.add(newItem)
                        continue
                }
                if (isHeadingSeparator) continue
                when {
                        separatorCount == 1 && isTrailingSeparator -> {
                                if (syllables.size != 1) continue
                                val isLengthNotMatched: Boolean = item.inputCount != (text.length - 1)
                                if (isLengthNotMatched) continue
                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text)
                                qualified.add(newItem)
                        }
                        separatorCount == 1 -> {
                                when (syllables.size) {
                                        1 -> {
                                                if (item.input != textParts.firstOrNull()) continue
                                                val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                                qualified.add(newItem)
                                        }
                                        2 -> {
                                                if (syllables.firstOrNull() != textParts.firstOrNull()) continue
                                                val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                                qualified.add(newItem)
                                        }
                                        else -> continue
                                }
                        }
                        separatorCount == 2 && isTrailingSeparator -> {
                                when (syllables.size) {
                                        1 -> {
                                                if (item.input != textParts.firstOrNull()) continue
                                                val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                                qualified.add(newItem)
                                        }
                                        2 -> {
                                                val isLengthNotMatched: Boolean = item.inputCount != (text.length - 2)
                                                if (isLengthNotMatched) continue
                                                if (syllables.firstOrNull() != textParts.firstOrNull()) continue
                                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = text)
                                                qualified.add(newItem)
                                        }
                                        else -> continue
                                }
                        }
                        else -> {
                                if (syllables.size >= textParts.size) continue
                                val checks = syllables.indices.map { syllables[it] == textParts[it] }
                                val isMatched = checks.fold(true) { acc, b -> acc && b }
                                if (!isMatched) continue
                                val separatorNumber = syllables.size - 1
                                val tail = CharArray(separatorNumber) { 'i' }.concatToString()
                                val combinedInput = item.input + tail
                                val newItem = Lexicon(text = item.text, romanization = item.romanization, input = combinedInput)
                                qualified.add(newItem)
                        }
                }
        }
        if (qualified.isNotEmpty()) return qualified
        val anchors = textParts.mapNotNull { it.firstOrNull() }
        val anchorKeys = anchors.mapNotNull { VirtualInputKey.matchVirtualInputKey(it) }
        return db.anchorsMatch(keys = anchorKeys)
                .filter { item ->
                        val syllables = item.romanization.filterNot { it.isCantoneseToneDigit }.split(PresetString.SPACE)
                        if (syllables.size != anchors.size) {
                                false
                        } else {
                                val checks = anchors.indices.map { index ->
                                        val part = textParts[index]
                                        val isAnchorOnly = (part.length == 1)
                                        if (isAnchorOnly) syllables[index].startsWith(part) else syllables[index] == part
                                }
                                checks.fold(true) { acc, b -> acc && b }
                        }
                }
                .map {
                        Lexicon(text = it.text, romanization = it.romanization, input = text)
                }
}

private fun Researcher.search(keys: List<VirtualInputKey>, segmentation: NewSegmentation, limit: Int? = null, db: DatabaseHelper): List<Lexicon> {
        val inputLength = keys.size
        val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val spellMatched = db.spellMatch(text = text, input = text, limit = limit)
        val anchorsMatched = db.anchorsMatch(keys = keys, limit = limit)
        val queried = query(inputLength = inputLength, segmentation = segmentation, limit = limit, db = db)
        val shouldMatchPrefixes: Boolean = if (inputLength <= 2) {
                false
        } else if (spellMatched.isNotEmpty()) {
                false
        } else if (queried.any { it.inputCount == inputLength }) {
                false
        } else if (keys.firstOrNull() == VirtualInputKey.letterM || keys.lastOrNull() == VirtualInputKey.letterM) {
                true
        } else {
                segmentation.any { it.length == inputLength }.negative
        }
        val prefixesLimit: Int = if (limit == null) 500 else 200
        val prefixMatched: List<Lexicon> = if (shouldMatchPrefixes.negative) emptyList() else segmentation.flatMap { scheme ->
                if (scheme.isEmpty()) return@flatMap emptyList()
                val tail = keys.drop(scheme.length)
                val lastAnchor = tail.firstOrNull() ?: return@flatMap emptyList()
                val schemeAnchors = scheme.aliasAnchors
                val conjoined = schemeAnchors + tail
                val anchors = schemeAnchors + lastAnchor
                val schemeSyllableText = scheme.syllableText
                val mark: String = scheme.mark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                val tailAsAnchorText = tail.mapNotNull { if (it == VirtualInputKey.letterY) VirtualInputKey.letterJ.text.firstOrNull() else it.text.firstOrNull() }
                val conjoinedMatched = db.anchorsMatch(keys = conjoined, limit = prefixesLimit)
                        .mapNotNull { item ->
                                val toneFreeRomanization = item.romanization.filterNot { it.isCantoneseToneDigit }
                                if (toneFreeRomanization.startsWith(schemeSyllableText).negative) return@mapNotNull null
                                val suffixAnchorText = toneFreeRomanization.drop(schemeSyllableText.length).split(PresetString.SPACE).mapNotNull { it.firstOrNull() }
                                if (suffixAnchorText != tailAsAnchorText) return@mapNotNull null
                                return@mapNotNull Lexicon(text = item.text, romanization = item.romanization, input = text, mark = mark, order = item.order)
                        }
                val modifiedTail = if (tail.firstOrNull() == VirtualInputKey.letterY) (listOf(VirtualInputKey.letterJ) + tail.drop(1)) else tail
                val transformedTailText: String = modifiedTail.joinToString(separator = PresetString.EMPTY) { it.text }
                val syllables: String = schemeSyllableText + PresetString.SPACE + transformedTailText
                val anchorsMatched = db.anchorsMatch(keys = anchors, limit = prefixesLimit)
                        .mapNotNull { item ->
                                val toneFreeRomanization = item.romanization.filterNot { it.isCantoneseToneDigit }
                                if (toneFreeRomanization.startsWith(syllables).negative) return@mapNotNull null
                                return@mapNotNull Lexicon(text = item.text, romanization = item.romanization, input = text, mark = mark, order = item.order)
                        }
                return@flatMap conjoinedMatched + anchorsMatched
        }
        val gainedMatched: List<Lexicon> = if (shouldMatchPrefixes.negative) emptyList() else 1.rangeUntil(inputLength).reversed().flatMap { number ->
                val leadingKeys = keys.take(number)
                val leadingText = leadingKeys.joinToString(separator = PresetString.EMPTY) { it.text }
                return@flatMap db.anchorsMatch(keys = leadingKeys, input = leadingText, limit = 300)
        }.mapNotNull { item ->
                val tail = keys.drop(item.inputCount - 1)
                if (tail.size > 6) return@mapNotNull null
                val converted = Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text, order = item.order)
                val rawSyllable = item.romanization.filter { it.isLowercaseBasicLatinLetter }
                if (rawSyllable.startsWith(text)) return@mapNotNull converted
                val lastSyllable = item.romanization.split(PresetString.SPACE).lastOrNull()?.filter { it.isCantoneseToneDigit.negative } ?: return@mapNotNull null
                val tailSyllable = Segmenter.syllableText(keys = tail, db = db)
                if (tailSyllable != null) {
                        return@mapNotNull if (lastSyllable == tailSyllable) converted else null
                } else {
                        val tailText = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        return@mapNotNull if (lastSyllable.startsWith(tailText)) converted else null
                }
        }
        val fetched: List<Lexicon> = run {
                val idealQueried = queried.filter { it.inputCount == inputLength }.sortedBy { it.order }.distinct()
                val notIdealQueried = queried.filter { it.inputCount < inputLength }.sorted().distinct()
                val fullInput = (spellMatched + idealQueried + anchorsMatched + prefixMatched + gainedMatched).distinct()
                val primary = fullInput.take(10)
                val secondary = fullInput.sorted().take(10)
                val tertiary = notIdealQueried.take(10)
                val quaternary = notIdealQueried.sortedBy { it.order }.take(10)
                (primary + secondary + tertiary + quaternary + fullInput + notIdealQueried).distinct()
        }
        val firstInputCount = fetched.firstOrNull()?.inputCount ?: return processSlices(keys = keys, text = text, limit = limit, db = db)
        if (firstInputCount == inputLength) return fetched
        val headInputLengths = fetched.map { it.inputCount }.distinct()
        val concatenated: List<Lexicon> = headInputLengths.mapNotNull { headLength ->
                val tailKeys = keys.drop(headLength)
                val tailSegmentation = Segmenter.segment(tailKeys, db)
                val tailLexicon = search(keys = tailKeys, segmentation = tailSegmentation, limit = 50, db = db).firstOrNull() ?: return@mapNotNull null
                val headLexicon = fetched.firstOrNull { it.inputCount == headLength } ?: return@mapNotNull null
                return@mapNotNull headLexicon + tailLexicon
        }.distinct().sorted().take(1)
        return concatenated + fetched
}

private fun Researcher.query(inputLength: Int, segmentation: NewSegmentation, limit: Int? = null, db: DatabaseHelper): List<Lexicon> {
        val idealSchemes = segmentation.filter { it.length == inputLength }
        if (idealSchemes.isEmpty()) {
                return segmentation.flatMap { performQuery(scheme = it, limit = limit, db = db) }
        } else {
                return idealSchemes.flatMap { scheme ->
                        return when (scheme.size) {
                                0 -> emptyList()
                                1 -> performQuery(scheme = scheme, limit = limit, db = db)
                                else -> {
                                        scheme.size.downTo(1).map { scheme.take(it) }.flatMap { performQuery(scheme = it, limit = limit, db = db) }
                                }
                        }
                }
        }
}
private fun Researcher.performQuery(scheme: Scheme, limit: Int? = null, db: DatabaseHelper): List<Lexicon> {
        val anchorsCode = scheme.aliasAnchors.anchorsCode()
        if (anchorsCode < 1) return emptyList()
        val spellCode = scheme.originText.hashCode()
        return db.strictMatch(anchors = anchorsCode, spell = spellCode, input = scheme.aliasText, mark = scheme.mark, limit = limit)
}

private fun Researcher.processSlices(keys: List<VirtualInputKey>, text: String, limit: Int? = null, db: DatabaseHelper): List<Lexicon> {
        val adjustedLimit: Int = if (limit == null) 300 else 100
        val inputLength: Int = keys.size
        val entries: List<Lexicon> = 0.rangeUntil(inputLength).flatMap { number ->
                val leadingKeys = keys.dropLast(number)
                val leadingText = leadingKeys.joinToString(separator =  PresetString.EMPTY) { it.text }
                val spellMatched = db.spellMatch(text = leadingText, input = leadingText, limit = limit)
                        .map { modify(item = it, keys = keys, text = text, inputLength = inputLength, db = db) }
                val anchorsMatched = db.anchorsMatch(keys = leadingKeys, input = leadingText, limit = adjustedLimit)
                        .map { modify(item = it, keys = keys, text = text, inputLength = inputLength, db = db) }
                        .sorted()
                        .take(72)
                return spellMatched + anchorsMatched
        }
        return entries.distinct().sorted()
}
private fun Researcher.modify(item: Lexicon, keys: List<VirtualInputKey>, text: String, inputLength: Int, db: DatabaseHelper): Lexicon {
        if (inputLength <= 1) return item
        if (item.inputCount != inputLength) return item
        val converted = Lexicon(text = item.text, romanization = item.romanization, input = text, mark = text, order = item.order)
        if (item.romanization.filter { it.isLowercaseBasicLatinLetter }.startsWith(text)) return converted
        val lastSyllable = item.romanization.split(PresetString.SPACE).lastOrNull()?.filterNot { it.isCantoneseToneDigit } ?: return item
        val tail = keys.drop(item.inputCount - 1)
        if (tail.size > 6) return item
        val tailSyllable = Segmenter.syllableText(keys = tail, db = db)
        if (tailSyllable != null) {
                return if (lastSyllable == tailSyllable) converted else item
        } else {
                val tailText = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                return if (lastSyllable.startsWith(tailText)) converted else item
        }
}

private fun DatabaseHelper.anchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<Lexicon> {
        val code = keys.anchorsCode()
        if (code == 0L) return emptyList()
        val inputText: String = input ?: keys.joinToString(PresetString.EMPTY) { it.text }
        val instances: MutableList<Lexicon> = mutableListOf()
        val limitValue: Int = limit ?: 100
        val command = "SELECT rowid, word, romanization FROM core_lexicon WHERE anchors = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val order = cursor.getInt(0)
                val word = cursor.getString(1)
                val romanization = cursor.getString(2)
                val instance = Lexicon(text = word, romanization = romanization, input = inputText, mark = inputText, order = order)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun DatabaseHelper.spellMatch(text: String, input: String, mark: String? = null, limit: Int? = null): List<Lexicon> {
        if (text.isBlank()) return emptyList()
        val spellCode: Int = text.hashCode()
        val instances: MutableList<Lexicon> = mutableListOf()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM core_lexicon WHERE spell = $spellCode LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val order = cursor.getInt(0)
                val word = cursor.getString(1)
                val romanization = cursor.getString(2)
                val markText = mark ?: romanization.filterNot { it.isDigit() }
                val instance = Lexicon(text = word, romanization = romanization, input = input, mark = markText, order = order)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun DatabaseHelper.strictMatch(anchors: Long, spell: Int, input: String, mark: String? = null, limit: Int? = null): List<Lexicon> {
        if (anchors == 0L) return emptyList()
        val instances: MutableList<Lexicon> = mutableListOf()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM core_lexicon WHERE spell = $spell AND anchors = $anchors LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val order = cursor.getInt(0)
                val word = cursor.getString(1)
                val romanization = cursor.getString(2)
                val markText = mark ?: romanization.filterNot { it.isDigit() }
                val instance = Lexicon(text = word, romanization = romanization, input = input, mark = markText, order = order)
                instances.add(instance)
        }
        cursor.close()
        return instances
}

fun DatabaseHelper.searchSymbols(text: String, segmentation: NewSegmentation): List<Lexicon> {
        val fullMatched = symbolMatch(text = text, input = text)
        val textLength = text.length
        val schemes = segmentation.filter { it.length == textLength }
        if (schemes.isEmpty()) return fullMatched
        val matches = schemes.flatMap { symbolMatch(text = it.originText, input = text) }
        return fullMatched + matches
}
