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
                val matched = db.strokeMatch(text)
                val globed = db.strokeGlob(text).sortedWith(compareBy({it.complex}, {it.order}))
                return (matched + globed)
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
