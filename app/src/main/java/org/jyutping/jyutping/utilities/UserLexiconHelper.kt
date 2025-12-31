package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jyutping.jyutping.UserSettingsKey
import org.jyutping.jyutping.extensions.isCantoneseToneDigit
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.models.Segmentation
import org.jyutping.jyutping.models.length
import org.jyutping.jyutping.models.mark
import org.jyutping.jyutping.models.originText
import org.jyutping.jyutping.models.syllableText

class UserLexiconHelper(context: Context) : SQLiteOpenHelper(context, UserSettingsKey.UserLexiconDatabaseFileName, null, 3) {

        private val createTable: String = "CREATE TABLE IF NOT EXISTS memory(id INTEGER NOT NULL PRIMARY KEY,word TEXT NOT NULL,romanization TEXT NOT NULL,shortcut INTEGER NOT NULL,ping INTEGER NOT NULL,frequency INTEGER NOT NULL,latest INTEGER NOT NULL);"

        override fun onCreate(db: SQLiteDatabase?) {
                db?.execSQL(createTable)
        }
        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }
        override fun onOpen(db: SQLiteDatabase?) {
                super.onOpen(db)
                db?.execSQL(createTable)
        }

        fun process(candidates: List<Candidate>) {
                val isNotAllCantonese = candidates.count { it.type.isNotCantonese() } > 0
                if (isNotAllCantonese) return
                val lexicon = UserLexicon.join(candidates)
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

        fun remove(candidate: Candidate) {
                if (candidate.type.isNotCantonese()) return
                val id: Int = (candidate.lexiconText + candidate.romanization).hashCode()
                val command: String = "DELETE FROM memory WHERE id = ${id};"
                this.writableDatabase.execSQL(command)
        }
        fun deleteAll() {
                val command: String = "DELETE FROM memory;"
                this.writableDatabase.execSQL(command)
        }

        fun suggest(text: String, segmentation: Segmentation): List<Candidate> {
                val matches = query(text = text, input = text, isShortcut = false)
                val shortcuts = query(text = text, input = text, mark = text, isShortcut = true)
                val textLength = text.length
                val schemes = segmentation.filter { it.length == textLength }
                if (schemes.isEmpty()) return matches + shortcuts
                val searches = schemes.flatMap { scheme ->
                        val matched = query(text = scheme.originText, input = text, isShortcut = false)
                        if (matched.isEmpty()) return@flatMap emptyList()
                        val mark = scheme.mark
                        val syllableText = scheme.syllableText
                        val transformed = matched.mapNotNull { if (it.mark == syllableText) Candidate(text = it.text, romanization = it.romanization, input = text, mark = mark) else null }
                        return@flatMap transformed
                }
                return matches + shortcuts + searches
        }
        private fun query(text: String, input: String, mark: String? = null, isShortcut: Boolean): List<Candidate> {
                val candidates: MutableList<Candidate> = mutableListOf()
                val code: Int = if (isShortcut) text.replace("y", "j").hashCode() else text.hashCode()
                val column: String = if (isShortcut) "shortcut" else "ping"
                val command: String = "SELECT word, romanization FROM memory WHERE $column = $code ORDER BY frequency DESC LIMIT 5;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        val romanization = cursor.getString(1)
                        val markText: String = mark ?: romanization.filterNot { it.isCantoneseToneDigit }
                        val instance = Candidate(text = word, romanization = romanization, input = input, mark = markText)
                        candidates.add(instance)
                }
                cursor.close()
                return candidates
        }
}
