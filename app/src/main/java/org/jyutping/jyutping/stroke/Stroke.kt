package org.jyutping.jyutping.stroke

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.keyboard.ShapeLexicon
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.decimalOverflowed
import org.jyutping.jyutping.presets.PresetString

object Stroke {
        fun reverseLookup(keys: List<VirtualInputKey>): List<Lexicon> {
                val strokeKeys = keys.mapNotNull { it.strokeVirtualKey }
                val isWildcard: Boolean = strokeKeys.any { it.isWildcard }
                val input: String = strokeKeys.joinToString(separator = PresetString.EMPTY) { it.code.toString() }
                val text: String = if (isWildcard) input.replace("6", "[12345]") else input
                val matched: List<ShapeLexicon> = if (isWildcard) strokeWildcardMatch(text = text, input = input) else strokeMatch(keys = strokeKeys, input = input)
                return (matched + strokeGlob(text = text, input = input))
                        .distinct()
                        .flatMap { Elephant.reveresLookup(text = it.text, input = it.input) }
        }

        private fun strokeMatch(keys: List<StrokeVirtualKey>, input: String): List<ShapeLexicon> {
                val code: Long = keys.map { it.code }.decimalOverflowed()
                val complex: Int = keys.size
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word FROM stroke_table WHERE code = $code AND complex = $complex;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                items.add(ShapeLexicon(text = word, input = input, complex = complex, order = rowId))
                        }
                }
                return items
        }

        private fun strokeWildcardMatch(text: String, input: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, complex FROM stroke_table WHERE stroke LIKE ? LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(text)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val complex = cursor.getInt(2)
                                items.add(ShapeLexicon(text = word, input = input, complex = complex, order = rowId))
                        }
                }
                return items.sorted()
        }

        private fun strokeGlob(text: String, input: String): List<ShapeLexicon> {
                val items: MutableList<ShapeLexicon> = mutableListOf()
                val command: String = "SELECT rowid, word, complex FROM stroke_table WHERE stroke GLOB ? ORDER BY complex ASC LIMIT 100;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf("$text*")).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val word = cursor.getString(1) ?: continue
                                val complex = cursor.getInt(2)
                                items.add(ShapeLexicon(text = word, input = input, complex = complex, order = rowId))
                        }
                }
                return items.sorted()
        }
}
