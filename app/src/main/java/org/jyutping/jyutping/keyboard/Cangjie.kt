package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.utilities.DatabaseHelper

object Cangjie {

        /**
         * Cangjie / Quick(Sucheng) Reverse Lookup
         * @param text User input
         * @param variant Cangjie / Quick version
         * @param db DatabaseHelper
         * @return List of Candidate
         */
        fun reverseLookup(text: String, variant: CangjieVariant, db: DatabaseHelper): List<Lexicon> = when (variant) {
                CangjieVariant.Cangjie5 -> cangjieReverseLookup(5, text, db)
                CangjieVariant.Cangjie3 -> cangjieReverseLookup(3, text, db)
                CangjieVariant.Quick5 -> quickReverseLookup(5, text, db)
                CangjieVariant.Quick3 -> quickReverseLookup(3, text, db)
        }

        private fun cangjieReverseLookup(version: Int, text: String, db: DatabaseHelper): List<Lexicon> = (db.cangjieMatch(version, text) + db.cangjieGlob(version, text))
                .distinct()
                .flatMap { lexicon ->
                        db.reverseLookup(lexicon.text)
                                .map { romanization ->
                                        Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                }
                }

        private fun quickReverseLookup(version: Int, text: String, db: DatabaseHelper): List<Lexicon> = (db.quickMatch(version, text) + db.quickGlob(version, text))
                .distinct()
                .flatMap { lexicon ->
                        db.reverseLookup(lexicon.text)
                                .map { romanization ->
                                        Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                }
                }
}
