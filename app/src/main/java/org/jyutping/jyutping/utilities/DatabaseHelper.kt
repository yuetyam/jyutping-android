package org.jyutping.jyutping.utilities

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jyutping.jyutping.extensions.charcode
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.extensions.intercode
import org.jyutping.jyutping.extensions.isIdeographic
import org.jyutping.jyutping.extensions.shortcutCharcode
import org.jyutping.jyutping.keyboard.Candidate
import org.jyutping.jyutping.keyboard.SegmentToken
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.search.ChoHokYuetYamCitYiu
import org.jyutping.jyutping.search.FanWanCuetYiu
import org.jyutping.jyutping.search.GwongWanCharacter
import org.jyutping.jyutping.search.Pronunciation
import org.jyutping.jyutping.search.UnihanDefinition
import org.jyutping.jyutping.search.YingWaaFanWan
import kotlin.math.max

class DatabaseHelper(context: Context, databaseName: String) : SQLiteOpenHelper(context, databaseName, null, 3) {

        override fun onCreate(db: SQLiteDatabase) { }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) { }

        override fun onOpen(db: SQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
        }

        fun searchCantoneseLexicon(text: String): CantoneseLexicon? {
                when (text.length) {
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
                                }
                                val convertedText = text.convertedS2T()
                                val altRomanizations = fetchRomanizations(convertedText)
                                if (altRomanizations.isNotEmpty()) {
                                        val pronunciations = altRomanizations.map { romanization ->
                                                val homophones = fetchHomophones(romanization).filter { it != convertedText }
                                                val collocations = fetchCollocations(word = convertedText, romanization = romanization)
                                                Pronunciation(romanization = romanization, homophones = homophones, collocations = collocations)
                                        }
                                        return CantoneseLexicon(text = convertedText, pronunciations = pronunciations)
                                }
                                return CantoneseLexicon(text)
                        }
                        else -> {
                                val romanizations = fetchRomanizations(text)
                                if (romanizations.isNotEmpty()) {
                                        val pronunciations: List<Pronunciation> = romanizations.map { Pronunciation(it) }
                                        return CantoneseLexicon(text = text, pronunciations = pronunciations)
                                }
                                val convertedText = text.convertedS2T()
                                val altRomanizations = fetchRomanizations(convertedText)
                                if (altRomanizations.isNotEmpty()) {
                                        val pronunciations: List<Pronunciation> = altRomanizations.map { Pronunciation(it) }
                                        return CantoneseLexicon(text = convertedText, pronunciations = pronunciations)
                                }
                                val firstIdeographic = text.firstOrNull { it.isIdeographic() }
                                if (firstIdeographic == null) return CantoneseLexicon(text)
                                var chars = text
                                val fetches: MutableList<String> = mutableListOf()
                                var newText = ""
                                while (chars.isNotEmpty()) {
                                        val leading = fetchLeading(chars)
                                        val leadingRomanization = leading.first
                                        if (leadingRomanization != null) {
                                                fetches.add(leadingRomanization)
                                                val leadLength: Int = max(1, leading.second)
                                                val tailLength = chars.length - leadLength
                                                newText += chars.dropLast(tailLength)
                                                chars = chars.drop(leadLength)
                                        } else {
                                                val traditionalChars = chars.convertedS2T()
                                                val anotherLeading = fetchLeading(traditionalChars)
                                                val anotherRomanization = anotherLeading.first
                                                if (anotherRomanization != null) {
                                                        fetches.add(anotherRomanization)
                                                        val leadLength: Int = max(1, anotherLeading.second)
                                                        val tailLength = traditionalChars.length - leadLength
                                                        newText += traditionalChars.dropLast(tailLength)
                                                        chars = traditionalChars.drop(leadLength)
                                                } else {
                                                        val leadingChar = chars.first()
                                                        val symbol: String = if (leadingChar.isIdeographic()) "?" else leadingChar.toString()
                                                        fetches.add(symbol)
                                                        newText += leadingChar
                                                        chars = chars.drop(1)
                                                }
                                        }
                                }
                                if (fetches.isEmpty()) return CantoneseLexicon(text)
                                val romanization = fetches.joinToString(separator = " ")
                                val pronunciation = Pronunciation(romanization)
                                return CantoneseLexicon(text = newText, pronunciations = listOf(pronunciation))
                        }
                }
        }
        private fun fetchLeading(text: String): Pair<String?, Int> {
                var chars = text
                var romanization: String? = null
                var matchedCount = 0
                while (romanization == null && chars.isNotEmpty()) {
                        romanization = fetchRomanizations(chars).firstOrNull()
                        matchedCount = chars.length
                        chars = chars.dropLast(1)
                }
                if (romanization != null) {
                        return Pair(romanization, matchedCount)
                }
                return Pair(null, 0)
        }
        private fun fetchRomanizations(word: String): List<String> {
                val romanizations: MutableList<String> = mutableListOf()
                val command = "SELECT romanization FROM lexicontable WHERE word = '$word';"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val romanization = cursor.getString(0)
                        romanizations.add(romanization)
                }
                cursor.close()
                return romanizations
        }
        private fun fetchHomophones(romanization: String): List<String> {
                val words: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM lexicontable WHERE romanization = '$romanization' LIMIT 11;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        words.add(word)
                }
                cursor.close()
                return words
        }
        private fun fetchCollocations(word: String, romanization: String): List<String> {
                val command = "SELECT collocation FROM collocationtable WHERE word = '$word' AND romanization = '$romanization' LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        val text = cursor.getString(0)
                        cursor.close()
                        if (text == "X") return listOf()
                        return text.split(";")
                }
                return listOf()
        }

        fun matchYingWaaFanWan(char: Char): List<YingWaaFanWan> {
                val entries: MutableList<YingWaaFanWan> = mutableListOf()
                val code = char.code
                val command = "SELECT * FROM yingwaatable WHERE code = $code;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        // val code = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val romanization = cursor.getString(2)
                        val pronunciation = cursor.getString(3)
                        val pronunciationMark = cursor.getString(4)
                        val interpretation = cursor.getString(5)
                        val homophones = fetchYingWaaHomophones(romanization).filter { it != word }
                        val instance = YingWaaFanWan(
                                word = word,
                                romanization = romanization,
                                pronunciation = pronunciation,
                                pronunciationMark = if (pronunciationMark == "X") null else pronunciationMark,
                                interpretation = if (interpretation == "X") null else interpretation,
                                homophones = homophones
                        )
                        entries.add(instance)
                }
                cursor.close()
                return entries
        }
        private fun fetchYingWaaHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM yingwaatable WHERE romanization = '$romanization' LIMIT 11;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        homophones.add(word)
                }
                cursor.close()
                return homophones
        }

        fun matchChoHokYuetYamCitYiu(char: Char): List<ChoHokYuetYamCitYiu> {
                val entries: MutableList<ChoHokYuetYamCitYiu> = mutableListOf()
                val code = char.code
                val command = "SELECT * FROM chohoktable WHERE code = $code;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        // val code = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val romanization = cursor.getString(2)
                        val initial = cursor.getString(3)
                        val final = cursor.getString(4)
                        val tone = cursor.getString(5)
                        val faancit = cursor.getString(6)
                        val homophones = fetchChoHokHomophones(romanization).filter { it != word }
                        val convertedInitial: String = if (initial == "X") "" else initial
                        val convertedFinal: String = if (final == "X") "" else final
                        val pronunciation: String = convertedInitial + convertedFinal
                        val faancitText: String = faancit + "切"
                        val instance = ChoHokYuetYamCitYiu(
                                word = word,
                                pronunciation = pronunciation,
                                tone = tone,
                                faancit = faancitText,
                                romanization = romanization,
                                homophones = homophones
                        )
                        entries.add(instance)
                }
                cursor.close()
                return entries
        }
        private fun fetchChoHokHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM chohoktable WHERE romanization = '$romanization' LIMIT 11;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        homophones.add(word)
                }
                cursor.close()
                return homophones
        }

        fun matchFanWanCuetYiu(char: Char): List<FanWanCuetYiu> {
                val entries: MutableList<FanWanCuetYiu> = mutableListOf()
                val code = char.code
                val command = "SELECT * FROM fanwantable WHERE code = $code;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        // val code = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val romanization = cursor.getString(2)
                        val initial = cursor.getString(3)
                        val final = cursor.getString(4)
                        val yamyeung = cursor.getString(5)
                        val tone = cursor.getString(6)
                        val rhyme = cursor.getString(7)
                        val interpretation = cursor.getString(8)
                        val pronunciation = "${initial}母　${final}韻　$yamyeung$tone　${rhyme}小韻"
                        val convertedRomanization = romanization
                                .replace(Regex("7$"), "1")
                                .replace(Regex("8$"), "3")
                                .replace(Regex("9$"), "6")
                        val homophones = fetchFanWanHomophones(romanization).filter { it != word }
                        val processedInterpretation = if (interpretation == "X") "(None)" else interpretation
                        val instance = FanWanCuetYiu(
                                word = word,
                                pronunciation = pronunciation,
                                romanization = convertedRomanization,
                                homophones = homophones,
                                interpretation = processedInterpretation
                        )
                        entries.add(instance)
                }
                cursor.close()
                return entries
        }
        private fun fetchFanWanHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM fanwantable WHERE romanization = '$romanization' LIMIT 11;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val word = cursor.getString(0)
                        homophones.add(word)
                }
                cursor.close()
                return homophones
        }

        fun matchGwongWan(char: Char): List<GwongWanCharacter> {
                val entries: MutableList<GwongWanCharacter> = mutableListOf()
                val code = char.code
                val command = "SELECT * FROM gwongwantable WHERE code = $code;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        // val code = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val rhyme = cursor.getString(2)
                        // val subRime = cursor.getString(3)
                        // val subRhymeSerial = cursor.getInt(4)
                        // val subRhymeNumber = cursor.getInt(5)
                        val upper = cursor.getString(6)
                        val lower = cursor.getString(7)
                        val initial = cursor.getString(8)
                        val rounding = cursor.getString(9)
                        val division = cursor.getString(10)
                        // val rhymeClass = cursor.getString(11)
                        // val repeating = cursor.getString(12)
                        val tone = cursor.getString(13)
                        val interpretation = cursor.getString(14)
                        val faancit = upper + lower + "切"
                        val hasDivision = division != "X"
                        val hasRounding = rounding != "X"
                        val tailText: String = when {
                                hasDivision && hasRounding -> "　${division}等　${rounding}口"
                                hasDivision && !hasRounding -> "　${division}等"
                                !hasDivision && hasRounding -> "　${rounding}口"
                                else -> ""
                        }
                        val hierarchy = "${initial}母　${rhyme}韻　${tone}聲${tailText}"
                        val pronunciation = "$faancit　$hierarchy"
                        val instance = GwongWanCharacter(word = word, pronunciation = pronunciation, interpretation = interpretation)
                        entries.add(instance)
                }
                cursor.close()
                return entries
        }

        fun matchUnihanDefinition(text: String): UnihanDefinition? {
                if (text.length != 1) return null
                val character = text.first()
                val code = character.code
                val command = "SELECT definition FROM definitiontable WHERE code = $code LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        val definition = cursor.getString(0)
                        cursor.close()
                        return UnihanDefinition(character = character, definition = definition)
                }
                cursor.close()
                return null
        }

        fun canProcess(text: String): Boolean {
                val value = text.firstOrNull()?.intercode() ?: 0
                if (value == 0) return false
                val code = if (value == 44) 29 else value // Replace 'y' with 'j'
                val command = "SELECT rowid FROM lexicontable WHERE shortcut = $code LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        cursor.close()
                        return true
                } else {
                        return false
                }
        }
        fun shortcut(text: String): List<Candidate> {
                val code: Int = text.shortcutCharcode() ?: 0
                if (code == 0) return emptyList()
                val candidates: MutableList<Candidate> = mutableListOf()
                val command = "SELECT rowid, word, romanization FROM lexicontable WHERE shortcut = $code LIMIT 50;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val order = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val romanization = cursor.getString(2)
                        val candidate = Candidate(text = word, romanization = romanization, input = text, order = order)
                        candidates.add(candidate)
                }
                cursor.close()
                return candidates
        }
        fun match(text: String, input: String, mark: String? = null): List<Candidate> {
                if (text.isBlank()) return emptyList()
                val code: Int = text.hashCode()
                val candidates: MutableList<Candidate> = mutableListOf()
                val command = "SELECT rowid, word, romanization FROM lexicontable WHERE ping = ${code};"
                val cursor = this.readableDatabase.rawQuery(command, null)
                while (cursor.moveToNext()) {
                        val order = cursor.getInt(0)
                        val word = cursor.getString(1)
                        val romanization = cursor.getString(2)
                        val markText = mark ?: romanization.filter { it.isDigit().not() }
                        val candidate = Candidate(text = word, romanization = romanization, input = input, mark = markText, order = order)
                        candidates.add(candidate)
                }
                cursor.close()
                return candidates
        }
        fun matchSyllable(text: String): SegmentToken? {
                val code = text.charcode() ?: 0
                if (code == 0) return null
                var token: SegmentToken? = null
                val command = "SELECT token, origin FROM syllabletable WHERE code = $code LIMIT 1;"
                val cursor = this.readableDatabase.rawQuery(command, null)
                if (cursor.moveToFirst()) {
                        val tokenText = cursor.getString(0)
                        val origin = cursor.getString(1)
                        token = SegmentToken(text = tokenText, origin = origin)
                }
                cursor.close()
                return token
        }
}
