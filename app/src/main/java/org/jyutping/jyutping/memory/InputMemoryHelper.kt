package org.jyutping.jyutping.memory

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.edit
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jyutping.jyutping.UserSettingsKey
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.extensions.latinLetterOnly
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.extensions.strippedSpaces
import org.jyutping.jyutping.extensions.strippedTones
import org.jyutping.jyutping.extensions.toneConverted
import org.jyutping.jyutping.extensions.toneDigitOnly
import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.models.NineKeyScheme
import org.jyutping.jyutping.models.NineKeySegmentation
import org.jyutping.jyutping.models.Scheme
import org.jyutping.jyutping.models.Segmentation
import org.jyutping.jyutping.models.Segmenter
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.models.aliasAnchors
import org.jyutping.jyutping.models.aliasText
import org.jyutping.jyutping.models.anchorNormalized
import org.jyutping.jyutping.models.complexity
import org.jyutping.jyutping.models.conjoinedCode
import org.jyutping.jyutping.models.isIrregular
import org.jyutping.jyutping.models.length
import org.jyutping.jyutping.models.mark
import org.jyutping.jyutping.models.originCombos
import org.jyutping.jyutping.models.originKeys
import org.jyutping.jyutping.models.serialOriginKeys
import org.jyutping.jyutping.models.syllableText
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.decimalCombinedCode
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

class InputMemoryHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
                private const val DATABASE_VERSION = 3
                private const val DATABASE_NAME: String = UserSettingsKey.InputMemoryDatabaseFileName

                private const val TABLE_NAME: String = "memory2608"
                private const val UNIQUE_WHERE: String = "word = ? AND romanization = ?"

                private const val LEGACY_CORE_MEMORY_TABLE_NAME: String = "core_memory"
                private const val LEGACY_MEMORY_TABLE_NAME: String = "memory"

                private const val KEY_MIGRATION: String = UserSettingsKey.MemoryMigration2608
                private const val DEFINED_MIGRATION_VALUE: Int = 2608

                private const val KEY_MIGRATION_2026: String = UserSettingsKey.MemoryMigration2026
                private const val DEFINED_MIGRATION_2026_VALUE: Int = 2026

                @Volatile internal var isMigrating: Boolean = false
        }

        private val tableCreationCommand: String = "CREATE TABLE IF NOT EXISTS memory2608 (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, frequency INTEGER NOT NULL, latest INTEGER NOT NULL, char_count INTEGER NOT NULL, letter_count INTEGER NOT NULL, complexity INTEGER NOT NULL, anchors INTEGER NOT NULL, spell INTEGER NOT NULL, anchors_9key INTEGER NOT NULL, spell_9key INTEGER NOT NULL, UNIQUE (word, romanization));"
        private val indexCreationCommands: List<String> = listOf(
                "CREATE INDEX IF NOT EXISTS ix2608_frequency ON memory2608 (frequency);",
                "CREATE INDEX IF NOT EXISTS ix2608_anchors ON memory2608 (anchors, char_count, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix2608_spell ON memory2608 (spell, letter_count, complexity, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix2608_anchors_9key ON memory2608 (anchors_9key, char_count, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix2608_spell_9key ON memory2608 (spell_9key, letter_count, complexity, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix2608_word ON memory2608 (word, frequency DESC);",
                "CREATE INDEX IF NOT EXISTS ix2608_lexicon ON memory2608 (word, romanization);",
        )
        private var isTableEnsured: Boolean = false

        override fun onConfigure(db: SQLiteDatabase?) {
                super.onConfigure(db)
                db?.enableWriteAheadLogging()
        }
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

        fun prepare() {
                val sharedPreferences = context.getSharedPreferences(UserSettingsKey.PreferencesFileName, MODE_PRIVATE)
                val isMigrated: Boolean = (sharedPreferences.getInt(KEY_MIGRATION, 0) == DEFINED_MIGRATION_VALUE)
                if (isMigrated || isMigrating) return
                isMigrating = true
                migrateMemory(sharedPreferences = sharedPreferences)
        }
        private fun migrateMemory(sharedPreferences: SharedPreferences) {
                val didPreviousMigrationCompleted: Boolean = (sharedPreferences.getInt(KEY_MIGRATION_2026, 0) == DEFINED_MIGRATION_2026_VALUE)
                val tableName: String = if (didPreviousMigrationCompleted) LEGACY_CORE_MEMORY_TABLE_NAME else LEGACY_MEMORY_TABLE_NAME
                if (isLegacyDataPresent(table = tableName)) {
                        performMigration(sharedPreferences = sharedPreferences, table = tableName)
                } else {
                        missionAccomplished(sharedPreferences = sharedPreferences)
                }
        }
        private fun performMigration(sharedPreferences: SharedPreferences, table: String) {
                val savedValue: Int = sharedPreferences.getInt(KEY_MIGRATION, 0)
                if (savedValue == DEFINED_MIGRATION_VALUE) {
                        missionAccomplished(sharedPreferences = sharedPreferences)
                        return
                }
                if (savedValue == 0) {
                        migrate(table = table, lower = 20, upper = 100000)
                        sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, 20) }
                }
                var upper: Int = if (savedValue == 0) 20 else savedValue
                while (upper > 1) {
                        val lower: Int = (upper - 1)
                        migrate(table = table, lower = lower, upper = upper)
                        sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, lower) }
                        upper = lower
                }
                if (upper <= 1) {
                        migrate(table = table, lower = 0, upper = 1)
                        missionAccomplished(sharedPreferences = sharedPreferences)
                }
        }
        private fun migrate(table: String, lower: Int, upper: Int) {
                val fetchedEntries = fetchLegacyEntries(table = table, lower = lower, upper = upper)
                if (fetchedEntries.isEmpty()) return
                val command: String = "INSERT OR IGNORE INTO memory2608 (word, romanization, frequency, latest, char_count, letter_count, complexity, anchors, spell, anchors_9key, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                this.writableDatabase.compileStatement(command).use { statement ->
                        try {
                                this.writableDatabase.beginTransaction()
                                for (entry in fetchedEntries) {
                                        statement.clearBindings()
                                        statement.bindString(1, entry.word)
                                        statement.bindString(2, entry.romanization)
                                        statement.bindLong(3, entry.frequency)
                                        statement.bindLong(4, entry.latest)
                                        statement.bindLong(5, entry.charCount.toLong())
                                        statement.bindLong(6, entry.letterCount.toLong())
                                        statement.bindLong(7, entry.complexity)
                                        statement.bindLong(8, entry.anchors)
                                        statement.bindLong(9, entry.spell)
                                        statement.bindLong(10, entry.nineKeyAnchors)
                                        statement.bindLong(11, entry.nineKeySpell)
                                        statement.executeInsert()
                                }
                                this.writableDatabase.setTransactionSuccessful()
                        } finally {
                                this.writableDatabase.endTransaction()
                        }
                }
        }

        // core_memory(id INTEGER PRIMARY KEY AUTOINCREMENT,word TEXT,romanization TEXT,frequency INTEGER,latest INTEGER,shortcut INTEGER,spell INTEGER,nine_key_anchors INTEGER,nine_key_code INTEGER)
        // memory(id INTEGER PRIMARY KEY,word TEXT,romanization TEXT,shortcut INTEGER,ping INTEGER,frequency INTEGER,latest INTEGER)
        private fun fetchLegacyEntries(table: String, lower: Int, upper: Int): List<MemoryLexicon> {
                val instances: MutableList<MemoryLexicon> = mutableListOf()
                val command: String = "SELECT word, romanization, frequency, latest FROM $table WHERE frequency > $lower AND frequency <= $upper ORDER BY frequency DESC;"
                readableDatabase.rawQuery(command, null).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                val romanization = cursor.getString(1)
                                val frequency = cursor.getLong(2)
                                val latest = cursor.getLong(3)
                                val instance = MemoryLexicon.new(word = word, romanization = romanization, frequency = frequency, latest = latest)
                                instances.add(instance)
                        }
                }
                return instances
        }
        private fun isLegacyDataPresent(table: String): Boolean {
                val tableExistsCommand = "SELECT EXISTS(SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = '$table');"
                val tableExistsCursor = this.readableDatabase.rawQuery(tableExistsCommand, null)
                val tableExists = tableExistsCursor.use { cursor ->
                        cursor.moveToFirst() && cursor.getInt(0) == 1
                }
                if (tableExists.negative) return false
                val dataExistsCommand = "SELECT EXISTS(SELECT 1 FROM $table WHERE frequency > 0 LIMIT 1);"
                val dataExistsCursor = this.readableDatabase.rawQuery(dataExistsCommand, null)
                return dataExistsCursor.use { cursor ->
                        cursor.moveToFirst() && cursor.getInt(0) == 1
                }
        }
        private fun missionAccomplished(sharedPreferences: SharedPreferences) {
                sharedPreferences.edit(commit = true) { putInt(KEY_MIGRATION, DEFINED_MIGRATION_VALUE) }
                cleanupObsoleteObjects(sharedPreferences = sharedPreferences)
                isMigrating = false
        }
        private fun cleanupObsoleteObjects(sharedPreferences: SharedPreferences) {
                sharedPreferences.edit { remove(KEY_MIGRATION_2026) }
        }

        //endregion

        fun handle(lexicon: Lexicon) {
                if (isMigrating) return
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
                var id: Long? = null
                var frequency: Long? = null
                val command: String = "SELECT id, frequency FROM memory2608 WHERE word = ? AND romanization = ? LIMIT 1;"
                readableDatabase.rawQuery(command, arrayOf(word, romanization)).use { cursor ->
                        if (cursor.moveToFirst()) {
                                id = cursor.getLong(0)
                                frequency = cursor.getLong(1)
                        }
                }
                return if (id != null && frequency != null) Pair(id, frequency) else null
        }
        private fun update(id: Long, frequency: Long) {
                val latest: Long = System.currentTimeMillis()
                val command: String = "UPDATE memory2608 SET frequency = ${frequency}, latest = $latest WHERE id = ${id};"
                this.writableDatabase.execSQL(command)
        }
        private fun insert(entry: MemoryLexicon) {
                val command: String = "INSERT INTO memory2608 (word, romanization, frequency, latest, char_count, letter_count, complexity, anchors, spell, anchors_9key, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
                writableDatabase.compileStatement(command).use { statement ->
                        statement.bindString(1, entry.word)
                        statement.bindString(2, entry.romanization)
                        statement.bindLong(3, entry.frequency)
                        statement.bindLong(4, entry.latest)
                        statement.bindLong(5, entry.charCount.toLong())
                        statement.bindLong(6, entry.letterCount.toLong())
                        statement.bindLong(7, entry.complexity)
                        statement.bindLong(8, entry.anchors)
                        statement.bindLong(9, entry.spell)
                        statement.bindLong(10, entry.nineKeyAnchors)
                        statement.bindLong(11, entry.nineKeySpell)
                        statement.executeInsert()
                }
        }

        /** Delete the given Lexicon from the InputMemory */
        fun forget(lexicon: Lexicon) {
                if (isMigrating) return
                if (lexicon.isNotCantonese) return
                writableDatabase.delete(TABLE_NAME, UNIQUE_WHERE, arrayOf(lexicon.text, lexicon.romanization))
        }

        /** Clear Input Memory */
        fun deleteAll() {
                if (isMigrating) return
                val command: String = "DELETE FROM memory2608;"
                writableDatabase.execSQL(command)
        }

        fun inspect(lexicon: Lexicon): Pair<Long, Long> {
                var frequency: Long = 0L
                var latest: Long = 0L
                val command = "SELECT frequency, latest FROM memory2608 WHERE word = ? AND romanization = ? LIMIT 1;"
                readableDatabase.rawQuery(command, arrayOf(lexicon.text, lexicon.romanization)).use { cursor ->
                        if (cursor.moveToFirst()) {
                                frequency = cursor.getLong(0)
                                latest = cursor.getLong(1)
                        }
                }
                return Pair(frequency, latest)
        }
}

