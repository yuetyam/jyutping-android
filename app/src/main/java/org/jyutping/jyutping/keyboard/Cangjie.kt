package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.utilities.DatabaseHelper

object Cangjie {

        /**
         * Cangjie / Quick(Sucheng) Reverse Lookup
         * @param text User input
         * @param variant Cangjie / Quick version
         * @param db DatabaseHelper
         * @return List of Candidate
         */
        fun reverseLookup(text: String, variant: CangjieVariant, db: DatabaseHelper): List<Candidate> = when (variant) {
                CangjieVariant.Cangjie5 -> cangjieReverseLookup(5, text, db)
                CangjieVariant.Cangjie3 -> cangjieReverseLookup(3, text, db)
                CangjieVariant.Quick5 -> quickReverseLookup(5, text, db)
                CangjieVariant.Quick3 -> quickReverseLookup(3, text, db)
        }
        private fun cangjieReverseLookup(version: Int, text: String, db: DatabaseHelper): List<Candidate> {
                val matched = db.cangjieMatch(version, text)
                val globed = db.cangjieGlob(version, text).sortedWith(compareBy({it.complex}, {it.order}))
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
        private fun quickReverseLookup(version: Int, text: String, db: DatabaseHelper): List<Candidate> {
                val matched = db.quickMatch(version, text)
                val globed = db.quickGlob(version, text).sortedWith(compareBy({it.complex}, {it.order}))
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
