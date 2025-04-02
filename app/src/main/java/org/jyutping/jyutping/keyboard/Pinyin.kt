package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.PinyinLexicon
import kotlin.math.max

object Pinyin {
        fun reverseLookup(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<Candidate> {
                val canSegment: Boolean = schemes.firstOrNull().isNullOrEmpty().not()
                if (canSegment) {
                        return process(text, schemes, db)
                                .map { lexicon ->
                                        lookupRomanization(lexicon.text, db)
                                                .map {
                                                        Candidate(
                                                                text = lexicon.text,
                                                                romanization = it,
                                                                input = lexicon.input,
                                                                mark = lexicon.mark,
                                                                order = lexicon.order
                                                        )
                                                }
                                }
                                .flatten()
                } else {
                        return processVerbatim(text, db)
                                .map { lexicon ->
                                        db.reverseLookup(lexicon.text)
                                                .map {
                                                        Candidate(
                                                                text = lexicon.text,
                                                                romanization = it,
                                                                input = lexicon.input,
                                                                mark = lexicon.mark,
                                                                order = lexicon.order
                                                        )
                                                }
                                }
                                .flatten()
                }
        }

        private fun lookupRomanization(text: String, db: DatabaseHelper): List<String> {
                val matched = db.reverseLookup(text)
                if (matched.isNotEmpty()) return matched
                if (text.length == 1) return emptyList()
                fun fetchLeading(word: String): Pair<String?, Int> {
                        var chars = word
                        var romanization: String? = null
                        var matchedCount = 0
                        while (romanization == null && chars.isNotEmpty()) {
                                romanization = db.reverseLookup(chars).firstOrNull()
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

        private fun processVerbatim(text: String, db: DatabaseHelper): List<PinyinLexicon> {
                val rounds: MutableList<List<PinyinLexicon>> = mutableListOf()
                for (number in text.indices) {
                        val leading = text.dropLast(number)
                        val round = db.pinyinMatch(leading) + db.pinyinShortcut(leading)
                        rounds.add(round)
                }
                return rounds.flatten().distinct()
        }
        private fun process(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<PinyinLexicon> {
                val textLength = text.length
                val primary = query(text, schemes, db)
                val firstInputLength = primary.firstOrNull()?.input?.length ?: 0
                if (firstInputLength == 0) return processVerbatim(text, db)
                if (firstInputLength == textLength) return primary
                val prefixes: List<PinyinLexicon> = run {
                        val hasPrefectSchemes = schemes.find { it.length() == textLength }
                        if (hasPrefectSchemes != null) emptyList<PinyinLexicon>()
                        val shortcuts: MutableList<List<PinyinLexicon>> = mutableListOf()
                        for (scheme in schemes) {
                                val tail = text.drop(scheme.length())
                                val lastAnchor = tail.firstOrNull() ?: continue
                                val schemeAnchors = scheme.mapNotNull { it.firstOrNull() }
                                val anchors: String = (schemeAnchors + lastAnchor).joinToString(separator = PresetString.EMPTY)
                                val text2mark = scheme.joinToString(separator = PresetString.SPACE) { it } + PresetString.SPACE + tail
                                val shortcut = db.pinyinShortcut(anchors)
                                        .filter { it.pinyin.startsWith(text2mark) }
                                        .map { PinyinLexicon(text = it.text, pinyin = it.pinyin, input = text, mark = text2mark, order = it.order) }
                                shortcuts.add(shortcut)
                        }
                        shortcuts.flatten()
                }
                if (prefixes.isNotEmpty()) return prefixes + primary
                val headTexts = primary.map { it.input }.distinct()
                val concatenated: MutableList<PinyinLexicon> = mutableListOf()
                for (headText in headTexts) {
                        val headInputLength = headText.length
                        val tailText = text.drop(headInputLength)
                        val tailSegmentation = PinyinSegmentor.segment(tailText, db)
                        val tailLexicon = process(tailText, tailSegmentation, db).firstOrNull()
                        if (tailLexicon == null) continue
                        val headPinyinLexicon = primary.takeWhile { it.input == headText }.firstOrNull()
                        if (headPinyinLexicon == null) continue
                        val conjoined = headPinyinLexicon + tailLexicon
                        concatenated.add(conjoined)
                }
                val preferredConcatenated = concatenated.distinct().sorted().take(1)
                return preferredConcatenated + primary
        }
        private fun query(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<PinyinLexicon> {
                val textLength = text.length
                val searches = search(text, schemes, db)
                val preferredSearches = searches.filter { it.inputCount == textLength }
                val matched = db.pinyinMatch(text)
                val shortcut = db.pinyinShortcut(text)
                return (matched + preferredSearches + shortcut + searches).distinct()
        }
        private fun search(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<PinyinLexicon> {
                val textLength = text.length
                val perfectSchemes = schemes.filter { scheme ->
                        val schemeLength = scheme.map { it.length }.fold(0) { acc, i -> acc + i }
                        schemeLength == textLength
                }
                if (perfectSchemes.isNotEmpty()) {
                        val matches: MutableList<List<PinyinLexicon>> = mutableListOf()
                        for (scheme in perfectSchemes) {
                                for (number in scheme.indices) {
                                        val pingText = scheme.dropLast(number).joinToString(separator = PresetString.EMPTY)
                                        val matched = db.pinyinMatch(pingText)
                                        matches.add(matched)
                                }
                        }
                        return matches.flatten().sorted()
                } else {
                        val matches: MutableList<List<PinyinLexicon>> = mutableListOf()
                        for (scheme in schemes) {
                                val pingText = scheme.joinToString(separator = PresetString.EMPTY)
                                val matched = db.pinyinMatch(pingText)
                                matches.add(matched)
                        }
                        return matches.flatten().sorted()
                }
        }
}

private fun List<String>.length(): Int = this.map { it.length }.fold(0) { acc, i -> acc + i }
