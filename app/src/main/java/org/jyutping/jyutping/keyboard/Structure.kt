package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.utilities.DatabaseHelper

object Structure {
        fun reverseLookup(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                return structureProcess(text, segmentation, db)
                        .map { item ->
                                db.characterReverseLookup(item.text)
                                        .map { romanization -> Candidate(text = item.text, romanization = romanization, input = text, mark = romanization.filter { it.isLetter() }) }
                        }
                        .flatten()
        }
        private fun structureProcess(text: String, segmentation: Segmentation, db: DatabaseHelper): List<Candidate> {
                val fullMatched = db.structureMatch(text)
                val textLength = text.length
                val schemes = segmentation.filter { it.length() == textLength }
                if (schemes.maxSchemeLength() < 1) return fullMatched
                val matches: MutableList<List<Candidate>> = mutableListOf()
                for (scheme in schemes) {
                        val pingText = scheme.joinToString(separator = String.empty) { it.origin }
                        val matched = db.structureMatch(pingText)
                        matches.add(matched)
                }
                return fullMatched + matches.flatten()
        }
}
