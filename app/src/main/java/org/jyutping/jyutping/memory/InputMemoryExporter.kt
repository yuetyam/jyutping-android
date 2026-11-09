package org.jyutping.jyutping.memory

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files

/** Writes the same standalone version-1 SQLite format as the iOS and macOS input methods. */
object InputMemoryExporter {
        /** [output] must not exist. Returns the number of exported rows; removes partial output on failure. */
        suspend fun export(helper: InputMemoryHelper, output: File): Long = withContext(Dispatchers.IO) {
                helper.withTransfer { exportReserved(helper, output) }
        }

        // The controller retains its reservation until provider publication finishes.
        internal suspend fun exportReserved(helper: InputMemoryHelper, output: File): Long = withContext(Dispatchers.IO) {
                require(!output.exists()) { "The export output must be a new file" }
                val protectedPaths = listOf("", "-journal", "-wal", "-shm").map { File(helper.databasePath.path + it).canonicalFile }
                require(output.canonicalFile !in protectedPaths) { "The live store and its sidecars cannot be export destinations" }
                helper.ensureReady().getOrThrow()
                currentCoroutineContext().ensureActive()
                val workspace = Files.createTempDirectory(helper.context.cacheDir.toPath(), "memory-export-").toFile()
                try {
                        val snapshot = File(workspace, "snapshot.sqlite3")
                        // language=TEXT
                        helper.writableDatabase.execSQL("VACUUM INTO ?;", arrayOf(snapshot.path))
                        currentCoroutineContext().ensureActive()
                        val count = InputMemoryFile.open(snapshot).use { source ->
                                InputMemoryFile.open(output, writable = true, create = true).use { destination ->
                                        destination.rawQuery("PRAGMA journal_mode = DELETE;", null).use { it.moveToFirst() }
                                        destination.beginTransaction()
                                        try {
                                                destination.execSQL(InputMemoryFile.SCHEMA)
                                                destination.version = 1
                                                var copiedCount = 0L
                                                destination.compileStatement("INSERT INTO input_memory (word, romanization, frequency, latest) VALUES (?, ?, ?, ?);").use { insert ->
                                                        source.rawQuery("SELECT word, romanization, frequency, latest FROM memory2608;", null).use { cursor ->
                                                                while (cursor.moveToNext()) {
                                                                        currentCoroutineContext().ensureActive()
                                                                        insert.clearBindings()
                                                                        insert.bindString(1, cursor.getString(0))
                                                                        insert.bindString(2, cursor.getString(1))
                                                                        insert.bindLong(3, cursor.getLong(2))
                                                                        insert.bindLong(4, cursor.getLong(3))
                                                                        insert.executeInsert()
                                                                        copiedCount++
                                                                }
                                                        }
                                                }
                                                if (copiedCount != InputMemoryFile.count(source, "memory2608")) throw InputMemoryValidationError()
                                                destination.execSQL("CREATE INDEX ix_input_memory_frequency ON input_memory (frequency);")
                                                currentCoroutineContext().ensureActive()
                                                destination.execSQL("CREATE INDEX ix_input_memory_latest ON input_memory (latest);")
                                                currentCoroutineContext().ensureActive()
                                                destination.setTransactionSuccessful()
                                                copiedCount
                                        } finally {
                                                destination.endTransaction()
                                        }
                                }
                        }
                        InputMemoryFile.open(output).use { db ->
                                InputMemoryFile.validate(db)
                                if (InputMemoryFile.count(db, "input_memory") != count) throw InputMemoryValidationError()
                                db.compileStatement("SELECT sql FROM sqlite_master WHERE type = 'table' AND name = 'input_memory';").use {
                                        if (it.simpleQueryForString() != InputMemoryFile.SCHEMA.dropLast(1)) throw InputMemoryValidationError()
                                }
                        }
                        currentCoroutineContext().ensureActive()
                        count
                } catch (error: Throwable) {
                        InputMemoryFile.delete(output)
                        throw error
                } finally {
                        workspace.deleteRecursively()
                }
        }
}
