package org.jyutping.jyutping.stroke

import org.jyutping.jyutping.keyboard.ShapeLexicon
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object Stroke {
        fun reverseLookup(keys: List<VirtualInputKey>, db: DatabaseHelper): List<Lexicon> {
                val strokeKeys = keys.mapNotNull { it.strokeVirtualKey }
                val isWildcard: Boolean = strokeKeys.any { it.isWildcard }
                val input: String = strokeKeys.joinToString(separator = PresetString.EMPTY) { it.code.toString() }
                val text: String = if (isWildcard) input.replace("6", "[12345]") else input
                val matched = if (isWildcard) db.strokeWildcardMatch(text, input) else db.strokeMatch(strokeKeys, text)
                return (matched + db.strokeGlob(text, input))
                        .distinct()
                        .flatMap { lexicon ->
                                db.reverseLookup(lexicon.text)
                                        .map { romanization ->
                                                Lexicon(text = lexicon.text, romanization = romanization, input = lexicon.input, number = lexicon.order)
                                        }
                        }
        }
}

private fun DatabaseHelper.strokeMatch(keys: List<StrokeVirtualKey>, text: String): List<ShapeLexicon> {
        val complex: Int = keys.size
        val isLongSequence: Boolean = complex >= 19
        val column: String = if (isLongSequence) "spell" else "code"
        val codeValue: Long = if (isLongSequence) text.hashCode().toLong() else keys.map { it.code.toLong() }.fold(0L) { acc, i -> acc * 10L + i }
        val command = "SELECT rowid, word FROM stroke_table WHERE $column = ${codeValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        val items: MutableList<ShapeLexicon> = mutableListOf()
        while (cursor.moveToNext()) {
                val rowID = cursor.getInt(0)
                val word = cursor.getString(1)
                val instance = ShapeLexicon(text = word, input = text, complex = complex, order = rowID)
                items.add(instance)
        }
        cursor.close()
        return items
}

private fun DatabaseHelper.strokeWildcardMatch(text: String, input: String): List<ShapeLexicon> {
        val items: MutableList<ShapeLexicon> = mutableListOf()
        val command = "SELECT rowid, word, complex FROM stroke_table WHERE stroke LIKE '${text}' LIMIT 100;"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val rowID = cursor.getInt(0)
                val word = cursor.getString(1)
                val complex = cursor.getInt(2)
                val instance = ShapeLexicon(text = word, input = input, complex = complex, order = rowID)
                items.add(instance)
        }
        cursor.close()
        return items.sorted()
}

private fun DatabaseHelper.strokeGlob(text: String, input: String): List<ShapeLexicon> {
        val items: MutableList<ShapeLexicon> = mutableListOf()
        val command = "SELECT rowid, word, complex FROM stroke_table WHERE stroke GLOB '${text}*' ORDER BY complex ASC LIMIT 100;"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val rowID = cursor.getInt(0)
                val word = cursor.getString(1)
                val complex = cursor.getInt(2)
                val instance = ShapeLexicon(text = word, input = input, complex = complex, order = rowID)
                items.add(instance)
        }
        cursor.close()
        return items.sorted()
}
