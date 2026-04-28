package org.jyutping.jyutping.memory

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.edit
import org.jyutping.jyutping.UserSettingsKey
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.extensions.isSpace
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.Scheme
import org.jyutping.jyutping.models.Segmentation
import org.jyutping.jyutping.models.Segmenter
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.aliasAnchors
import org.jyutping.jyutping.models.aliasText
import org.jyutping.jyutping.models.decimalCombined
import org.jyutping.jyutping.models.mark
import org.jyutping.jyutping.models.originAnchorsText
import org.jyutping.jyutping.models.originText
import org.jyutping.jyutping.models.schemeLength
import org.jyutping.jyutping.models.syllableText
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import kotlin.getValue

class InputMemoryHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 3) {

        companion object {
                const val DATABASE_NAME: String = UserSettingsKey.InputMemoryDatabaseFileName

                const val TABLE_NAME: String = "core_memory"
                const val UNIQUE_WHERE: String = "word = ? AND romanization = ?"

                const val LEGACY_TABLE_NAME: String = "memory"

                const val KEY_MIGRATION: String = UserSettingsKey.MemoryMigration2026
                const val DEFINED_MIGRATION_VALUE: Int = 2026
        }

        private val tableCreationCommand: String = "CREATE TABLE IF NOT EXISTS core_memory (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, frequency INTEGER NOT NULL, latest INTEGER NOT NULL, shortcut INTEGER NOT NULL, spell INTEGER NOT NULL, nine_key_anchors INTEGER NOT NULL, nine_key_code INTEGER NOT NULL, UNIQUE (word, romanization));"

        private val indexCreationCommands: List<String> = listOf(
                "CREATE INDEX IF NOT EXISTS ix_core_memory_frequency ON core_memory (frequency);",
                "CREATE INDEX IF NOT EXISTS ix_core_memory_shortcut ON core_memory (shortcut, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix_core_memory_spell ON core_memory (spell, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix_core_memory_strict ON core_memory (spell, shortcut, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix_core_memory_nine_key_anchors ON core_memory (nine_key_anchors, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix_core_memory_nine_key_code ON core_memory (nine_key_code, frequency DESC);",
        )

        private var isTableEnsured: Boolean = false

        override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL(tableCreationCommand)
                for (command in indexCreationCommands) {
                        db?.execSQL(command)
                }
                isTableEnsured = true
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

        override fun onOpen(db: SQLiteDatabase?) {
                super.onOpen(db)
                if (isTableEnsured.negative) {
                        db?.execSQL(tableCreationCommand)
                        for (command in indexCreationCommands) {
                                db?.execSQL(command)
                        }
                }
        }

        //region Memory Migration

