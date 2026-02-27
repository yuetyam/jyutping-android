package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jyutping.jyutping.UserSettingsKey
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.Scheme
import org.jyutping.jyutping.models.Segmentation
import org.jyutping.jyutping.models.Segmenter
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.aliasAnchors
import org.jyutping.jyutping.models.aliasText
import org.jyutping.jyutping.models.length
import org.jyutping.jyutping.models.mark
import org.jyutping.jyutping.models.originAnchorsText
import org.jyutping.jyutping.models.originText
import org.jyutping.jyutping.models.syllableText
import org.jyutping.jyutping.presets.PresetString
import kotlin.collections.distinct
import kotlin.collections.plus

class UserLexiconHelper(context: Context) : SQLiteOpenHelper(context, UserSettingsKey.UserLexiconDatabaseFileName, null, 3) {

        private val createTable: String = "CREATE TABLE IF NOT EXISTS memory(id INTEGER NOT NULL PRIMARY KEY,word TEXT NOT NULL,romanization TEXT NOT NULL,shortcut INTEGER NOT NULL,ping INTEGER NOT NULL,frequency INTEGER NOT NULL,latest INTEGER NOT NULL);"

        override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL(createTable)
        }
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
        override fun onOpen(db: SQLiteDatabase?) {
                super.onOpen(db)
                db?.execSQL(createTable)
        }

        fun process(lexicons: List<Lexicon>) {
                val isNotAllCantonese = lexicons.any { it.isNotCantonese }
                if (isNotAllCantonese) return
                val lexicon = UserLexicon.join(lexicons)
                val id = lexicon.id
                val frequency = find(id)
                if (frequency != null) {
                        update(id = id, frequency = frequency)
                } else {
                        insert(lexicon)
                }
        }
        private fun insert(lexicon: UserLexicon) {
                val leading: String = "INSERT INTO memory (id, word, romanization, shortcut, ping, frequency, latest) VALUES ("
                val trailing: String = ");"
                val values: String = "${lexicon.id}, '${lexicon.word}', '${lexicon.romanization}', ${lexicon.shortcut}, ${lexicon.ping}, ${lexicon.frequency}, ${lexicon.latest}"
                val command: String = leading + values + trailing
                this.writableDatabase.execSQL(command)
        }
        private fun update(id: Int, frequency: Int) {
                val newFrequency: Int = frequency + 1
                val newTime: Long = System.currentTimeMillis()
                val command: String = "UPDATE memory SET frequency = ${newFrequency}, latest = $newTime WHERE id = ${id};"
                this.writableDatabase.execSQL(command)
        }
        private fun find(id: Int): Int? {
                val command: String = "SELECT frequency FROM memory WHERE id = $id LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        val frequency = cursor.getInt(0)
                        cursor.close()
                        return frequency
                } else {
                        return null
                }
        }

        fun remove(lexicon: Lexicon) {
                if (lexicon.isNotCantonese) return
                // TODO: Use text and romanization instead of id
                val id: Int = (lexicon.text + lexicon.romanization).hashCode()
                val command: String = "DELETE FROM memory WHERE id = ${id};"
                this.writableDatabase.execSQL(command)
        }
        fun deleteAll() {
                val command: String = "DELETE FROM memory;"
                this.writableDatabase.execSQL(command)
        }


        fun search(keys: List<VirtualInputKey>, text: String, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
                if (keys.any { it.isSyllableLetter.negative }) return emptyList()
                val inputLength = keys.size
                // val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                val fullMatched = spellMatch(text = text, input = text)
                val idealSchemes = segmentation.filter { it.length == inputLength }
                val idealQueried: List<InternalLexicon> = idealSchemes.flatMap { scheme ->
                        val spellCode = scheme.originText.hashCode()
                        val shortcutCode = scheme.originAnchorsText.hashCode()
                        return@flatMap strictMatch(spell = spellCode, shortcut = shortcutCode, input = text, mark = scheme.mark)
                }
                val queried = query(segmentation = segmentation, idealSchemes = idealSchemes)
                if (fullMatched.isNotEmpty() || idealQueried.isNotEmpty()) {
                        return (fullMatched + idealQueried).distinct().map { Lexicon(text = it.word, romanization = it.romanization, input = text, mark = it.mark, number = -1) } + queried
                }
                val shortcuts = shortcutMatch(text = text, input = text, limit = 5)
                if (shortcuts.isNotEmpty()) {
                        return shortcuts.map { Lexicon(text = it.word, romanization = it.romanization, input = text, mark = it.mark, number = -1) } + queried
                }
                val shouldPartiallyMatch: Boolean = idealSchemes.isEmpty() || (keys.lastOrNull() == VirtualInputKey.letterM) || (keys.firstOrNull() == VirtualInputKey.letterM)
                if (shouldPartiallyMatch.negative) return queried
                val prefixMatched: List<InternalLexicon> = segmentation.flatMap { scheme ->
                        if (scheme.isEmpty()) return@flatMap emptyList<InternalLexicon>()
                        val tail = keys.drop(scheme.length)
                        if (tail.isEmpty()) return@flatMap emptyList<InternalLexicon>()
                        val schemeAnchors = scheme.aliasAnchors
                        val conjoinedText = (schemeAnchors + tail).joinToString(separator = PresetString.EMPTY) { it.text }
                        val schemeSyllableText = scheme.syllableText
                        val mark: String = scheme.mark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        val tailAsAnchorText = tail.mapNotNull { if (it == VirtualInputKey.letterY) VirtualInputKey.letterJ.text.firstOrNull() else it.text.firstOrNull() }
                        val conjoinedMatched = shortcutMatch(text = conjoinedText, input = conjoinedText)
                                .mapNotNull { item ->
                                        val toneFreeRomanization = item.romanization.filterNot { it.isCantoneseToneDigit }
                                        if (toneFreeRomanization.startsWith(schemeSyllableText).negative) return@mapNotNull null
                                        val suffixAnchorText = toneFreeRomanization.drop(schemeSyllableText.length).split(PresetString.SPACE).mapNotNull { it.firstOrNull() }
                                        if (suffixAnchorText != tailAsAnchorText) return@mapNotNull null
                                        return@mapNotNull InternalLexicon(
                                                word = item.word,
                                                romanization = item.romanization,
                                                frequency = item.frequency,
                                                latest = item.latest,
                                                input = text,
                                                mark = mark
                                        )
                                }
                        val transformedTailText =
                                tail.mapIndexed { index, value -> if (index == 0 && value == VirtualInputKey.letterY) VirtualInputKey.letterJ.text else value.text }
                        val syllableText = schemeSyllableText + PresetString.SPACE + transformedTailText
                        val anchorsText = schemeAnchors.joinToString(separator = PresetString.EMPTY) { it.text } + (tail.firstOrNull()?.text ?: PresetString.EMPTY)
                        val anchorsMatched = shortcutMatch(text = anchorsText, input = anchorsText)
                                .mapNotNull { item ->
                                        val toneFreeRomanization = item.romanization.filterNot { it.isCantoneseToneDigit }
                                        if (toneFreeRomanization.startsWith(syllableText).negative) return@mapNotNull null
                                        return@mapNotNull InternalLexicon(
                                                word = item.word,
                                                romanization = item.romanization,
                                                frequency = item.frequency,
                                                latest = item.latest,
                                                input = text,
                                                mark = mark
                                        )
                                }
                        return@flatMap conjoinedMatched + anchorsMatched
                }
                val gainedMatched = 1.rangeUntil(inputLength).reversed()
                        .flatMap { number ->
                                val leadingText = keys.take(number).joinToString(separator = PresetString.EMPTY) { it.text }
                                return@flatMap shortcutMatch(text = leadingText, input = leadingText)
                        }
                        .mapNotNull { item ->
                                val tail = keys.drop(item.inputCount - 1)
                                if (tail.size > 6) return@mapNotNull null
                                val converted by lazy {
                                        InternalLexicon(
                                                word = item.word,
                                                romanization = item.romanization,
                                                frequency = item.frequency,
                                                latest = item.latest,
                                                input = text,
                                                mark = text
                                        )
                                }
                                val rawSyllable = item.romanization.filter { it.isLowercaseBasicLatinLetter }
                                if (rawSyllable.startsWith(text)) return@mapNotNull converted
                                val lastSyllable = item.romanization.split(PresetString.SPACE).lastOrNull()?.filterNot { it.isCantoneseToneDigit } ?: return@mapNotNull null
                                val tailSyllable = Segmenter.syllableText(keys = tail, db = db)
                                if (tailSyllable != null) {
                                        return@mapNotNull if (lastSyllable == tailSyllable) converted else null
                                } else {
                                        val tailText = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                                        return@mapNotNull if (lastSyllable.startsWith(tailText)) converted else null
                                }
                        }
                val partialMatched = (prefixMatched + gainedMatched)
                        .sorted()
                        .distinct()
                        .take(5)
                        .map { Lexicon(text = it.word, romanization = it.romanization, input = text, mark = it.mark, number = -1) }
                return partialMatched + queried
        }

}