suspend fun InputMemoryHelper.suggest(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
        if (InputMemoryHelper.isMigrating) return emptyList()
        val hasApostrophe: Boolean = keys.any { it.isApostrophe }
        val hasToneInputKey: Boolean = keys.any { it.isToneInputKey }
        return when {
                hasApostrophe.negative && hasToneInputKey.negative -> search(keys = keys, segmentation = segmentation)
                hasApostrophe && hasToneInputKey -> {
                        val syllableKeys = keys.filter { it.isSyllableLetter }
                        val candidates = search(keys = syllableKeys, segmentation = segmentation)
                        val inputText: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        val text: String = inputText.toneConverted()
                        candidates.mapNotNull { item ->
                                if (text.startsWith(item.romanization).negative) return@mapNotNull null
                                item.replacedInput(inputText)
                        }
                }
                hasApostrophe.negative && hasToneInputKey -> {
                        val syllableKeys = keys.filter { it.isSyllableLetter }
                        val candidates = search(keys = syllableKeys, segmentation = segmentation)
                        val inputText: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        val text: String = inputText.toneConverted()
                        val textTones: String = text.toneDigitOnly
                        candidates.mapNotNull { item ->
                                val syllableText: String = item.romanization.strippedSpaces()
                                if (syllableText != text) return@mapNotNull item.replacedInput(inputText)
                                val tones: String = syllableText.toneDigitOnly
                                when (Pair(textTones.length, tones.length)) {
                                        Pair(1, 1) -> {
                                                if (text.length != (item.inputCount + 1)) return@mapNotNull null
                                                val isToneLast: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                if (isToneLast.negative) return@mapNotNull null
                                                if (textTones != tones) return@mapNotNull null
                                                item.replacedInput(inputText)
                                        }
                                        Pair(1, 2) -> {
                                                val isToneLast: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                if (isToneLast) {
                                                        if (tones.endsWith(textTones).negative) return@mapNotNull null
                                                        val isCorrectPosition: Boolean = text.drop(item.inputCount).firstOrNull()?.isCantoneseToneDigit ?: false
                                                        if (isCorrectPosition.negative) return@mapNotNull null
                                                        item.replacedInput(inputText)
                                                } else {
                                                        if (tones.startsWith(textTones).negative) return@mapNotNull null
                                                        item.replacedInput(inputText)
                                                }
                                        }
                                        Pair(2, 2) -> {
                                                val isToneLast: Boolean = text.lastOrNull()?.isCantoneseToneDigit ?: false
                                                if (isToneLast.negative) return@mapNotNull null
                                                if (textTones != tones) return@mapNotNull null
                                                if (item.inputCount != (text.length - 2)) return@mapNotNull null
                                                item.replacedInput(inputText)
                                        }
                                        else -> {
                                                if (inputText != syllableText) return@mapNotNull null
                                                item.replacedInput(inputText)
                                        }
                                }
                        }
                }
                else -> {
                        val syllableKeys = keys.filter { it.isSyllableLetter }
                        val candidates = search(keys = syllableKeys, segmentation = segmentation)
                        val isHeadingSeparator: Boolean = keys.firstOrNull()?.isApostrophe ?: false
                        val isTrailingSeparator: Boolean = keys.lastOrNull()?.isApostrophe ?: false
                        if (isHeadingSeparator) return emptyList()
                        val inputSeparatorCount: Int = keys.count { it.isApostrophe }
                        val inputLength: Int = keys.size
                        val text: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        val textParts: List<String> = text.split(PresetCharacter.APOSTROPHE).filter { it.isNotEmpty() }
                        candidates.mapNotNull { item ->
                                val syllables: List<String> = item.romanization.strippedTones().split(PresetCharacter.SPACE)
                                if (syllables == textParts) return@mapNotNull item.replacedInput(text)
                                when {
                                        (inputSeparatorCount == 1) && isTrailingSeparator -> {
                                                if (syllables.size != 1) return@mapNotNull null
                                                if (item.inputCount != (inputLength - 1)) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                                        inputSeparatorCount == 1 -> {
                                                if (syllables.size != 2) return@mapNotNull null
                                                val isMatched: Boolean = run {
                                                        if (inputLength == 3) return@run true
                                                        if (syllables.first() == textParts.first()) return@run true
                                                        if (textParts.firstOrNull()?.length != 1) return@run false
                                                        if (textParts.firstOrNull()?.firstOrNull() != syllables.firstOrNull()?.firstOrNull()) return@run false
                                                        val lastSyllable = syllables.lastOrNull() ?: return@run false
                                                        textParts.lastOrNull()?.startsWith(lastSyllable) ?: false
                                                }
                                                if (isMatched.negative) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                                        (inputSeparatorCount == 2) && isTrailingSeparator -> {
                                                if (syllables.size != 2) return@mapNotNull null
                                                if (item.inputCount != (inputLength - 2)) return@mapNotNull null
                                                val isMatched: Boolean = run {
                                                        if (inputLength == 4) return@run true
                                                        if (syllables.first() == textParts.first()) return@run true
                                                        if (textParts.firstOrNull()?.length != 1) return@run false
                                                        if (textParts.firstOrNull()?.firstOrNull() != syllables.firstOrNull()?.firstOrNull()) return@run false
                                                        textParts.lastOrNull() == syllables.lastOrNull()
                                                }
                                                if (isMatched.negative) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                                        (inputSeparatorCount == 2 && inputLength == 5 && textParts.size == 3) ||
                                                (inputSeparatorCount == 3 && inputLength == 6 && textParts.size == 3) -> {
                                                if (syllables.size != 3) return@mapNotNull null
                                                item.replacedInput(text)
                                        }
                                        else -> null
                                }
                        }
                }
        }
}

/** Preferred maximum number of syllables in one lexicon entry */
private const val MAX_CHAR_COUNT: Int = 9

private suspend fun isActive(): Boolean = currentCoroutineContext().isActive

private suspend fun InputMemoryHelper.search(keys: List<VirtualInputKey>, segmentation: Segmentation): List<Lexicon> {
        val inputLength: Int = keys.size
        val idealSchemes: List<Scheme> = segmentation.filter { it.length == inputLength }
        val queried = query(segmentation = segmentation, idealSchemes = idealSchemes)
        val anchorsMatched = anchorsMatch(keys = keys, limit = if (queried.isEmpty()) 20 else 5)
                .regularSorted(isOrdered = true)
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
        val idealQueried = queried.filter { it.inputCount >= inputLength }
                .regularSorted()
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
        val notIdealQueried = queried.filter { it.inputCount < inputLength }
                .peculiarSorted()
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -2) }
        if (idealQueried.isNotEmpty() || anchorsMatched.isNotEmpty()) {
                return idealQueried + anchorsMatched + notIdealQueried
        }
        if (inputLength <= 2 || inputLength >= 25) return notIdealQueried
        val shouldPartiallyMatch: Boolean = idealSchemes.isEmpty() || (keys.lastOrNull() == VirtualInputKey.letterM) || (keys.firstOrNull() == VirtualInputKey.letterM)
        if (shouldPartiallyMatch.negative) return notIdealQueried
        val text: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val prefixMatched: List<InternalLexicon> = segmentation.flatMap { scheme ->
                if (isActive().negative) return@flatMap emptyList<InternalLexicon>()
                val leadingCharCount: Int = scheme.size
                if (leadingCharCount <= 0 || leadingCharCount > MAX_CHAR_COUNT) return@flatMap emptyList()
                val tail: List<VirtualInputKey> = keys.drop(scheme.length)
                if (tail.isEmpty()) return@flatMap emptyList()
                val schemeAnchors: List<VirtualInputKey> = scheme.aliasAnchors
                val conjoined: List<VirtualInputKey> = schemeAnchors + tail
                val schemeSyllableText: String = scheme.syllableText
                val mark: String = scheme.mark + PresetString.SPACE + tail.joinToString(separator = PresetString.EMPTY) { it.text }
                val tailAsAnchorText: List<Char> = tail.mapNotNull { if (it == VirtualInputKey.letterY) VirtualInputKey.letterJ.text.firstOrNull() else it.text.firstOrNull() }
                val conjoinedMatched = anchorsMatch(keys = conjoined)
                        .mapNotNull { item ->
                                val toneFreeRomanization: String = item.romanization.strippedTones()
                                if (toneFreeRomanization.startsWith(schemeSyllableText).negative) return@mapNotNull null
                                val suffixAnchorText: List<Char> = toneFreeRomanization.drop(schemeSyllableText.length).split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }
                                if (suffixAnchorText != tailAsAnchorText) return@mapNotNull null
                                InternalLexicon(word = item.word, romanization = item.romanization, frequency = item.frequency, latest = item.latest, input = text, mark = mark)
                        }
                val transformedTailText: String = tail.mapIndexed { index, value -> if (index == 0 && value == VirtualInputKey.letterY) VirtualInputKey.letterJ.text else value.text }.joinToString(separator = PresetString.EMPTY)
                val syllableText: String = schemeSyllableText + PresetString.SPACE + transformedTailText
                val anchors: List<VirtualInputKey> = schemeAnchors + listOf(tail.first())
                val anchorsMatchedInScheme = anchorsMatch(keys = anchors)
                        .mapNotNull { item ->
                                if (item.romanization.strippedTones().startsWith(syllableText).negative) return@mapNotNull null
                                InternalLexicon(word = item.word, romanization = item.romanization, frequency = item.frequency, latest = item.latest, input = text, mark = mark)
                        }
                conjoinedMatched + anchorsMatchedInScheme
        }
        val gainedMatched: List<InternalLexicon> = (inputLength - 1 downTo 1).flatMap { number ->
                if (isActive().negative || (number > MAX_CHAR_COUNT)) return@flatMap emptyList<InternalLexicon>()
                anchorsMatch(keys = keys.take(number))
        }.mapNotNull { item ->
                val tail: List<VirtualInputKey> = keys.drop(item.inputCount - 1)
                if (tail.size > 6) return@mapNotNull null
                val converted: InternalLexicon by lazy { InternalLexicon(word = item.word, romanization = item.romanization, frequency = item.frequency, latest = item.latest, input = text, mark = text) }
                if (item.romanization.latinLetterOnly().startsWith(text)) return@mapNotNull converted
                val lastSyllable: String = item.romanization.split(PresetCharacter.SPACE).lastOrNull()?.filter { it.isCantoneseToneDigit.negative } ?: return@mapNotNull null
                val tailSyllable: String? = Segmenter.syllableText(keys = tail)
                if (tailSyllable != null) {
                        if (lastSyllable == tailSyllable) converted else null
                } else {
                        val tailText: String = tail.joinToString(separator = PresetString.EMPTY) { it.text }
                        if (lastSyllable.startsWith(tailText)) converted else null
                }
        }
        val partialMatched: List<Lexicon> = (prefixMatched + gainedMatched)
                .peculiarSorted()
                .take(5)
                .map { Lexicon(text = it.word, romanization = it.romanization, input = text, mark = it.mark, number = -1) }
        return partialMatched + notIdealQueried
}