        fun performMemoryMigration() {
                val sharedPreferences = context.getSharedPreferences(UserSettingsKey.PreferencesFileName, MODE_PRIVATE)
                val savedValue: Int = sharedPreferences.getInt(KEY_MIGRATION, 0)
                if (savedValue == DEFINED_MIGRATION_VALUE) return
                if (isLegacyDataPresent().negative) {
                        sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, DEFINED_MIGRATION_VALUE) }
                        return
                }
                if (savedValue == 0) {
                        migrate(lower = 20, upper = 100000)
                        sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, 20) }
                }
                var upper: Int = if (savedValue == 0) 20 else savedValue
                while (upper > 1) {
                        val lower: Int = (upper - 1)
                        migrate(lower, upper)
                        sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, lower) }
                        upper = lower
                }
                migrate(lower = 0, upper = 1)
                sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, DEFINED_MIGRATION_VALUE) }
        }
        private fun migrate(lower: Int, upper: Int) {
                val fetchedEntries = fetchLegacyEntries(lower, upper)
                if (fetchedEntries.isEmpty()) return
                val command: String = "INSERT OR IGNORE INTO core_memory (word, romanization, frequency, latest, shortcut, spell, nine_key_anchors, nine_key_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
                this.writableDatabase.compileStatement(command).use { statement ->
                        try {
                                this.writableDatabase.beginTransaction()
                                for (entry in fetchedEntries) {
                                        statement.clearBindings()
                                        statement.bindString(1, entry.word)
                                        statement.bindString(2, entry.romanization)
                                        statement.bindLong(3, entry.frequency)
                                        statement.bindLong(4, entry.latest)
                                        statement.bindLong(5, entry.shortcut.toLong())
                                        statement.bindLong(6, entry.spell.toLong())
                                        statement.bindLong(7, entry.nineKeyAnchors)
                                        statement.bindLong(8, entry.nineKeyCode)
                                        statement.executeInsert()
                                }
                                this.writableDatabase.setTransactionSuccessful()
                        } finally {
                                this.writableDatabase.endTransaction()
                        }
                }
        }

        // memory(id INTEGER PRIMARY KEY,word TEXT,romanization TEXT,shortcut INTEGER,ping INTEGER,frequency INTEGER,latest INTEGER)
        private fun fetchLegacyEntries(lower: Int, upper: Int): List<MemoryLexicon> {
                val command: String = "SELECT word, romanization, frequency, latest FROM $LEGACY_TABLE_NAME WHERE frequency > $lower AND frequency <= ${upper};"
                val cursor = this.readableDatabase.rawQuery(command, null)
                val instances: MutableList<MemoryLexicon> = mutableListOf()
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val frequency = cursor.getLong(2)
                        val latest = cursor.getLong(3)
                        val instance = MemoryLexicon.new(word = word, romanization = romanization, frequency = frequency, latest = latest)
                        instances.add(instance)
                }
                cursor.close()
                return instances
        }
        private fun isLegacyDataPresent(): Boolean {
                val tableExistsCommand = "SELECT EXISTS(SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '${LEGACY_TABLE_NAME}');"
                val tableExistsCursor = this.readableDatabase.rawQuery(tableExistsCommand, null)
                val tableExists = tableExistsCursor.use { cursor ->
                        cursor.moveToFirst() && cursor.getInt(0) == 1
                }
                if (tableExists.negative) return false
                val dataExistsCommand = "SELECT EXISTS(SELECT 1 FROM $LEGACY_TABLE_NAME WHERE frequency > 0 LIMIT 1);"
                val dataExistsCursor = this.readableDatabase.rawQuery(dataExistsCommand, null)
                return dataExistsCursor.use { cursor ->
                        cursor.moveToFirst() && cursor.getInt(0) == 1
                }
        }

        //endregion

        fun handle(lexicon: Lexicon) {
                if (lexicon.isNotCantonese) return
                val found = find(word = lexicon.text, romanization = lexicon.romanization)
                if (found != null) {
                        update(id = found.first, frequency = found.second + 1)
                } else {
                        val newEntry = MemoryLexicon.new(word = lexicon.text, romanization = lexicon.romanization)
                        insert(entry = newEntry)
                }
        }
        private fun find(word: String, romanization: String): Pair<Long, Long>? {
                val command: String = "SELECT id, frequency FROM core_memory WHERE word = ? AND romanization = ? LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, arrayOf(word, romanization))
                if (cursor.moveToFirst()) {
                        val id = cursor.getLong(0)
                        val frequency = cursor.getLong(1)
                        cursor.close()
                        return Pair(id, frequency)
                } else {
                        cursor.close()
                        return null
                }
        }
        private fun update(id: Long, frequency: Long) {
                val latest: Long = System.currentTimeMillis()
                val command: String = "UPDATE core_memory SET frequency = ${frequency}, latest = $latest WHERE id = ${id};"
                this.writableDatabase.execSQL(command)
        }
        private fun insert(entry: MemoryLexicon) {
                val command: String = "INSERT INTO core_memory (word, romanization, frequency, latest, shortcut, spell, nine_key_anchors, nine_key_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?);"
                val statement = this.writableDatabase.compileStatement(command)
                try {
                        statement.bindString(1, entry.word)
                        statement.bindString(2, entry.romanization)
                        statement.bindLong(3, entry.frequency)
                        statement.bindLong(4, entry.latest)
                        statement.bindLong(5, entry.shortcut.toLong())
                        statement.bindLong(6, entry.spell.toLong())
                        statement.bindLong(7, entry.nineKeyAnchors)
                        statement.bindLong(8, entry.nineKeyCode)
                        statement.executeInsert()
                } finally {
                        statement.close()
                }
        }

        /** Delete the given Lexicon from the InputMemory */
        fun forget(lexicon: Lexicon) {
                if (lexicon.isNotCantonese) return
                this.writableDatabase.delete(TABLE_NAME, UNIQUE_WHERE, arrayOf(lexicon.text, lexicon.romanization))
        }

        /** Clear Input Memory */
        fun deleteAll() {
                val command: String = "DELETE FROM core_memory;"
                this.writableDatabase.execSQL(command)
        }
}

