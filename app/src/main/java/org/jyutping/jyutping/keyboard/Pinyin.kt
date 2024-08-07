package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.PinyinLexicon

object Pinyin {
        fun reverseLookup(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<Candidate> {
                val canSegment: Boolean = schemes.firstOrNull().isNullOrEmpty().not()
                if (canSegment) {
                        return process(text, schemes, db)
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
                                val anchors: String = (schemeAnchors + lastAnchor).joinToString(separator = String.empty)
                                val text2mark = scheme.joinToString(separator = String.space) { it } + String.space + tail
                                val shortcut = db.pinyinShortcut(anchors)
                                        .filter { it.pinyin.startsWith(text2mark) }
                                        .map { PinyinLexicon(text = it.text, pinyin = it.pinyin, input = text, mark = text2mark, order = it.order) }
                                shortcuts.add(shortcut)
                        }
                        shortcuts.flatten()
                }
                if (prefixes.isNotEmpty()) return prefixes + primary
                return primary
        }
        private fun query(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<PinyinLexicon> {
                val textLength = text.length
                val searches = search(text, schemes, db)
                val preferredSearches = searches.filter { it.input.length == textLength }
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
                        for (scheme in schemes) {
                                for (number in scheme.indices) {
                                        val pingText = scheme.dropLast(number).joinToString(separator = String.empty)
                                        val matched = db.pinyinMatch(pingText)
                                        matches.add(matched)
                                }
                        }
                        return matches.flatten()
                } else {
                        val matches: MutableList<List<PinyinLexicon>> = mutableListOf()
                        for (scheme in schemes) {
                                val pingText = scheme.joinToString(separator = String.empty)
                                val matched = db.pinyinMatch(pingText)
                                matches.add(matched)
                        }
                        return matches.flatten()
                }
        }
}

private fun List<String>.length(): Int = this.map { it.length }.fold(0) { acc, i -> acc + i }
