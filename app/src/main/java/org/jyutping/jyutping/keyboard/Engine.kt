package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.anchorsCode
import org.jyutping.jyutping.extensions.isApostrophe
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Engine {
        fun suggest(origin: String, text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, asap: Boolean): List<Candidate> {
                return when (text.length) {
                        0 -> emptyList()
                        1 -> when (text) {
                                "a" -> db.pingMatch(text = text, input = text) + db.pingMatch(text = "aa", input = text, mark = text) + db.shortcutMatch(text = text, limit = 100)
                                "o", "m", "e" -> db.pingMatch(text = text, input = text) + db.shortcutMatch(text = text, limit = 100)
                                else -> db.shortcutMatch(text = text, limit = 100)
                        }
                        else -> {
                                val textMarkCandidates = db.fetchTextMarks(origin)
                                textMarkCandidates + if (asap) {
                                        if (segmentation.maxSchemeLength() > 0) {
                                                val candidates = query(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                                                candidates.ifEmpty { processVerbatim(text = text, db = db) }
                                        } else {
                                                processVerbatim(text = text, db = db)
                                        }
                                } else {
                                        dispatch(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                                }
                        }
                }
        }
        private fun dispatch(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean): List<Candidate> {
                val hasSeparators: Boolean = text.contains(PresetCharacter.APOSTROPHE)
                val hasTones: Boolean = text.contains(Regex("[1-6]"))
                return when {
                        hasSeparators && hasTones -> {
                                val syllable = text.filter { it.isLetter() }
                                db.pingMatch(text = syllable, input = text, mark = text).filter { text.startsWith(it.romanization) }
                        }
                        !hasSeparators && hasTones -> processWithTones(text, segmentation, db)
                        hasSeparators && !hasTones -> processWithSeparators(text, segmentation, db)
                        else -> {
                                if (segmentation.maxSchemeLength() < 1) {
                                        processVerbatim(text, db)
                                } else {
                                        process(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                                }
                        }
                }
        }
        private fun processWithTones(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val textTones = text.filter { it.isDigit() }
                val textToneCount = textTones.length
                val rawText: String = text.filterNot { it.isDigit() }
                val candidates = query(text = rawText, segmentation = segmentation, db = db, needsSymbols = false)
                val qualified: MutableList<Candidate> = mutableListOf()
                for (item in candidates) {
                        val continuous = item.romanization.filterNot { it.isWhitespace() }
                        val continuousTones = continuous.filter { it.isDigit() }
                        val continuousToneCount = continuousTones.length
                        when {
                                textToneCount == 1 && continuousToneCount == 1 -> {
                                        if (textTones != continuousTones) continue
                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                        if (!isCorrectPosition) continue
                                        val combinedInput = item.input + textTones
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                                textToneCount == 1 && continuousToneCount == 2 -> {
                                        val isToneLast: Boolean = text.lastOrNull()?.isDigit() == true
                                        if (isToneLast) {
                                                if (!(continuousTones.endsWith(textTones))) continue
                                                val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                                if (!isCorrectPosition) continue
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text)
                                                qualified.add(newItem)
                                        } else {
                                                if (!continuous.startsWith(text)) continue
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text)
                                                qualified.add(newItem)
                                        }
                                }
                                textToneCount == 2 && continuousToneCount == 1 -> {
                                        if (!(textTones.startsWith(continuousTones))) continue
                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isDigit() == true
                                        if (!isCorrectPosition) continue
                                        val combinedInput = item.input + continuousTones
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                                textToneCount == 2 && continuousToneCount == 2 -> {
                                        if (textTones != continuousTones) continue
                                        val isLastTone: Boolean = text.lastOrNull()?.isDigit() == true
                                        if (isLastTone) {
                                                if (item.inputCount != (text.length - 2)) continue
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text, mark = text)
                                                qualified.add(newItem)
                                        } else {
                                                val tail = text.drop(item.inputCount + 1)
                                                val isCorrectPosition: Boolean = tail.firstOrNull() == textTones.lastOrNull()
                                                if (!isCorrectPosition) continue
                                                val combinedInput = item.input + textTones
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput, mark = item.input)
                                                qualified.add(newItem)
                                        }
                                }
                                else -> {
                                        if (continuous.startsWith(text)) {
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text)
                                                qualified.add(newItem)
                                        } else if (text.startsWith(continuous)) {
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = continuous, mark = item.input)
                                                qualified.add(newItem)
                                        } else {
                                                continue
                                        }
                                }
                        }
                }
                return qualified
        }
        private fun processWithSeparators(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val separatorCount = text.count { it.isApostrophe }
                val textParts = text.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
                val isHeadingSeparator: Boolean = text.firstOrNull()?.isApostrophe ?: false
                val isTrailingSeparator: Boolean = text.lastOrNull()?.isApostrophe ?: false
                val rawText = text.filterNot { it.isApostrophe }
                val candidates = query(text = rawText, segmentation = segmentation, db = db, needsSymbols = false)
                val qualified: MutableList<Candidate> = mutableListOf()
                for (item in candidates) {
                        val syllables = item.romanization.filterNot { it.isDigit() }.split(' ')
                        if (syllables == textParts) {
                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text)
                                qualified.add(newItem)
                                continue
                        }
                        if (isHeadingSeparator) continue
                        when {
                                separatorCount == 1 && isTrailingSeparator -> {
                                        if (syllables.size != 1) continue
                                        val isLengthNotMatched: Boolean = item.inputCount != (text.length - 1)
                                        if (isLengthNotMatched) continue
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = text, mark = text)
                                        qualified.add(newItem)
                                }
                                separatorCount == 1 -> {
                                        when (syllables.size) {
                                                1 -> {
                                                        if (item.input != textParts.firstOrNull()) continue
                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                                        qualified.add(newItem)
                                                }
                                                2 -> {
                                                        if (syllables.firstOrNull() != textParts.firstOrNull()) continue
                                                        val combinedInput: String = item.input + PresetCharacter.APOSTROPHE
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
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
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                                        qualified.add(newItem)
                                                }
                                                2 -> {
                                                        val isLengthNotMatched: Boolean = item.inputCount != (text.length - 2)
                                                        if (isLengthNotMatched) continue
                                                        if (syllables.firstOrNull() != textParts.firstOrNull()) continue
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = text)
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
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                        }
                }
                if (qualified.isNotEmpty()) return qualified
                val anchors = textParts.mapNotNull { it.firstOrNull() }
                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                return db.shortcutMatch(anchorText)
                        .filter { item ->
                                val syllables = item.romanization.filterNot { it.isDigit() }.split(' ')
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
                                Candidate(text = it.text, romanization = it.romanization, input = text)
                        }
        }
        private fun processVerbatim(text: String, db: DatabaseHelper, limit: Int? = null): List<Candidate> {
                val anchorLimit: Int = if (limit == null) 250 else 100
                val rounds: MutableList<List<Candidate>> = mutableListOf()
                for (number in text.indices) {
                        val leading = text.dropLast(number)
                        val round = db.pingMatch(text = leading, input = leading, limit = limit) + db.shortcutMatch(text = leading, limit = anchorLimit)
                        rounds.add(round)
                }
                val candidates: MutableList<Candidate> = mutableListOf()
                val textLength = text.length
                for (item in rounds.flatten()) {
                        val syllables = item.romanization.filterNot { it.isDigit() }.split(PresetCharacter.SPACE)
                        val lastSyllable: String = syllables.lastOrNull() ?: continue
                        if (text.endsWith(lastSyllable).not()) {
                                candidates.add(item)
                                continue
                        }
                        val isMatched: Boolean = ((syllables.count() - 1) + lastSyllable.count()) == textLength
                        if (isMatched.not()) {
                                candidates.add(item)
                                continue
                        }
                        val mark = syllables.mapNotNull { it.firstOrNull() }.dropLast(1).joinToString(separator = PresetString.SPACE) + PresetString.SPACE + lastSyllable
                        val instance = Candidate(text = item.text, romanization = item.romanization, input = text, mark = mark, order = item.order)
                        candidates.add(instance)
                }
                return candidates.sorted()
        }
        private fun process(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val primary = query(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                val firstInputLength = primary.firstOrNull()?.input?.length ?: 0
                if (firstInputLength == 0) return processVerbatim(text, db, limit)
                if (firstInputLength == textLength) return primary
                val prefixes: List<Candidate> = if (segmentation.maxSchemeLength() >= textLength) emptyList() else {
                        val anchorLimit: Int = if (limit == null) 400 else 100
                        val shortcuts: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val tail = text.drop(scheme.length())
                                val lastAnchor = tail.firstOrNull() ?: continue
                                val schemeAnchors: String = scheme.mapNotNull { it.text.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                                val conjoined: String = schemeAnchors + tail
                                val anchors: String = schemeAnchors + lastAnchor
                                val schemeMark: String = scheme.joinToString(separator = PresetString.SPACE) { it.text }
                                val spacedMark: String = schemeMark + PresetString.SPACE + tail.toList().joinToString(separator = PresetString.SPACE)
                                val anchorMark: String = schemeMark + PresetString.SPACE + tail
                                val conjoinedShortcuts = db.shortcutMatch(conjoined, anchorLimit)
                                        .filter { item ->
                                                val rawRomanization = item.romanization.filterNot { it.isDigit() }
                                                rawRomanization.startsWith(schemeMark) && run {
                                                        val tailAnchors = rawRomanization.drop(schemeMark.length).split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                                                        tailAnchors == tail
                                                }
                                        }
                                        .map { Candidate(text = it.text, romanization = it.romanization, input = text, mark = spacedMark, order = it.order) }
                                shortcuts.add(conjoinedShortcuts)
                                val anchorShortcuts = db.shortcutMatch(anchors, anchorLimit)
                                        .filter { item -> item.romanization.filterNot { it.isDigit() }.startsWith(anchorMark) }
                                        .map { Candidate(text = it.text, romanization = it.romanization, input = text, mark = anchorMark, order = it.order) }
                                shortcuts.add(anchorShortcuts)
                        }
                        shortcuts.flatten()
                }
                if (prefixes.isNotEmpty()) return prefixes + primary
                val headTexts = primary.map { it.input }.distinct()
                val concatenated: MutableList<Candidate> = mutableListOf()
                for (headText in headTexts) {
                        val headInputLength = headText.length
                        val tailText = text.drop(headInputLength)
                        if (db.canProcess(tailText).not()) continue
                        val tailSegmentation = Segmentor.segment(tailText, db)
                        val tailCandidate = process(text = tailText, segmentation = tailSegmentation, db = db, needsSymbols = false, limit = 50).minOrNull()
                        if (tailCandidate == null) continue
                        val headCandidate = primary.takeWhile { it.input == headText }.minOrNull()
                        if (headCandidate == null) continue
                        val conjoined = headCandidate + tailCandidate
                        concatenated.add(conjoined)
                }
                val preferredConcatenated = concatenated.distinct().sorted().take(1)
                return preferredConcatenated + primary
        }
        private fun query(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val fullMatch = db.pingMatch(text = text, input = text, limit = limit)
                val fullShortcut = db.shortcutMatch(text, limit)
                val searches = search(text, segmentation, db, limit)
                val queried = ordered(textLength, (fullMatch + fullShortcut + searches))
                val shouldNotContinue: Boolean = !needsSymbols || (limit != null) || queried.isEmpty()
                if (shouldNotContinue) return queried
                val symbols: List<Candidate> = searchSymbols(text = text, segmentation = segmentation, db = db)
                if (symbols.isEmpty()) return queried
                val combined = queried.toMutableList()
                for (symbol in symbols.reversed()) {
                        val index = combined.indexOfFirst { it.lexiconText == symbol.lexiconText && it.romanization == symbol.romanization }
                        if (index != -1) {
                                combined.add(index = index + 1, element = symbol)
                        }
                }
                return combined
        }
        private fun search(text: String, segmentation: Segmentation, db: DatabaseHelper, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val perfectSchemes = segmentation.filter { it.length() == textLength }
                if (perfectSchemes.isNotEmpty()) {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in perfectSchemes) {
                                for (number in scheme.indices) {
                                        val slice = scheme.dropLast(number)
                                        val shortcutCode = slice.mapNotNull { it.text.firstOrNull() }.anchorsCode()
                                        if (shortcutCode == null) continue
                                        val pingCode = slice.joinToString(separator = PresetString.EMPTY) { it.origin }.hashCode()
                                        val input = slice.joinToString(separator = PresetString.EMPTY) { it.text }
                                        val mark = slice.joinToString(separator = PresetString.SPACE) { it.text }
                                        val matched = db.strictMatch(shortcut = shortcutCode, spell = pingCode, input = input, mark = mark, limit = limit)
                                        matches.add(matched)
                                }
                        }
                        return matches.flatten()
                } else {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val shortcutCode = scheme.mapNotNull { it.text.firstOrNull() }.anchorsCode()
                                if (shortcutCode == null) continue
                                val pingCode = scheme.joinToString(separator = PresetString.EMPTY) { it.origin }.hashCode()
                                val input = scheme.joinToString(separator = PresetString.EMPTY) { it.text }
                                val mark = scheme.joinToString(separator = PresetString.SPACE) { it.text }
                                val matched = db.strictMatch(shortcut = shortcutCode, spell = pingCode, input = input, mark = mark, limit = limit)
                                matches.add(matched)
                        }
                        return matches.flatten()
                }
        }
        private fun ordered(textLength: Int, candidates: List<Candidate>): List<Candidate> {
                val matched = candidates.filter { it.inputCount == textLength }.sortedBy { it.order }.distinct()
                val others = candidates.filter { it.inputCount != textLength }.distinct().sorted()
                val primary = matched.take(15)
                val secondary = others.take(10)
                val tertiary = others.sortedBy { it.order }.take(7)
                return (primary + secondary + tertiary + matched + others).distinct()
        }
        private fun searchSymbols(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val regular = db.symbolMatch(text = text, input = text)
                val textLength = text.length
                val schemes = segmentation.filter { it.length() == textLength }
                if (schemes.isEmpty()) return regular
                val matches: MutableList<List<Candidate>> = mutableListOf()
                for (scheme in schemes) {
                        val pingText = scheme.joinToString(separator = PresetString.EMPTY) { it.origin }
                        val matched = db.symbolMatch(text = pingText, input = text)
                        matches.add(matched)
                }
                return (regular + matches.flatten()).distinct()
        }
}
