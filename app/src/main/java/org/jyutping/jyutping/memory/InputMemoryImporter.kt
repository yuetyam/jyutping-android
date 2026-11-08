package org.jyutping.jyutping.memory

import android.database.Cursor
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter

sealed class InputMemoryImportError : Exception() {
        // Each throw needs a fresh exception with its own stack trace.
        @Suppress("CanSealedSubClassBeObject")
        class UnsupportedFormat : InputMemoryImportError()
}

object InputMemoryImporter {
        /** Consumes a private copy of an export. Merges valid rows atomically, then deletes the copy. */
        suspend fun import(helper: InputMemoryHelper, file: File): Long = withContext(Dispatchers.IO) {
                requirePrivateCopy(helper, file)
                try {
                        helper.withTransfer { importReserved(helper, file) }
                } finally {
                        InputMemoryFile.delete(file)
                }
        }

        internal suspend fun importReserved(helper: InputMemoryHelper, file: File): Long {
                requirePrivateCopy(helper, file)
                currentCoroutineContext().ensureActive()
                val source = try {
                        InputMemoryFile.open(file, writable = true).also { db ->
                                try {
                                        db.rawQuery("PRAGMA journal_mode = DELETE;", null).use { it.moveToFirst() }
                                        InputMemoryFile.validate(db)
                                } catch (error: Throwable) {
                                        db.close()
                                        throw error
                                }
                        }
                } catch (error: CancellationException) {
                        throw error
                } catch (_: Exception) {
                        throw InputMemoryImportError.UnsupportedFormat()
                }
                return source.use {
                        helper.ensureReady().getOrThrow()
                        val destination = helper.writableDatabase
                        destination.beginTransactionNonExclusive()
                        try {
                                var count = 0L
                                destination.compileStatement(InputMemoryFile.UPSERT).use { upsert ->
                                        source.rawQuery("SELECT word, romanization, frequency, latest FROM input_memory;", null).use { cursor ->
                                                while (cursor.moveToNext()) {
                                                        currentCoroutineContext().ensureActive()
                                                        val entry = entry(cursor) ?: continue
                                                        InputMemoryFile.bind(upsert, entry)
                                                        upsert.executeInsert()
                                                        count++
                                                }
                                        }
                                }
                                currentCoroutineContext().ensureActive()
                                destination.setTransactionSuccessful()
                                count
                        } finally {
                                destination.endTransaction()
                        }
                }
        }

        private fun requirePrivateCopy(helper: InputMemoryHelper, file: File) {
                val protectedPaths = listOf("", "-journal", "-wal", "-shm").map { File(helper.databasePath.path + it).canonicalFile }
                require(file.canonicalFile !in protectedPaths) { "Import requires a private copy" }
        }

        private fun entry(cursor: Cursor): MemoryLexicon? {
                if (cursor.getType(0) != Cursor.FIELD_TYPE_STRING || cursor.getType(1) != Cursor.FIELD_TYPE_STRING || cursor.getType(2) != Cursor.FIELD_TYPE_INTEGER || cursor.getType(3) != Cursor.FIELD_TYPE_INTEGER) return null
                val word = cursor.getString(0)
                val romanization = cursor.getString(1)
                val frequency = cursor.getLong(2)
                val latest = cursor.getLong(3)
                if (word.isEmpty() || frequency <= 0 || latest <= 0) return null
                if (!romanization.any { it.isLowercaseBasicLatinLetter } || !romanization.all { it.isLowercaseBasicLatinLetter || it.isCantoneseToneDigit || it == ' ' }) return null
                return MemoryLexicon.new(word, romanization, frequency, latest)
        }
}