private suspend fun InputMemoryHelper.query(segmentation: Segmentation, idealSchemes: List<Scheme>): List<InternalLexicon> {
        return if (idealSchemes.isEmpty()) {
                segmentation.flatMap { scheme -> perform(scheme = scheme) }
        } else {
                idealSchemes.flatMap { scheme ->
                        (scheme.size downTo 1).flatMap { number ->
                                perform(scheme = scheme.take(number), limit = if (number == scheme.size) 20 else 5)
                        }
                }
        }
}

private suspend fun InputMemoryHelper.perform(scheme: Scheme, limit: Int = 5): List<InternalLexicon> {
        if (isActive().negative) return emptyList()
        return spellMatch(keys = scheme.originKeys, complexity = scheme.complexity, input = scheme.aliasText, mark = scheme.mark, limit = limit)
}

private fun InputMemoryHelper.anchorsMatch(keys: List<VirtualInputKey>, input: String? = null, limit: Int? = null): List<InternalLexicon> {
        val anchorsCode: Long = keys.anchorNormalized.conjoinedCode
        val charCount: Long = keys.size.toLong()
        val limitValue: Int = limit ?: 100
        val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val items: MutableList<InternalLexicon> = mutableListOf()
        val command: String = "SELECT word, romanization, frequency, latest FROM memory2608 WHERE anchors = $anchorsCode AND char_count = $charCount ORDER BY frequency DESC LIMIT $limitValue;"
        readableDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val frequency = cursor.getLong(2)
                        val latest = cursor.getLong(3)
                        val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = inputText, mark = inputText)
                        items.add(instance)
                }
        }
        return items
}
private fun InputMemoryHelper.spellMatch(keys: List<VirtualInputKey>, complexity: Long, input: String? = null, mark: String? = null, limit: Int? = null): List<InternalLexicon> {
        val spell: Long = keys.conjoinedCode
        val letterCount: Long = keys.size.toLong()
        val limitValue: Int = limit ?: 100
        val inputText: String = input ?: keys.joinToString(separator = PresetString.EMPTY) { it.text }
        val items: MutableList<InternalLexicon> = mutableListOf()
        val command: String = "SELECT word, romanization, frequency, latest FROM memory2608 WHERE spell = $spell AND letter_count = $letterCount AND complexity = $complexity ORDER BY frequency DESC LIMIT $limitValue;"
        readableDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val frequency = cursor.getLong(2)
                        val latest = cursor.getLong(3)
                        val markText: String = mark ?: romanization.strippedTones()
                        val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = inputText, mark = markText)
                        items.add(instance)
                }
        }
        return items
}

