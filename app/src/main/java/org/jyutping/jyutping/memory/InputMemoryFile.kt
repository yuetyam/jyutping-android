package org.jyutping.jyutping.memory

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.IOException

internal class InputMemoryBusyError : IOException()
internal class InputMemoryValidationError : IOException()

internal object InputMemoryFile {
        const val SCHEMA = "CREATE TABLE input_memory (word TEXT NOT NULL, romanization TEXT NOT NULL, frequency INTEGER NOT NULL, latest INTEGER NOT NULL, UNIQUE (word, romanization));"
        const val UPSERT = "INSERT INTO memory2608 (word, romanization, frequency, latest, char_count, letter_count, complexity, anchors, spell, anchors_9key, spell_9key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (word, romanization) DO UPDATE SET frequency = MAX(frequency, excluded.frequency), latest = MAX(latest, excluded.latest);"

        fun open(file: File, writable: Boolean = false, create: Boolean = false): SQLiteDatabase {
                val flags = SQLiteDatabase.NO_LOCALIZED_COLLATORS or (if (writable) SQLiteDatabase.OPEN_READWRITE else SQLiteDatabase.OPEN_READONLY) or (if (create) SQLiteDatabase.CREATE_IF_NECESSARY else 0)
                return SQLiteDatabase.openDatabase(file.path, null, flags)
        }

        suspend fun validate(db: SQLiteDatabase) {
                currentCoroutineContext().ensureActive()
                if (db.version != 1) throw InputMemoryValidationError()
                db.rawQuery("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = 'input_memory';", null).use {
                        if (!it.moveToFirst()) throw InputMemoryValidationError()
                }
                val columns = mutableSetOf<String>()
                db.rawQuery("PRAGMA table_info(input_memory);", null).use {
                        while (it.moveToNext()) columns.add(it.getString(1))
                }
                if (!columns.containsAll(listOf("word", "romanization", "frequency", "latest"))) throw InputMemoryValidationError()
                db.rawQuery("PRAGMA integrity_check;", null).use {
                        if (!it.moveToFirst() || it.getString(0) != "ok" || it.moveToNext()) throw InputMemoryValidationError()
                }
                currentCoroutineContext().ensureActive()
        }

        fun count(db: SQLiteDatabase, table: String): Long = db.compileStatement("SELECT COUNT(*) FROM $table;").use { it.simpleQueryForLong() }

        fun delete(file: File) {
                for (suffix in listOf("", "-journal", "-wal", "-shm")) File(file.path + suffix).delete()
        }

        fun bind(statement: SQLiteStatement, entry: MemoryLexicon) {
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
        }
}
