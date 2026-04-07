package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper

object NineKeyResearcher {
        fun nineKeySearch(combos: List<Combo>, limit: Int? = null, db: DatabaseHelper): List<Lexicon> {
                val inputLength: Int = combos.size
                val fullCode: Long = combos.map { it.number }.decimalCombined()
                when (inputLength) {
                        0 -> return emptyList()
                        1 -> return db.nineKeyCodeMatch(fullCode, limit) + db.nineKeyAnchorsMatch(fullCode, 100)
                        else -> {}
                }
                val fullMatched = db.nineKeyCodeMatch(fullCode, limit)
                val idealAnchorsMatched = db.nineKeyAnchorsMatch(fullCode, 4)
                val codeMatched: List<Lexicon> = 1.rangeUntil(inputLength).flatMap { number ->
                        val code = combos.dropLast(number).map { it.number }.decimalCombined()
                        return@flatMap if (code < 1) emptyList() else db.nineKeyCodeMatch(code, limit)
                }
                val anchorsMatched: List<Lexicon> = 0.rangeUntil(inputLength).flatMap { number ->
                        val code = combos.dropLast(number).map { it.number }.decimalCombined()
                        return@flatMap if (code < 1) emptyList() else db.nineKeyAnchorsMatch(code, limit)
                }
                val queried = (fullMatched + idealAnchorsMatched + codeMatched + anchorsMatched)
                val firstInputCount = queried.firstOrNull()?.inputCount ?: 0
                if (firstInputCount >= inputLength) return queried
                val tailCombos = combos.drop(firstInputCount)
                val tailCode = tailCombos.map { it.number }.decimalCombined()
                if (tailCode < 1) return queried
                val tailLexicons = db.nineKeyCodeMatch(tailCode, 20) + db.nineKeyAnchorsMatch(tailCode, 20)
                if (tailLexicons.isEmpty()) return queried
                val head = queried.firstOrNull() ?: return queried
                val concatenated = tailLexicons.mapNotNull { head + it }.sorted().take(1)
                return concatenated + queried
        }
}

private fun DatabaseHelper.nineKeyAnchorsMatch(code: Long, limit: Int? = null): List<Lexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<Lexicon> = mutableListOf()
        val limitValue: Int = limit ?: 30
        val command = "SELECT rowid, word, romanization FROM core_lexicon WHERE nine_key_anchors = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val number = cursor.getInt(0)
                val word = cursor.getString(1)
                val romanization = cursor.getString(2)
                val anchors = romanization.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                val instance = Lexicon(text = word, romanization = romanization, input = anchors, mark = anchors, number = number)
                items.add(instance)
        }
        cursor.close()
        return items
}
private fun DatabaseHelper.nineKeyCodeMatch(code: Long, limit: Int? = null): List<Lexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<Lexicon> = mutableListOf()
        val limitValue: Int = limit ?: -1
        val command = "SELECT rowid, word, romanization FROM core_lexicon WHERE nine_key_code = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val number = cursor.getInt(0)
                val word = cursor.getString(1)
                val romanization = cursor.getString(2)
                val mark = romanization.filterNot { it.isCantoneseToneDigit }
                val input = mark.filterNot { it.isSpace }
                val instance = Lexicon(text = word, romanization = romanization, input = input, mark = mark, number = number)
                items.add(instance)
        }
        cursor.close()
        return items
}
