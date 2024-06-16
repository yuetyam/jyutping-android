package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.search.Pronunciation

class DatabaseHelper(context: Context, databaseName: String) : SQLiteOpenHelper(context, databaseName, null, 3) {

        override fun onCreate(db: SQLiteDatabase) { }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

        override fun onOpen(db: SQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
        }

        fun search(text: String): CantoneseLexicon? {
                when (text.count()) {
                        0 -> return null
                        1 -> {
                                val romanizations = fetchRomanizations(text)
                                if (romanizations.isNotEmpty()) {
                                        val pronunciations = romanizations.map { romanization ->
                                                val homophones = fetchHomophones(romanization).filter { it != text }
                                                val collocations = fetchCollocations(word = text, romanization = romanization)
                                                Pronunciation(romanization = romanization, homophones = homophones, collocations = collocations)
                                        }
                                        return  CantoneseLexicon(text = text, pronunciations = pronunciations)
                                } else {
                                        val convertedText = text.convertedS2T()
                                        val altRomanizations = fetchRomanizations(convertedText)
                                        if (altRomanizations.isNotEmpty()) {
                                                val pronunciations = altRomanizations.map { romanization ->
                                                        val homophones = fetchHomophones(romanization).filter { it != convertedText }
                                                        val collocations = fetchCollocations(word = convertedText, romanization = romanization)
                                                        Pronunciation(romanization = romanization, homophones = homophones, collocations = collocations)
                                                }
                                                return CantoneseLexicon(text = convertedText, pronunciations = pronunciations)
                                        } else {
                                                return CantoneseLexicon(text)
                                        }
                                }
                        }
                        else -> {
                                val romanizations = fetchRomanizations(text)
                                if (romanizations.isNotEmpty()) {
                                        val pronunciations: List<Pronunciation> = romanizations.map { Pronunciation(it) }
                                        return CantoneseLexicon(text = text, pronunciations = pronunciations)
                                } else {
                                        val convertedText = text.convertedS2T()
                                        val altRomanizations = fetchRomanizations(convertedText)
                                        if (altRomanizations.isNotEmpty()) {
                                                val pronunciations: List<Pronunciation> = altRomanizations.map { Pronunciation(it) }
                                                return CantoneseLexicon(text = convertedText, pronunciations = pronunciations)
                                        } else {
                                                // TODO: Advanced Search
                                                return CantoneseLexicon(text)
                                        }
                                }
                        }
                }
        }

        fun fetchWords(romanization: String): List<String> {
                val words: MutableList<String> = mutableListOf()
                val command: String = "SELECT word FROM jyutpingtable WHERE romanization = '$romanization';"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        words.add(word)
                }
                cursor.close()
                return words
        }
        fun fetchRomanizations(word: String): List<String> {
                val romanizations: MutableList<String> = mutableListOf()
                val command: String = "SELECT romanization FROM jyutpingtable WHERE word = '$word';"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val romanization = cursor.getString(0)
                        romanizations.add(romanization)
                }
                cursor.close()
                return romanizations
        }
        fun fetchHomophones(romanization: String): List<String> {
                val words: MutableList<String> = mutableListOf()
                val command: String = "SELECT word FROM jyutpingtable WHERE romanization = '$romanization' LIMIT 11;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        words.add(word)
                }
                cursor.close()
                return words
        }
        fun fetchCollocations(word: String, romanization: String): List<String> {
                val command: String = "SELECT collocation FROM collocationtable WHERE word = '$word' AND romanization = '$romanization' LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        val text = cursor.getString(0)
                        cursor.close()
                        if (text == "X") {
                                return listOf()
                        } else {
                                return text.split(";")
                        }
                } else {
                        return listOf()
                }
        }
}
