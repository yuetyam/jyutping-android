package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jyutping.jyutping.UserSettingsKey
import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.Segmentation
import org.jyutping.jyutping.keyboard.length

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

        fun handle(candidate: Candidate) {
                val id: Int = (candidate.lexiconText + candidate.romanization).hashCode()
                val frequency = find(id)
                if (frequency != null) {
                        val newFrequency: Int = frequency + 1
                        update(id = id, frequency = newFrequency)
                } else {
                        val lexicon = UserLexicon.convert(candidate)
                        insert(lexicon)
                }
        }
        fun process(candidates: List<Candidate>) {
                val lexicon = UserLexicon.join(candidates)
                val id = lexicon.id
                val frequency = find(id)
                if (frequency != null) {
                        val newFrequency: Int = frequency + 1
                        update(id = id, frequency = newFrequency)
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
                val command: String = "UPDATE memory SET frequency = $frequency WHERE id = ${id};"
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
        fun deleteAll() {
                val command: String = "DELETE FROM memory;"
                this.writableDatabase.execSQL(command)
        }

        fun suggest(text: String, segmentation: Segmentation): List<Candidate> {
                val matches = query(text = text, input = text, isShortcut = false)
                val shortcuts = query(text = text, input = text, mark = text, isShortcut = true)
                val textLength = text.length
                val schemes = segmentation.filter { it.length() == textLength }
                if (schemes.isEmpty()) return matches + shortcuts
                val searches: MutableList<List<Candidate>> = mutableListOf()
                for (scheme in schemes) {
                        val pingText = scheme.joinToString(separator = String.empty) { it.origin }
                        val matched = query(text = pingText, input = text, isShortcut = false)
                        if (matched.isEmpty()) continue
                        val text2mark = scheme.joinToString(separator = String.space) { it.text }
                        val syllables = scheme.joinToString(separator = String.space) { it.origin }
                        val transformed = matched.filter { it.mark == syllables }.map { Candidate(text = it.text, romanization = it.romanization, input = it.input, mark = text2mark) }
                        searches.add(transformed)
                }
                return matches + shortcuts + searches.flatten()
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
                        val markText: String = mark ?: romanization.filterNot { it.isDigit() }
                        val instance = Candidate(text = word, romanization = romanization, input = input, mark = markText)
                        candidates.add(instance)
                }
                cursor.close()
                return candidates
        }
}
