package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.charCode

object Cangjie {

        /**
         * Cangjie / Quick(Sucheng) Reverse Lookup
         * @param text User input
         * @param variant Cangjie / Quick version
         * @return List of Candidate
         */
        fun reverseLookup(text: String, variant: CangjieVariant): List<Lexicon> = when (variant) {
                CangjieVariant.Cangjie5 -> cangjieReverseLookup(5, text)
                CangjieVariant.Cangjie3 -> cangjieReverseLookup(3, text)
                CangjieVariant.Quick5 -> quickReverseLookup(5, text)
                CangjieVariant.Quick3 -> quickReverseLookup(3, text)
        }

        private fun cangjieReverseLookup(version: Int, text: String): List<Lexicon> = (cangjieMatch(version, text) + cangjieGlob(version, text))
                .distinct()
                .flatMap { lexicon ->
                        Elephant.lookupRomanization(lexicon.text)
                                .map { romanization ->
                                        Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                }
                }

        private fun quickReverseLookup(version: Int, text: String): List<Lexicon> = (quickMatch(version, text) + quickGlob(version, text))
                .distinct()
                .flatMap { lexicon ->
                        Elephant.lookupRomanization(lexicon.text)
                                .map { romanization ->
                                        Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                }
                }

        private fun cangjieMatch(version: Int, text: String): List<ShapeLexicon> {
                val code = text.charCode() ?: return emptyList()
                val command = "SELECT rowid, word FROM cangjie_table WHERE c${version}code = ${code};"
                val items: MutableList<ShapeLexicon> = mutableListOf()
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val instance = ShapeLexicon(text = word, input = text, complex = text.length, order = rowId)
                                items.add(instance)
                        }
                }
                return items
        }

        private fun cangjieGlob(version: Int, text: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command = "SELECT rowid, word, c${version}complex FROM cangjie_table WHERE cangjie${version} GLOB '${text}*' ORDER BY c${version}complex ASC LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val complex = cursor.getInt(2)
                                val instance = ShapeLexicon(text = word, input = text, complex = complex, order = rowId)
                                items.add(instance)
                        }
                }
                return items.sorted()
        }

        private fun quickMatch(version: Int, text: String): List<ShapeLexicon> {
                val code = text.charCode() ?: return emptyList()
                val command = "SELECT rowid, word FROM quick_table WHERE q${version}code = ${code};"
                val items: MutableList<ShapeLexicon> = mutableListOf()
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val instance = ShapeLexicon(text = word, input = text, complex = text.length, order = rowId)
                                items.add(instance)
                        }
                }
                return items
        }

        private fun quickGlob(version: Int, text: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command = "SELECT rowid, word, q${version}complex FROM quick_table WHERE quick${version} GLOB '${text}*' ORDER BY q${version}complex ASC LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val complex = cursor.getInt(2)
                                val instance = ShapeLexicon(text = word, input = text, complex = complex, order = rowId)
                                items.add(instance)
                        }
                }
                return items.sorted()
        }
}