fun InputMemoryHelper.searchMemory(keys: List<VirtualInputKey>, text: String, segmentation: Segmentation, db: DatabaseHelper): List<Lexicon> {
        if (keys.any { it.isSyllableLetter.negative }) return emptyList()
        val inputLength = keys.size
        // val text = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val fullMatched = spellMatch(text = text, input = text)
        val idealSchemes = segmentation.filter { it.schemeLength == inputLength }
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
                val tail = keys.drop(scheme.schemeLength)
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

private fun InputMemoryHelper.query(segmentation: Segmentation, idealSchemes: List<Scheme>): List<Lexicon> {
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

private fun InputMemoryHelper.performQuery(scheme: Scheme): List<InternalLexicon> {
        val spellCode = scheme.originText.hashCode()
        val shortcutCode = scheme.originAnchorsText.hashCode()
        return strictMatch(spell = spellCode, shortcut = shortcutCode, input = scheme.aliasText, mark = scheme.mark, limit = 5)
}
private fun InputMemoryHelper.shortcutMatch(text: String, input: String, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val code = text.replace('y', 'j').hashCode()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM core_memory WHERE shortcut = $code ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = input)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun InputMemoryHelper.spellMatch(text: String, input: String, mark: String? = null, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val code = text.hashCode()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM core_memory WHERE spell = $code ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                val markText: String = mark ?: romanization.filterNot { it.isCantoneseToneDigit }
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = markText)
                instances.add(instance)
        }
        cursor.close()
        return instances
}
private fun InputMemoryHelper.strictMatch(spell: Int, shortcut: Int, input: String, mark: String? = null, limit: Int? = null): List<InternalLexicon> {
        val instances: MutableList<InternalLexicon> = mutableListOf()
        val limitValue: Int = limit ?: 100
        val command = "SELECT word, romanization, frequency, latest FROM core_memory WHERE spell = $spell AND shortcut = $shortcut ORDER BY frequency DESC LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                val markText: String = mark ?: romanization.filterNot { it.isCantoneseToneDigit }
                val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = markText)
                instances.add(instance)
        }
        cursor.close()
        return instances
}

fun InputMemoryHelper.nineKeyMemorySearch(combos: List<Combo>): List<Lexicon> {
        val inputLength: Int = combos.size
        val fullCode: Long = combos.map { it.number }.decimalCombined()
        when (inputLength) {
                0 -> return emptyList()
                1 -> return (nineKeyCodeMatch(fullCode, 100) + nineKeyAnchorsMatch(fullCode, 5)).map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
                else -> {}
        }
        val fullCodeMatched = nineKeyCodeMatch(fullCode, 100)
        val fullAnchorsMatched = nineKeyAnchorsMatch(fullCode, 4)
        val ideal = (fullCodeMatched.take(10) + (fullCodeMatched + fullAnchorsMatched).sorted())
                .distinct()
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
        val queried = 1.rangeUntil(inputLength).flatMap { number ->
                val code = combos.dropLast(number).map { it.number }.decimalCombined()
                return@flatMap if (code < 1) emptyList() else nineKeyCodeMatch(code, limit = 4)
        }.distinct().take(6).map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -2) }
        return ideal + queried
}
private fun InputMemoryHelper.nineKeyAnchorsMatch(code: Long, limit: Int? = null): List<InternalLexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<InternalLexicon> = mutableListOf()
        val limitValue: Int = limit ?: 30
        val command = "SELECT word, romanization, frequency, latest FROM core_memory WHERE nine_key_anchors = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                val anchors = romanization.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                val instance = InternalLexicon(word = word, romanization = romanization, input = anchors, frequency = frequency, latest = latest, mark = anchors)
                items.add(instance)
        }
        cursor.close()
        return items
}
private fun InputMemoryHelper.nineKeyCodeMatch(code: Long, limit: Int? = null): List<InternalLexicon> {
        if (code < 1) return emptyList()
        val items: MutableList<InternalLexicon> = mutableListOf()
        val limitValue: Int = limit ?: -1
        val command = "SELECT word, romanization, frequency, latest FROM core_memory WHERE nine_key_code = $code LIMIT ${limitValue};"
        val cursor = this.readableDatabase.rawQuery(command, null)
        while (cursor.moveToNext()) {
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                val mark = romanization.filterNot { it.isCantoneseToneDigit }
                val input = mark.filterNot { it.isSpace }
                val instance = InternalLexicon(word = word, romanization = romanization, input = input, frequency = frequency, latest = latest, mark = mark)
                items.add(instance)
        }
        cursor.close()
        return items
}

private data class InternalLexicon(
        val word: String,
        val romanization: String,
        val frequency: Long,
        val latest: Long,
        val input: String,
        val inputCount: Int = input.length,
        val mark: String
) : Comparable<InternalLexicon> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is InternalLexicon) return false
                return (word == other.word) && (romanization == other.romanization)
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }
        override fun compareTo(other: InternalLexicon): Int {
                return inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: frequency.compareTo(other.frequency).unaryMinus()
                        .takeIf { it != 0 } ?: latest.compareTo(other.latest).unaryMinus()
        }
}
