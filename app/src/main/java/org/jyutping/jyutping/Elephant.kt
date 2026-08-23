package org.jyutping.jyutping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jyutping.jyutping.emoji.Emoji
import org.jyutping.jyutping.emoji.EmojiCategory
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.generateSymbol
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.LexiconType
import org.jyutping.jyutping.models.Segmentation
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.conjoinedCode
import org.jyutping.jyutping.models.NineKeySegmentation
import org.jyutping.jyutping.models.complexity
import org.jyutping.jyutping.models.length
import org.jyutping.jyutping.models.originKeys
import org.jyutping.jyutping.models.originCombos
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.decimalCombinedCode
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

        // MARK: - Reverse Lookup Annotation

        /**
         * Reverse Lookup.
         * @param text Cantonese word.
         * @param input User input for this word.
         * @param mark Formatted user input for pre-edit display.
         * @return Lookup transformed Lexicons.
         */
        fun reveresLookup(text: String, input: String, mark: String? = null): List<Lexicon> {
                val romanizations: List<String> = lookup(text)
                return romanizations.map { Lexicon(text = text, romanization = it, input = input, mark = mark ?: input) }
        }

        /**
         * Search Romanization for word
         * @param text Cantonese word
         * @return Array of Jyutping matched the input word
         */
        fun lookup(text: String): List<String> {
                if (text.isEmpty()) return emptyList()
                val matched = lookupMatch(text)
                if (matched.isNotEmpty()) return matched
                if (text.characterCount <= 1) return emptyList()
                var chars = text
                val fetches = mutableListOf<String>()
                while (chars.isNotEmpty()) {
                        val leading = fetchLeading(chars)
                        val romanization = leading.first
                        if (romanization == null) {
                                fetches.clear()
                                break
                        }
                        fetches.add(romanization)
                        val length = max(1, leading.second)
                        chars = chars.drop(length)
                }
                if (fetches.isEmpty()) return emptyList()
                val suggestion = fetches.joinToString(separator = PresetString.SPACE)
                return listOf(suggestion)
        }

        private fun fetchLeading(word: String): Pair<String?, Int> {
                var chars = word
                var romanization: String? = null
                var matchedCount = 0
                while (romanization == null && chars.isNotEmpty()) {
                        romanization = lookupMatch(chars).firstOrNull()
                        matchedCount = chars.characterCount
                        chars = chars.dropLast(1)
                }
                return if (romanization != null) Pair(romanization, matchedCount) else Pair(null, 0)
        }

        private fun lookupMatch(text: String): List<String> {
                val romanizations: MutableList<String> = mutableListOf()
                val command = "SELECT romanization FROM lexicon_core WHERE word = ? ORDER BY rowid;"
                sharedDatabase.rawQuery(command, arrayOf(text)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val romanization = cursor.getString(0) ?: continue
                                romanizations.add(romanization)
                        }
                }
                return romanizations
        }

        // MARK: - Plain Text Suggestions

        fun searchPlainTexts(keys: List<VirtualInputKey>): List<Lexicon> {
                val spell: Long = keys.conjoinedCode
                val letterCount: Long = keys.size.toLong()
                val entries: MutableList<String> = mutableListOf()
                val command: String = "SELECT word FROM plain_text_table WHERE spell = $spell AND letter_count = $letterCount ORDER BY rowid;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0) ?: continue
                                entries.add(word)
                        }
                }
                if (entries.isEmpty()) return emptyList()
                val input: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                return entries.map { Lexicon(type = LexiconType.Text, text = it, romanization = input, input = input) }
        }

        fun queryPlainTexts(combos: List<Combo>): List<Lexicon> {
                val code: Long = combos.decimalCombinedCode
                val letterCount: Long = combos.size.toLong()
                val entries: MutableList<Lexicon> = mutableListOf()
                val command: String = "SELECT input, word FROM plain_text_table WHERE spell_9key = $code AND letter_count = $letterCount ORDER BY rowid;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val input = cursor.getString(0) ?: continue
                                val word = cursor.getString(1) ?: continue
                                entries.add(Lexicon(type = LexiconType.Text, text = word, romanization = input, input = input))
                        }
                }
                return entries
        }

        // MARK: - Emoji / Symbol Suggestions

        fun searchSymbols(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
                val inputLength: Int = keys.count { it.isSyllableLetter }
                val input: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                return segmentation.filter { it.length == inputLength }
                        .flatMap { symbolMatch(spell = it.originKeys.conjoinedCode, complexity = it.complexity, input = input) }
                        .distinct()
        }

        private fun symbolMatch(spell: Long, complexity: Long, input: String): List<Lexicon> {
                val emojis: MutableList<Emoji> = mutableListOf()
                val command: String = "SELECT category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE spell = $spell AND complexity = $complexity ORDER BY rowid;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val categoryCode = cursor.getInt(0)
                                val unicodeVersion = cursor.getInt(1)
                                val codePointText = cursor.getString(2) ?: continue
                                val cantonese = cursor.getString(3) ?: continue
                                val romanization = cursor.getString(4) ?: continue
                                val category = EmojiCategory.categoryOf(categoryCode) ?: EmojiCategory.Frequent
                                val instance = Emoji(category = category, unicodeVersion = unicodeVersion, identifier = categoryCode, text = codePointText, cantonese = cantonese, romanization = romanization)
                                emojis.add(instance)
                        }
                }
                if (emojis.isEmpty()) return emptyList()
                return emojis.mapNotNull { emoji ->
                        val codePointText: String = emoji.text
                        val shouldMapSkinTone: Boolean = (emoji.category == EmojiCategory.SmileysAndPeople) || (emoji.category == EmojiCategory.Activity)
                        val mappedCodePointText: String = if (shouldMapSkinTone) (mapSkinTone(codePointText) ?: codePointText) else codePointText
                        val symbolText: String = mappedCodePointText.generateSymbol()
                        val type: LexiconType = if (emoji.identifier < 10) LexiconType.Emoji else LexiconType.Symbol
                        Lexicon(type = type, text = symbolText, romanization = emoji.romanization, input = input, attached = emoji.cantonese)
                }
        }

        fun nineKeySearchSymbols(combos: List<Combo>, segmentation: NineKeySegmentation): List<Lexicon> {
                val inputLength: Int = combos.size
                return segmentation.filter { it.length == inputLength }
                        .flatMap { nineKeySymbolMatch(combos = it.originCombos, complexity = it.complexity) }
                        .distinct()
        }

        private fun nineKeySymbolMatch(combos: List<Combo>, complexity: Long): List<Lexicon> {
                val code: Long = combos.decimalCombinedCode
                val complexityValue: Long = complexity
                val emojis: MutableList<Emoji> = mutableListOf()
                val command: String = "SELECT category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE spell_9key = $code AND complexity = $complexityValue;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val categoryCode = cursor.getInt(0)
                                val unicodeVersion = cursor.getInt(1)
                                val codePointText = cursor.getString(2) ?: continue
                                val cantonese = cursor.getString(3) ?: continue
                                val romanization = cursor.getString(4) ?: continue
                                val category = EmojiCategory.categoryOf(categoryCode) ?: EmojiCategory.Frequent
                                val instance = Emoji(category = category, unicodeVersion = unicodeVersion, identifier = categoryCode, text = codePointText, cantonese = cantonese, romanization = romanization)
                                emojis.add(instance)
                        }
                }
                if (emojis.isEmpty()) return emptyList()
                val input: String = combos.mapNotNull { it.letters.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                return emojis.mapNotNull { emoji ->
                        val codePointText: String = emoji.text
                        val shouldMapSkinTone: Boolean = (emoji.category == EmojiCategory.SmileysAndPeople) || (emoji.category == EmojiCategory.Activity)
                        val mappedCodePointText: String = if (shouldMapSkinTone) (mapSkinTone(codePointText) ?: codePointText) else codePointText
                        val symbolText: String = mappedCodePointText.generateSymbol()
                        val type: LexiconType = if (emoji.identifier < 10) LexiconType.Emoji else LexiconType.Symbol
                        Lexicon(type = type, text = symbolText, romanization = emoji.romanization, input = input, attached = emoji.cantonese)
                }.distinct()
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
                val command = "SELECT rowid, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE category = 0 ORDER BY rowid;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val unicodeVersion = cursor.getInt(1)
                                val codePointText = cursor.getString(2) ?: continue
                                val cantonese = cursor.getString(3) ?: continue
                                val romanization = cursor.getString(4) ?: continue
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
                val command = "SELECT rowid, category, unicode_version, code_point, cantonese, romanization FROM symbol_table WHERE category > 0 AND category < 9 ORDER BY rowid;"
                sharedDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val rowId = cursor.getInt(0)
                                val categoryCode = cursor.getInt(1)
                                val unicodeVersion = cursor.getInt(2)
                                val codePointText = cursor.getString(3) ?: continue
                                val cantonese = cursor.getString(4) ?: continue
                                val romanization = cursor.getString(5) ?: continue
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
