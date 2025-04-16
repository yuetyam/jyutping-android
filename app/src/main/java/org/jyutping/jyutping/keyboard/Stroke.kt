package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.utilities.DatabaseHelper

object Stroke {

        /**
         * Stroke Reverse Lookup
         * @param text User input.
         * @param db DatabaseHelper.
         * @return List of Candidate.
         */
        fun reverseLookup(text: String, db: DatabaseHelper): List<Candidate> {
                val isWildcardSearch: Boolean = text.contains('x')
                val keyText: String = if (isWildcardSearch) text.replace("x", "[wsadz]") else text
                val matched = if (isWildcardSearch) db.strokeWildcardMatch(keyText) else db.strokeMatch(keyText)
                return (matched + db.strokeGlob(keyText))
                        .distinct()
                        .map { lexicon ->
                                db.reverseLookup(lexicon.text)
                                        .map { romanization ->
                                                Candidate(text = lexicon.text, romanization = romanization, input = lexicon.input, order = lexicon.order)
                                        }
                        }
                        .flatten()
        }
}
