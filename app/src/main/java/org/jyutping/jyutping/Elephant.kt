package org.jyutping.jyutping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jyutping.jyutping.emoji.Emoji
import org.jyutping.jyutping.emoji.EmojiCategory
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.generateSymbol
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.LexiconType
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.InheritedDatabaseHelper
import org.jyutping.jyutping.utilities.DatabasePreparer
import kotlin.math.max

object Elephant {

        lateinit var sharedDatabase: SQLiteDatabase
                private set

        fun connectDatabase(context: Context) {
                if (!::sharedDatabase.isInitialized) {
                        sharedDatabase = InheritedDatabaseHelper(context, DatabasePreparer.DATABASE_NAME).readableDatabase
                }
        }

        fun lookupRomanization(text: String): List<String> {
                val matched = lookupMatch(text)
                if (matched.isNotEmpty()) return matched
                if (text.characterCount <= 1) return emptyList()
                fun fetchLeading(word: String): Pair<String?, Int> {
                        var chars = word
                        var romanization: String? = null
                        var matchedCount = 0
                        while (romanization == null && chars.isNotEmpty()) {
                                romanization = lookupMatch(chars).firstOrNull()
                                matchedCount = chars.length
                                chars = chars.dropLast(1)
                        }
                        return romanization?.let { it to matchedCount } ?: (null to 0)
                }
                var chars = text
                val fetches = mutableListOf<String>()
                while (chars.isNotEmpty()) {
                        val leading = fetchLeading(chars)
                        val romanization = leading.first
                        if (romanization != null) {
                                fetches.add(romanization)
                                val length = max(1, leading.second)
                                chars = chars.drop(length)
                        } else {
                                fetches.clear()
                                break
                        }
                }
                if (fetches.isEmpty()) return emptyList()
                val suggestion = fetches.joinToString(separator = PresetString.SPACE)
                return listOf(suggestion)
        }
        private fun lookupMatch(text: String): List<String> {
                if (text.isBlank()) return emptyList()
                val romanizations: MutableList<String> = mutableListOf()
                val command = "SELECT romanization FROM core_lexicon WHERE word = ? ORDER BY rowid;"
                sharedDatabase.rawQuery(command, arrayOf(text)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val romanization = cursor.getString(0)
                                romanizations.add(romanization)
                        }
                }
                return romanizations
        }
        fun searchTextMarks(keys: List<VirtualInputKey>, text: String? = null): List<Lexicon> {
                val text = text ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val code = text.hashCode()
                val command = "SELECT mark FROM mark_table WHERE spell = ${code};"
                val items: MutableList<String> = mutableListOf()
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val instance = cursor.getString(0)
                                items.add(instance)
                        }
                }
                return items.map { Lexicon(type = LexiconType.Text, text = it, romanization = text, input = text) }

        }
        fun symbolMatch(text: String, input: String): List<Lexicon> {
                val code = text.hashCode()
                val command = "SELECT category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE spell = ${code};"
                val emojis: MutableList<Emoji> = mutableListOf()
                sharedDatabase.rawQuery(command, null).use { cursor ->
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
                }
                return emojis.map { emoji ->
                        val codePointText = emoji.text
                        val shouldMapSkinTone: Boolean = emoji.category == EmojiCategory.SmileysAndPeople || emoji.category == EmojiCategory.Activity
                        val mappedCodePointText: String = if (shouldMapSkinTone) (mapSkinTone(codePointText) ?: codePointText) else codePointText
                        val symbolText: String = mappedCodePointText.generateSymbol()
                        val type: LexiconType = if (emoji.identifier < 10) LexiconType.Emoji else LexiconType.Symbol
                        Lexicon(type = type, text = symbolText, romanization = emoji.romanization, input = input, attached = emoji.cantonese)
                }
        }
        fun mapSkinTone(source: String): String? {
                var target: String? = null
                val command = "SELECT target FROM emoji_skin_map WHERE source = ?;"
                sharedDatabase.rawQuery(command, arrayOf(source)).use { cursor ->
                        if (cursor.moveToFirst()) {
                                target = cursor.getString(0)
                        }
                }
                return target
        }
        fun fetchDefaultFrequentEmojis(): List<Emoji> {
                val emojis: MutableList<Emoji> = mutableListOf()
                val command = "SELECT rowid, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE category = 0"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val unicodeVersion = cursor.getInt(1)
                                val codePointText = cursor.getString(2)
                                val cantonese = cursor.getString(3)
                                val romanization = cursor.getString(4)
                                val identifier: Int = rowId + 50000
                                val emojiText: String = codePointText.generateSymbol()
                                val instance = Emoji(category = EmojiCategory.Frequent, unicodeVersion = unicodeVersion, identifier = identifier, text = emojiText, cantonese = cantonese, romanization = romanization)
                                emojis.add(instance)
                        }
                }
                return emojis
        }
        fun fetchEmojiSequence(): List<Emoji> {
                val emojis: MutableList<Emoji> = mutableListOf()
                val command = "SELECT rowid, category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE category > 0 AND category < 9"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val categoryCode = cursor.getInt(1)
                                val unicodeVersion = cursor.getInt(2)
                                val codePointText = cursor.getString(3)
                                val cantonese = cursor.getString(4)
                                val romanization = cursor.getString(5)
                                val category: EmojiCategory = EmojiCategory.categoryOf(categoryCode) ?: continue
                                val identifier: Int = rowId + 10000
                                val emojiText: String = codePointText.generateSymbol()
                                val instance = Emoji(category = category, unicodeVersion = unicodeVersion, identifier = identifier, text = emojiText, cantonese = cantonese, romanization = romanization)
                                emojis.add(instance)
                        }
                }
                return emojis.distinct()
        }
}
