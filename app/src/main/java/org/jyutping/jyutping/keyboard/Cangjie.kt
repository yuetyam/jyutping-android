package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.conjoinedCode

object Cangjie {

        /**
         * Cangjie / Quick(Sucheng) Reverse Lookup
         * @param keys User input keys
         * @param variant Cangjie / Quick version
         * @return List of Lexicon
         */
        fun reverseLookup(keys: List<VirtualInputKey>, variant: CangjieVariant): List<Lexicon> = when (variant) {
                CangjieVariant.Cangjie5 -> cangjieLookup(keys = keys, table = "cangjie_table", codeColumn = "c5code", matchedComplexColumn = null, textColumn = "cangjie5", complexColumn = "c5complex")
                CangjieVariant.Cangjie3 -> cangjieLookup(keys = keys, table = "cangjie_table", codeColumn = "c3code", matchedComplexColumn = null, textColumn = "cangjie3", complexColumn = "c3complex")
                CangjieVariant.Quick5 -> cangjieLookup(keys = keys, table = "quick_table", codeColumn = "q5code", matchedComplexColumn = "q5complex", textColumn = "quick5", complexColumn = "q5complex")
                CangjieVariant.Quick3 -> cangjieLookup(keys = keys, table = "quick_table", codeColumn = "q3code", matchedComplexColumn = "q3complex", textColumn = "quick3", complexColumn = "q3complex")
        }

        private fun cangjieLookup(keys: List<VirtualInputKey>, table: String, codeColumn: String, matchedComplexColumn: String?, textColumn: String, complexColumn: String): List<Lexicon> {
                val code = keys.conjoinedCode
                val text = keys.joinToString(separator = "") { it.text }
                val complex = keys.size
                return (match(table = table, codeColumn = codeColumn, matchedComplexColumn = matchedComplexColumn, code = code, input = text, complex = complex) +
                        glob(table = table, textColumn = textColumn, complexColumn = complexColumn, text = text))
                        .distinct()
                        .flatMap { Elephant.reveresLookup(text = it.text, input = it.input) }
        }

        private fun match(table: String, codeColumn: String, matchedComplexColumn: String?, code: Long, input: String, complex: Int): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command: String = if (matchedComplexColumn != null) {
                        "SELECT rowid, word, $matchedComplexColumn FROM $table WHERE $codeColumn = $code;"
                } else {
                        "SELECT rowid, word FROM $table WHERE $codeColumn = $code;"
                }
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                if (matchedComplexColumn != null) {
                                        val matchedComplex = cursor.getInt(2)
                                        if (matchedComplex != complex) continue
                                }
                                items.add(ShapeLexicon(text = word, input = input, complex = complex, order = rowId))
                        }
                }
                return items
        }

        private fun glob(table: String, textColumn: String, complexColumn: String, text: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, $complexColumn FROM $table WHERE $textColumn GLOB ? ORDER BY $complexColumn ASC LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf("$text*")).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val complex = cursor.getInt(2)
                                items.add(ShapeLexicon(text = word, input = text, complex = complex, order = rowId))
                        }
                }
                return items.sorted()
        }
}
