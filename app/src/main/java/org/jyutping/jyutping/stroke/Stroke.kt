package org.jyutping.jyutping.stroke

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.keyboard.ShapeLexicon
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.presets.PresetString

object Stroke {
        fun reverseLookup(keys: List<VirtualInputKey>): List<Lexicon> {
                val strokeKeys = keys.mapNotNull { it.strokeVirtualKey }
                val isWildcard: Boolean = strokeKeys.any { it.isWildcard }
                val input: String = strokeKeys.joinToString(separator = PresetString.EMPTY) { it.code.toString() }
                val text: String = if (isWildcard) input.replace("6", "[12345]") else input
                val matched = if (isWildcard) strokeWildcardMatch(text, input) else strokeMatch(strokeKeys, text)
                return (matched + strokeGlob(text, input))
                        .distinct()
                        .flatMap { lexicon ->
                                Elephant.lookupRomanization(lexicon.text)
                                        .map { romanization ->
                                                Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                        }
                        }
        }

        private fun strokeMatch(keys: List<StrokeVirtualKey>, text: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val complex: Int = keys.size
                val isLongSequence: Boolean = complex >= 19
                val column: String = if (isLongSequence) "spell" else "code"
                val codeValue: Long = if (isLongSequence) text.hashCode().toLong() else keys.map { it.code.toLong() }.fold(0L) { acc, i -> acc * 10L + i }
                val command = "SELECT rowid, word FROM stroke_table WHERE $column = ${codeValue};"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val instance = ShapeLexicon(text = word, input = text, complex = complex, order = rowId)
                                items.add(instance)
                        }
                }
                return items
        }

        private fun strokeWildcardMatch(text: String, input: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command = "SELECT rowid, word, complex FROM stroke_table WHERE stroke LIKE '${text}' LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val complex = cursor.getInt(2)
                                val instance = ShapeLexicon(text = word, input = input, complex = complex, order = rowId)
                                items.add(instance)
                        }
                }
                return items.sorted()
        }

        private fun strokeGlob(text: String, input: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command = "SELECT rowid, word, complex FROM stroke_table WHERE stroke GLOB '${text}*' ORDER BY complex ASC LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1)
                                val complex = cursor.getInt(2)
                                val instance = ShapeLexicon(text = word, input = input, complex = complex, order = rowId)
                                items.add(instance)
                        }
                }
                return items.sorted()
        }
}
