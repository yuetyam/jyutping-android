package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.PinyinLexicon

object Pinyin {
        fun reverseLookup(text: String, schemes: List<List<String>>, db: DatabaseHelper): List<Candidate> {
                return query(text, schemes, db)
                        .map { lexicon ->
                                db.reverseLookup(lexicon.text)
                                        .map { Candidate(text = lexicon.text, romanization = it, input = lexicon.input, mark = lexicon.mark, order = lexicon.order) }
                        }
                        .flatten()
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
                        val schemeLength = scheme.map { it.length }.reduce { acc, i -> acc + i }
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
