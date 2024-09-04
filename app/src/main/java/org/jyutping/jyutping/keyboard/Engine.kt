package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.isSeparatorChar
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Engine {
        fun suggest(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, asap: Boolean): List<Candidate> {
                return when (text.length) {
                        0 -> emptyList()
                        1 -> when (text) {
                                "a" -> db.match(text, input = text) + db.match(text = "aa", input = text) + db.shortcut(text)
                                "o", "m", "e" -> db.match(text = text, input = text) + db.shortcut(text)
                                else -> db.shortcut(text)
                        }
                        else -> {
                                if (asap) {
                                        if (segmentation.maxSchemeLength() > 0) {
                                                val candidates = query(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                                                if (candidates.isEmpty()) processVerbatim(text = text, db = db) else candidates
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
                val hasSeparators: Boolean = text.contains(PresetCharacter.SEPARATOR)
                val hasTones: Boolean = text.contains(Regex("[1-6]"))
                return when {
                        hasSeparators && hasTones -> {
                                val syllable = text.filter { it.isLetter() }
                                db.match(text = syllable, input = text, mark = text).filter { text.startsWith(it.romanization) }
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
                val candidates= query(text = rawText, segmentation = segmentation, db = db, needsSymbols = false)
                val qualified: MutableList<Candidate> = mutableListOf()
                for (item in candidates) {
                        val continuous = item.romanization.filterNot { it.isWhitespace() }
                        val continuousTones = continuous.filter { it.isDigit() }
                        val continuousToneCount = continuousTones.length
                        when {
                                textToneCount == 1 && continuousToneCount == 1 -> {
                                        if (textTones != continuousTones) continue
                                        val isCorrectPosition: Boolean = text.drop(item.input.length).firstOrNull()?.isDigit() ?: false
                                        if (!isCorrectPosition) continue
                                        val combinedInput = item.input + textTones
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                                textToneCount == 1 && continuousToneCount == 2 -> {
                                        val isToneLast: Boolean = text.lastOrNull()?.isDigit() ?: false
                                        if (isToneLast) {
                                                if (!(continuousTones.endsWith(textTones))) continue
                                                val isCorrectPosition: Boolean = text.drop(item.input.length).firstOrNull()?.isDigit() ?: false
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
                                        val isCorrectPosition: Boolean = text.drop(item.input.length).firstOrNull()?.isDigit() ?: false
                                        if (!isCorrectPosition) continue
                                        val combinedInput = item.input + continuousTones
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                                textToneCount == 2 && continuousToneCount == 2 -> {
                                        if (textTones != continuousTones) continue
                                        val isLastTone: Boolean = text.lastOrNull()?.isDigit() ?: false
                                        if (isLastTone) {
                                                if (item.input.length != (text.length - 2)) continue
                                                val newItem = Candidate(text = item.text, romanization = item.romanization, input = text, mark = text)
                                                qualified.add(newItem)
                                        } else {
                                                val tail = text.drop(item.input.length + 1)
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
                val separatorCount = text.count { it.isSeparatorChar() }
                val textParts = text.split(PresetCharacter.SEPARATOR).filter { it.isNotEmpty() }
                val isHeadingSeparator: Boolean = text.firstOrNull()?.isSeparatorChar() ?: false
                val isTrailingSeparator: Boolean = text.lastOrNull()?.isSeparatorChar() ?: false
                val rawText = text.filter { !(it.isSeparatorChar()) }
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
                                        val isLengthNotMatched: Boolean = item.input.length != (text.length - 1)
                                        if (isLengthNotMatched) continue
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = text, mark = text)
                                        qualified.add(newItem)
                                }
                                separatorCount == 1 -> {
                                        when (syllables.size) {
                                                1 -> {
                                                        if (item.input != textParts.firstOrNull()) continue
                                                        val combinedInput: String = item.input + PresetCharacter.SEPARATOR
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                                        qualified.add(newItem)
                                                }
                                                2 -> {
                                                        if (syllables.firstOrNull() != textParts.firstOrNull()) continue
                                                        val combinedInput: String = item.input + PresetCharacter.SEPARATOR
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
                                                        val combinedInput: String = item.input + PresetCharacter.SEPARATOR
                                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                                        qualified.add(newItem)
                                                }
                                                2 -> {
                                                        val isLengthNotMatched: Boolean = item.input.length != (text.length - 2)
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
                                        val tail = List(syllables.size - 1) { 'i' }
                                        val combinedInput = item.input + tail
                                        val newItem = Candidate(text = item.text, romanization = item.romanization, input = combinedInput)
                                        qualified.add(newItem)
                                }
                        }
                }
                if (qualified.isNotEmpty()) return qualified
                val anchors = textParts.mapNotNull { it.firstOrNull() }
                val anchorText = anchors.joinToString(separator = PresetString.EMPTY)
                return db.shortcut(anchorText)
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
                val rounds: MutableList<List<Candidate>> = mutableListOf()
                for (number in text.indices) {
                        val leading = text.dropLast(number)
                        val round = db.match(text = leading, input = leading, limit = limit) + db.shortcut(leading, limit)
                        rounds.add(round)
                }
                return rounds.flatten().distinct()
        }
        private fun process(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val primary = query(text = text, segmentation = segmentation, db = db, needsSymbols = needsSymbols)
                val firstInputLength = primary.firstOrNull()?.input?.length ?: 0
                if (firstInputLength == 0) return processVerbatim(text, db, limit)
                if (firstInputLength == textLength) return primary
                val prefixes: List<Candidate> = run {
                        if (segmentation.maxSchemeLength() >= textLength) emptyList<Candidate>()
                        val shortcuts: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val tail = text.drop(scheme.length())
                                val lastAnchor = tail.firstOrNull() ?: continue
                                val schemeAnchors = scheme.mapNotNull { it.text.firstOrNull() }
                                val anchors: String = (schemeAnchors + lastAnchor).joinToString(separator = PresetString.EMPTY)
                                val text2mark = scheme.joinToString(separator = PresetString.SPACE) { it.text } + PresetString.SPACE + tail
                                val shortcut = db.shortcut(anchors, limit)
                                        .filter { candidate -> candidate.romanization.filter { it.isDigit().not() }.startsWith(text2mark) }
                                        .map { Candidate(text = it.text, romanization = it.romanization, input = text, mark = text2mark) }
                                shortcuts.add(shortcut)
                        }
                        shortcuts.flatten()
                }
                if (prefixes.isNotEmpty()) return prefixes + primary
                val headTexts = primary.map { it.input }.distinct()
                val concatenated: MutableList<List<Candidate>> = mutableListOf()
                for (headText in headTexts) {
                        val headInputLength = headText.length
                        val tailText = text.drop(headInputLength)
                        if (db.canProcess(tailText).not()) continue
                        val tailSegmentation = Segmentor.segment(tailText, db)
                        val tailCandidates = process(text = tailText, segmentation = tailSegmentation, db = db, needsSymbols = false, limit = 8).take(100)
                        if (tailCandidates.isEmpty()) continue
                        val headCandidates = primary.takeWhile { it.input == headText }.take(8)
                        val combines = headCandidates.map { head -> tailCandidates.map { head + it } }
                        concatenated.add(combines.flatten())
                }
                val preferredConcatenated = preferred(text, concatenated.flatten().distinct()).take(1)
                return preferredConcatenated + primary
        }
        private fun query(text: String, segmentation: Segmentation, db: DatabaseHelper, needsSymbols: Boolean, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val searches = search(text, segmentation, db, limit)
                val preferredSearches = searches.filter { it.input.length == textLength }
                val matched = db.match(text = text, input = text, limit = limit)
                val shortcut = db.shortcut(text, limit)
                val fallback by lazy { (matched + preferredSearches + shortcut + searches).distinct() }
                val shouldNotContinue: Boolean = (!needsSymbols) || (limit != null) || (matched.isEmpty() && preferredSearches.isEmpty())
                if (shouldNotContinue) return fallback
                val symbols: List<Candidate> = searchSymbols(text = text, segmentation = segmentation, db = db)
                if (symbols.isEmpty()) return fallback
                val regular: MutableList<Candidate> = (matched + preferredSearches).toMutableList()
                for (symbol in symbols.reversed()) {
                        val index = regular.indexOfFirst { it.lexiconText == symbol.lexiconText }
                        if (index != -1) {
                                regular.add(index = index + 1, element = symbol)
                        }
                }
                return (regular + shortcut + searches).distinct()
        }
        private fun search(text: String, segmentation: Segmentation, db: DatabaseHelper, limit: Int? = null): List<Candidate> {
                val textLength = text.length
                val perfectSchemes = segmentation.filter { it.length() == textLength }
                if (perfectSchemes.isNotEmpty()) {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in perfectSchemes) {
                                for (number in scheme.indices) {
                                        val slice = scheme.dropLast(number)
                                        val pingText = slice.joinToString(separator = PresetString.EMPTY) { it.origin }
                                        val inputText = slice.joinToString(separator = PresetString.EMPTY) { it.text }
                                        val text2mark = slice.joinToString(separator = PresetString.SPACE) { it.text }
                                        val matched = db.match(text = pingText, input = inputText, mark = text2mark, limit = limit)
                                        matches.add(matched)
                                }
                        }
                        return ordered(textLength, matches.flatten())
                } else {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val pingText = scheme.joinToString(separator = PresetString.EMPTY) { it.origin }
                                val inputText = scheme.joinToString(separator = PresetString.EMPTY) { it.text }
                                val text2mark = scheme.joinToString(separator = PresetString.SPACE) { it.text }
                                val matched = db.match(text = pingText, input = inputText, mark = text2mark, limit = limit)
                                matches.add(matched)
                        }
                        return ordered(textLength, matches.flatten())
                }
        }
        private fun preferred(text: String, candidates: List<Candidate>): List<Candidate> {
                val sorted = candidates.sortedWith(compareBy({-it.input.length}, {-it.text.length}))
                val matched = sorted.filter { candidate -> candidate.romanization.filter { it.isLetter() } == text }
                return matched.ifEmpty { sorted }
        }
        private fun ordered(textLength: Int, candidates: List<Candidate>): List<Candidate> {
                val perfectCandidates = candidates.filter { it.input.length == textLength }.sortedBy { it.order }
                // val imperfectCandidates = candidates.filter { it.input.length != textLength }.sortedBy { -(it.input.length) }
                val leadingCandidates = candidates.filter { it.order <= 40000 && it.input.length != textLength }.sortedBy { -(it.input.length) }
                val trailingCandidates = candidates.filter { it.order > 40000 && it.input.length != textLength }.sortedBy { -(it.input.length) }
                return perfectCandidates + leadingCandidates + trailingCandidates
                // TODO: Candidate sorting
                /*
                val comparator = Comparator<Candidate> { lhs, rhs ->
                        val lhsInputLength = lhs.input.length
                        val rhsInputLength = rhs.input.length
                        val lhsOrder = lhs.order
                        val rhsOrder = max(0, rhs.order - 5000)
                        val shouldCompareOrder = lhsInputLength != textLength && rhsInputLength != textLength && (lhsOrder < rhsOrder)
                        if (shouldCompareOrder) {
                                return@Comparator lhsOrder.compareTo(rhsOrder)
                        } else {
                                return@Comparator rhsInputLength.compareTo(lhsInputLength)
                        }
                }
                return candidates.sortedWith(comparator)
                */
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