private fun UserLexiconHelper.query(segmentation: Segmentation, idealSchemes: List<Scheme>): List<Lexicon> {
        if (segmentation.isEmpty()) return emptyList()
        if (idealSchemes.isEmpty()) {
                return segmentation.flatMap { performQuery(scheme = it) }
                        .sorted()
                        .distinct()
                        .take(6)
                        .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -2) }
        } else {
                return idealSchemes.flatMap { scheme ->
                        if (scheme.size <= 1) return@flatMap emptyList<InternalLexicon>()
                        return@flatMap 1.rangeUntil(scheme.size).reversed().map { scheme.take(it) }.flatMap { performQuery(scheme = it) }
                }
                        .sorted()
                        .distinct()
                        .take(6)
                        .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -2) }
        }
}

private fun UserLexiconHelper.performQuery(scheme: Scheme): List<InternalLexicon> {
        val spellCode = scheme.originText.hashCode()
        val shortcutCode = scheme.originAnchorsText.hashCode()
        return strictMatch(spell = spellCode, shortcut = shortcutCode, input = scheme.aliasText, mark = scheme.mark, limit = 5)
}
private fun UserLexiconHelper.shortcutMatch(text: String, input: String, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val code = text.replace('y', 'j').hashCode()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM memory WHERE shortcut = $code ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getInt(2)
                val latest = cursor.getLong(3)
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = input)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun UserLexiconHelper.spellMatch(text: String, input: String, mark: String? = null, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val code = text.hashCode()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM memory WHERE ping = $code ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getInt(2)
                val latest = cursor.getLong(3)
                val markText: String = mark ?: romanization.filterNot { it.isCantoneseToneDigit }
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = markText)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun UserLexiconHelper.strictMatch(spell: Int, shortcut: Int, input: String, mark: String? = null, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM memory WHERE ping = $spell AND shortcut = $shortcut ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getInt(2)
                val latest = cursor.getLong(3)
                val markText: String = mark ?: romanization.filterNot { it.isCantoneseToneDigit }
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = markText)
                instances.add(instance)
        }
        cursor.close()
        return instances
}

private data class InternalLexicon(
        val word: String,
        val romanization: String,
        val frequency: Int,
        val latest: Long,
        val input: String,
        val inputCount: Int = input.length,
        val mark: String
) : Comparable<InternalLexicon>
{
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is InternalLexicon) return false
                return this.word == other.word && this.romanization == other.romanization
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }
        override fun compareTo(other: InternalLexicon): Int {
                return this.inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: this.frequency.compareTo(other.frequency).unaryMinus()
                        .takeIf { it != 0 } ?: this.latest.compareTo(other.latest).unaryMinus()
        }
}
