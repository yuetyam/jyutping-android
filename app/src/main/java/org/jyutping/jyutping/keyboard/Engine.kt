package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.utilities.DatabaseHelper

object Engine {
        fun suggest(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                return process(text, segmentation, db)
        }
        private fun processVerbatim(text: String, db: DatabaseHelper): List<Candidate> {
                val rounds = text.indices.map { number ->
                        val leading = text.dropLast(number)
                        return db.match(text = leading, input = leading) + db.shortcut(text = leading)
                }
                return rounds.flatten<Candidate>().distinct()
        }
        private fun process(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val primary = query(text, segmentation, db)
                val firstInputLength = primary.firstOrNull()?.input?.length ?: 0
                if (firstInputLength == 0) return processVerbatim(text, db)
                return primary
        }
        private fun query(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val textLength = text.length
                val searches = search(text, segmentation, db)
                val preferredSearched = searches.filter { it.input.length == textLength }
                val matched = db.match(text = text, input = text)
                val shortcut = db.shortcut(text)
                return (matched + preferredSearched + shortcut + searches).distinct()
        }
        private fun search(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val textLength = text.length
                val perfectSchemes = segmentation.filter { it.length() == textLength }
                if (perfectSchemes.isNotEmpty()) {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in perfectSchemes) {
                                val queries: MutableList<List<Candidate>> = mutableListOf()
                                for (number in scheme.indices) {
                                        val slice = scheme.dropLast(number)
                                        val pingText = slice.joinToString(separator = String.empty) { it.origin }
                                        val inputText = slice.joinToString(separator = String.empty) { it.text }
                                        val text2mark = slice.joinToString(separator = String.space) { it.text }
                                        val matched = db.match(text = pingText, input = inputText, mark = text2mark)
                                        queries.add(matched)
                                }
                                matches.add(queries.flatten())
                        }
                        return ordered(textLength, matches.flatten())
                } else {
                        val matches: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val pingText = scheme.joinToString(separator = String.empty) { it.origin }
                                val inputText = scheme.joinToString(separator = String.empty) { it.text }
                                val text2mark = scheme.joinToString(separator = String.space) { it.text }
                                val matched = db.match(text = pingText, input = inputText, mark = text2mark)
                                matches.add(matched)
                        }
                        return ordered(textLength, matches.flatten())
                }
        }

        private fun preferred(text: String, candidates: List<Candidate>): List<Candidate> {
                val comparator = Comparator<Candidate> { lhs, rhs ->
                        val inputComparison = lhs.input.length.compareTo(rhs.input.length)
                        if (inputComparison != 0) return@Comparator -inputComparison
                        val textComparison = lhs.text.length.compareTo(rhs.text.length)
                        return@Comparator -textComparison
                }
                val sorted = candidates.sortedWith(comparator)
                val matched = sorted.filter { candidate -> candidate.romanization.filter { it.isLetter() } == text }
                return matched.ifEmpty { sorted }
        }
        private fun ordered(textLength: Int, candidates: List<Candidate>): List<Candidate> {
                val comparator = Comparator<Candidate> { lhs, rhs ->
                        val isFullInput = lhs.input.length == textLength && rhs.input.length != textLength
                        if (isFullInput) return@Comparator -1
                        val isBetterOrder = lhs.order < (rhs.order - 50000)
                        if (isBetterOrder) return@Comparator -1
                        return@Comparator -(lhs.input.length.compareTo(rhs.input.length))
                }
                return candidates.sortedWith(comparator)
        }
}
