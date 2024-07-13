package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.math.max

object Engine {
        fun suggest(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                if (db.canProcess(text).not()) return emptyList()
                if (segmentation.maxLength() < 1) return processVerbatim(text, db)
                return process(text, segmentation, db)
        }
        private fun processVerbatim(text: String, db: DatabaseHelper): List<Candidate> {
                val rounds: MutableList<List<Candidate>> = mutableListOf()
                for (number in text.indices) {
                        val leading = text.dropLast(number)
                        val round = db.match(text = leading, input = leading) + db.shortcut(text = leading)
                        rounds.add(round)
                }
                return rounds.flatten().distinct()
        }
        private fun process(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val textLength = text.length
                val primary = query(text, segmentation, db)
                val firstInputLength = primary.firstOrNull()?.input?.length ?: 0
                if (firstInputLength == 0) return processVerbatim(text, db)
                if (firstInputLength == textLength) return primary
                val prefixes: List<Candidate> = run {
                        if (segmentation.maxLength() >= textLength) emptyList<Candidate>()
                        val shortcuts: MutableList<List<Candidate>> = mutableListOf()
                        for (scheme in segmentation) {
                                val tail = text.drop(scheme.length())
                                val lastAnchor = tail.firstOrNull() ?: continue
                                val schemeAnchors = scheme.mapNotNull { it.text.firstOrNull() }
                                val anchors: String = (schemeAnchors + lastAnchor).joinToString(separator = String.empty)
                                val text2mark = scheme.joinToString(separator = String.space) { it.text } + String.space + tail
                                val shortcut = db.shortcut(anchors)
                                        .filter { candidate -> candidate.romanization.filter { it.isDigit().not() }.startsWith(text2mark) }
                                        .map { Candidate(text = it.text, romanization = it.romanization, input = text, mark = text2mark) }
                                shortcuts.add(shortcut)
                        }
                        shortcuts.flatten()
                }
                if (prefixes.isNotEmpty()) return prefixes + primary
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
                                for (number in scheme.indices) {
                                        val slice = scheme.dropLast(number)
                                        val pingText = slice.joinToString(separator = String.empty) { it.origin }
                                        val inputText = slice.joinToString(separator = String.empty) { it.text }
                                        val text2mark = slice.joinToString(separator = String.space) { it.text }
                                        val matched = db.match(text = pingText, input = inputText, mark = text2mark)
                                        matches.add(matched)
                                }
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
                val sorted = candidates.sortedWith(compareBy({-it.input.length}, {-it.text.length}))
                val matched = sorted.filter { candidate -> candidate.romanization.filter { it.isLetter() } == text }
                return matched.ifEmpty { sorted }
        }
        private fun ordered(textLength: Int, candidates: List<Candidate>): List<Candidate> {
                val perfectCandidates = candidates.filter { it.input.length == textLength }.sortedBy { it.order }
                // val imperfectCandidates = candidates.filter { it.input.length != textLength }.sortedBy { -(it.input.length) }
                val leadingCandidates = candidates.filter { it.order < 40000 && it.input.length != textLength }.sortedBy { -(it.input.length) }
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
}