fun InputMemoryHelper.nineKeySearch(combos: List<Combo>, segmentation: NineKeySegmentation): List<Lexicon> {
        if (InputMemoryHelper.isMigrating) return emptyList()
        if (combos.isEmpty()) return emptyList()
        val inputLength: Int = combos.size
        if (inputLength <= 1) {
                return nineKeyAnchorsMatch(combos = combos)
                        .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
        }
        val anchorsMatched = nineKeyAnchorsMatch(combos = combos).regularSorted(isOrdered = true)
        val queried = nineKeyQuery(inputLength = inputLength, segmentation = segmentation).regularSorted()
        val fullMatched = queried.filter { it.inputCount >= inputLength }.regularSorted()
        val ideal = (fullMatched.take(10) + (fullMatched + anchorsMatched.take(5)).regularSorted())
                .distinct()
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -1) }
        val notIdeal = queried.filter { it.inputCount < inputLength }
                .peculiarSorted()
                .take(6)
                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.input, mark = it.mark, number = -2) }
        return ideal + notIdeal
}
private fun InputMemoryHelper.nineKeyQuery(inputLength: Int, segmentation: NineKeySegmentation): List<InternalLexicon> {
        val idealSchemes = segmentation.filter { it.length == inputLength }
        return if (idealSchemes.isEmpty()) {
                segmentation.flatMap { scheme -> nineKeyPerform(scheme = scheme, limit = 10) }
        } else {
                idealSchemes.flatMap { scheme ->
                        when (scheme.size) {
                                0 -> emptyList()
                                1 -> nineKeyPerform(scheme = scheme, limit = 20)
                                else -> (scheme.size downTo 1).flatMap { number ->
                                        nineKeyPerform(scheme = scheme.take(number), limit = if (number == scheme.size) 30 else 10)
                                }
                        }
                }
        }
}
private fun InputMemoryHelper.nineKeyPerform(scheme: NineKeyScheme, limit: Int = 50): List<InternalLexicon> {
        val containsIrregular: Boolean = scheme.any { it.isIrregular }
        return if (containsIrregular) {
                spellMatch(keys = scheme.serialOriginKeys, complexity = scheme.complexity, limit = limit)
        } else {
                nineKeySpellMatch(combos = scheme.originCombos, complexity = scheme.complexity, limit = limit)
        }
}
private fun InputMemoryHelper.nineKeyAnchorsMatch(combos: List<Combo>, limit: Int = 50): List<InternalLexicon> {
        val code: Long = combos.decimalCombinedCode
        val charCount: Long = combos.size.toLong()
        val items: MutableList<InternalLexicon> = mutableListOf()
        val command: String = "SELECT word, romanization, frequency, latest FROM memory2608 WHERE anchors_9key = $code AND char_count = $charCount ORDER BY frequency DESC LIMIT $limit;"
        readableDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val frequency = cursor.getLong(2)
                        val latest = cursor.getLong(3)
                        val anchorText: String = romanization.split(PresetCharacter.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                        val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = anchorText, mark = anchorText)
                        items.add(instance)
                }
        }
        return items
}
private fun InputMemoryHelper.nineKeySpellMatch(combos: List<Combo>, complexity: Long, limit: Int = 50): List<InternalLexicon> {
        val code: Long = combos.decimalCombinedCode
        val letterCount: Long = combos.size.toLong()
        val items: MutableList<InternalLexicon> = mutableListOf()
        val command: String = "SELECT word, romanization, frequency, latest FROM memory2608 WHERE spell_9key = $code AND letter_count = $letterCount AND complexity = $complexity ORDER BY frequency DESC LIMIT $limit;"
        readableDatabase.rawQuery(command, null).use { cursor ->
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val frequency = cursor.getLong(2)
                        val latest = cursor.getLong(3)
                        val input: String = romanization.filter { it.isLowercaseBasicLatinLetter }
                        val mark: String = romanization.strippedTones()
                        val instance = InternalLexicon(word = word, romanization = romanization, frequency = frequency, latest = latest, input = input, mark = mark)
                        items.add(instance)
                }
        }
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

private fun List<InternalLexicon>.regularSorted(isOrdered: Boolean = false): List<InternalLexicon> {
        val frequencyPreferred = if (isOrdered) this else sortedByDescending{ it.frequency }
        val datePreferred = sortedByDescending { it.latest }
        return (frequencyPreferred.take(3) + datePreferred.take(5) + frequencyPreferred).distinct()
}
private fun List<InternalLexicon>.peculiarSorted(): List<InternalLexicon> {
        return map { it.inputCount }.distinct().sortedDescending().flatMap { inputCount -> this.filter { it.inputCount == inputCount }.regularSorted() }
}
