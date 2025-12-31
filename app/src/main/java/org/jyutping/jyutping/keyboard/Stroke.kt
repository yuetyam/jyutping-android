package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Stroke {

        /**
         * Stroke Reverse Lookup
         * @param text User input.
         * @param db DatabaseHelper.
         * @return List of Candidate.
         */
        fun reverseLookup(text: String, db: DatabaseHelper): List<Candidate> {
                val codeText = text.mapNotNull { codeMap[it] }.joinToString(separator = PresetString.EMPTY)
                val isWildcardSearch: Boolean = codeText.contains('6')
                val inputText: String = if (isWildcardSearch) codeText.replace("6", "[12345]") else codeText
                val matched = if (isWildcardSearch) db.strokeWildcardMatch(inputText) else db.strokeMatch(inputText)
                return (matched + db.strokeGlob(inputText))
                        .distinct()
                        .flatMap { lexicon ->
                                db.reverseLookup(lexicon.text)
                                        .map { romanization ->
                                                Candidate(text = lexicon.text, romanization = romanization, input = lexicon.input, order = lexicon.order)
                                        }
                        }
        }
        private val codeMap: Map<Char, Char> = mapOf(
                'w' to '1',
                's' to '2',
                'a' to '3',
                'd' to '4',
                'z' to '5',
                'x' to '6',
        )
}
