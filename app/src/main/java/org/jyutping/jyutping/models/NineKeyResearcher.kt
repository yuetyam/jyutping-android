package org.jyutping.jyutping.models

import org.jyutping.jyutping.emoji.Emoji
import org.jyutping.jyutping.emoji.EmojiCategory
import org.jyutping.jyutping.extensions.generateSymbol
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

fun DatabaseHelper.queryTextMarks(combos: List<Combo>): List<Lexicon> {
        val code = combos.map { it.number }.decimalCombined()
        if (code < 1) return emptyList()
        val items: MutableList<Lexicon> = mutableListOf()
        val command = "SELECT input, mark FROM mark_table WHERE nine_key_code = ${code};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val input = cursor.getString(0)
                val textMark = cursor.getString(1)
                val instance = Lexicon(type = LexiconType.Text, text = textMark, romanization = textMark, input = input)
                items.add(instance)
        }
        cursor.close()
        return items
}

fun DatabaseHelper.nineKeySearchSymbols(combos: List<Combo>): List<Lexicon> {
        val code = combos.map { it.number }.decimalCombined()
        if (code < 1) return emptyList()
        val command = "SELECT category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE nine_key_code = ${code};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        val emojis: MutableList<Emoji> = mutableListOf()
        while (cursor.moveToNext()) {
                val categoryCode = cursor.getInt(0)
                val unicodeVersion = cursor.getInt(1)
                val codePointText = cursor.getString(2)
                val cantonese = cursor.getString(3)
                val romanization = cursor.getString(4)
                val category = EmojiCategory.categoryOf(categoryCode) ?: EmojiCategory.Frequent
                val entry = Emoji(category = category, unicodeVersion = unicodeVersion, identifier = categoryCode, text = codePointText, cantonese = cantonese, romanization = romanization)
                emojis.add(entry)
        }
        cursor.close()
        val input: String = combos.mapNotNull { it.letters.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
        return emojis.map { emoji ->
                val codePointText = emoji.text
                val shouldMapSkinTone: Boolean = emoji.category == EmojiCategory.SmileysAndPeople || emoji.category == EmojiCategory.Activity
                val mappedCodePointText: String = if (shouldMapSkinTone) (mapSkinTone(codePointText) ?: codePointText) else codePointText
                val symbolText: String = mappedCodePointText.generateSymbol()
                val type: LexiconType = if (emoji.identifier < 10) LexiconType.Emoji else LexiconType.Symbol
                Lexicon(type = type, text = symbolText, romanization = emoji.romanization, input = input, attached = emoji.cantonese)
        }
}
