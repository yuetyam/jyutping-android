package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.Elephant
import org.jyutping.jyutping.extensions.characterCount
import org.jyutping.jyutping.extensions.convertedS2T
import org.jyutping.jyutping.extensions.isIdeographicCodePoint
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.search.CantoneseLexicon
import org.jyutping.jyutping.search.ChoHokYuetYamCitYiu
import org.jyutping.jyutping.search.FanWanCuetYiu
import org.jyutping.jyutping.search.GwongWanCharacter
import org.jyutping.jyutping.search.Pronunciation
import org.jyutping.jyutping.search.YingWaaFanWan
import kotlin.math.max

object SearchHelper {
        fun searchCantoneseLexicon(text: String): CantoneseLexicon {
                when (text.characterCount) {
                        0 -> return CantoneseLexicon(text)
                        1 -> {
                                val romanizations = fetchRomanizations(text)
                                if (romanizations.isNotEmpty()) {
                                        val pronunciations = romanizations.map { romanization ->
                                                val homophones = fetchHomophones(romanization).filter { it != text }
                                                val collocations = fetchCollocations(word = text, romanization = romanization)
                                                val descriptions = fetchDescriptions(word = text, romanization = romanization)
                                                Pronunciation(romanization = romanization, homophones = homophones, collocations = collocations, descriptions = descriptions)
                                        }
                                        val unihanDefinition = unihanDefinitionMatch(text)
                                        return  CantoneseLexicon(text = text, pronunciations = pronunciations, unihanDefinition = unihanDefinition)
                                }
                                val convertedText = text.convertedS2T()
                                val altRomanizations = fetchRomanizations(convertedText)
                                if (altRomanizations.isNotEmpty()) {
                                        val pronunciations = altRomanizations.map { romanization ->
                                                val homophones = fetchHomophones(romanization).filter { it != convertedText }
                                                val collocations = fetchCollocations(word = convertedText, romanization = romanization)
                                                val descriptions = fetchDescriptions(word = convertedText, romanization = romanization)
                                                Pronunciation(romanization = romanization, homophones = homophones, collocations = collocations, descriptions = descriptions)
                                        }
                                        val unihanDefinition = unihanDefinitionMatch(convertedText)
                                        return CantoneseLexicon(text = convertedText, pronunciations = pronunciations, unihanDefinition = unihanDefinition)
                                }
                                return CantoneseLexicon(text)
                        }
                        else -> {
                                val romanizations = fetchRomanizations(text)
                                if (romanizations.isNotEmpty()) {
                                        val pronunciations: List<Pronunciation> = romanizations.map {
                                                val descriptions = fetchDescriptions(word = text, romanization = it)
                                                Pronunciation(romanization = it, descriptions = descriptions)
                                        }
                                        return CantoneseLexicon(text = text, pronunciations = pronunciations)
                                }
                                val convertedText = text.convertedS2T()
                                val altRomanizations = fetchRomanizations(convertedText)
                                if (altRomanizations.isNotEmpty()) {
                                        val pronunciations: List<Pronunciation> = altRomanizations.map {
                                                val descriptions = fetchDescriptions(word = convertedText, romanization = it)
                                                Pronunciation(romanization = it, descriptions = descriptions)
                                        }
                                        return CantoneseLexicon(text = convertedText, pronunciations = pronunciations)
                                }
                                val ideographicCount = text.codePoints().toArray().count { it.isIdeographicCodePoint }
                                if (ideographicCount == 0) return CantoneseLexicon(text)
                                var chars = text
                                val fetches: MutableList<String> = mutableListOf()
                                var newText = ""
                                while (chars.isNotEmpty()) {
                                        val leading = fetchLeading(chars)
                                        val leadingRomanization = leading.first
                                        if (leadingRomanization != null) {
                                                fetches.add(leadingRomanization)
                                                val leadLength: Int = max(1, leading.second)
                                                newText += chars.take(leadLength)
                                                chars = chars.drop(leadLength)
                                        } else {
                                                val traditionalChars = chars.convertedS2T()
                                                val anotherLeading = fetchLeading(traditionalChars)
                                                val anotherRomanization = anotherLeading.first
                                                if (anotherRomanization != null) {
                                                        fetches.add(anotherRomanization)
                                                        val leadLength: Int = max(1, anotherLeading.second)
                                                        newText += traditionalChars.take(leadLength)
                                                        chars = traditionalChars.drop(leadLength)
                                                } else {
                                                        val leadingChar = chars.first()
                                                        val symbol: String = if (leadingChar.code.isIdeographicCodePoint) "?" else leadingChar.toString()
                                                        fetches.add(symbol)
                                                        newText += leadingChar
                                                        chars = chars.drop(1)
                                                }
                                        }
                                }
                                if (fetches.isEmpty()) return CantoneseLexicon(text)
                                val romanization = fetches.joinToString(separator = PresetString.SPACE)
                                val descriptions = fetchDescriptions(word = newText, romanization = romanization)
                                val pronunciation = Pronunciation(romanization = romanization, descriptions = descriptions)
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
                return if (romanization == null) Pair(null, 0) else Pair(romanization, matchedCount)
        }
        private fun fetchRomanizations(word: String): List<String> {
                val romanizations: MutableList<String> = mutableListOf()
                val command = "SELECT romanization FROM core_lexicon WHERE word = ? ORDER BY rowid;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(word)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val romanization = cursor.getString(0)
                                romanizations.add(romanization)
                        }
                }
                return romanizations
        }
        private fun fetchHomophones(romanization: String): List<String> {
                val words: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM core_lexicon WHERE romanization = ? ORDER BY rowid LIMIT 11;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(romanization)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                words.add(word)
                        }
                }
                return words
        }
        private fun fetchCollocations(word: String, romanization: String): List<String> {
                var items: List<String> = emptyList()
                val command = "SELECT collocation FROM collocation_table WHERE word = ? AND romanization = ? ORDER BY rowid LIMIT 1;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(word, romanization)).use { cursor ->
                        if (cursor.moveToFirst()) {
                                val text = cursor.getString(0)
                                if (text != "X") {
                                        items = text.split(";")
                                }
                        }
                }
                return items
        }
        private fun fetchDescriptions(word: String, romanization: String): List<String> {
                val items: MutableList<String> = mutableListOf()
                val command = "SELECT description FROM dictionary_table WHERE word = ? AND romanization = ? ORDER BY rowid;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(word, romanization)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val item = cursor.getString(0)
                                items.add(item)
                        }
                }
                return items
        }

        fun searchYingWaaFanWan(text: String): List<YingWaaFanWan> {
                val matched = yingWaaFanWanMatch(text)
                if (matched.isNotEmpty()) return matched
                val traditionalText = text.convertedS2T()
                return yingWaaFanWanMatch(traditionalText)
        }
        private fun yingWaaFanWanMatch(text: String): List<YingWaaFanWan> {
                val entries: MutableList<YingWaaFanWan> = mutableListOf()
                val code = text.codePointAt(0)
                val command = "SELECT * FROM yingwaa_table WHERE code = $code;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
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
                }
                return entries
        }
        private fun fetchYingWaaHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM yingwaa_table WHERE romanization = ? LIMIT 11;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(romanization)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                homophones.add(word)
                        }
                }
                return homophones
        }

        fun searchChoHokYuetYamCitYiu(text: String): List<ChoHokYuetYamCitYiu> {
                val matched = choHokYuetYamCitYiuMatch(text)
                if (matched.isNotEmpty()) return matched
                val traditionalText = text.convertedS2T()
                return choHokYuetYamCitYiuMatch(traditionalText)
        }
        private fun choHokYuetYamCitYiuMatch(text: String): List<ChoHokYuetYamCitYiu> {
                val entries: MutableList<ChoHokYuetYamCitYiu> = mutableListOf()
                val code = text.codePointAt(0)
                val command = "SELECT * FROM chohok_table WHERE code = $code;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
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
                }
                return entries
        }
        private fun fetchChoHokHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM chohok_table WHERE romanization = ? LIMIT 11;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(romanization)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                homophones.add(word)
                        }
                }
                return homophones
        }

        fun searchFanWanCuetYiu(text: String): List<FanWanCuetYiu> {
                val matched = fanWanCuetYiuMatch(text)
                if (matched.isNotEmpty()) return matched
                val traditionalText = text.convertedS2T()
                return fanWanCuetYiuMatch(traditionalText)
        }
        private fun fanWanCuetYiuMatch(text: String): List<FanWanCuetYiu> {
                val entries: MutableList<FanWanCuetYiu> = mutableListOf()
                val code = text.codePointAt(0)
                val command = "SELECT * FROM fanwan_table WHERE code = $code;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
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
                }
                return entries
        }
        private fun fetchFanWanHomophones(romanization: String): List<String> {
                val homophones: MutableList<String> = mutableListOf()
                val command = "SELECT word FROM fanwan_table WHERE romanization = ? LIMIT 11;"
                Elephant.sharedDatabase.rawQuery(command, arrayOf(romanization)).use { cursor ->
                        while (cursor.moveToNext()) {
                                val word = cursor.getString(0)
                                homophones.add(word)
                        }
                }
                return homophones
        }

        fun searchGwongWan(text: String): List<GwongWanCharacter> {
                val matched = gwongWanMatch(text)
                if (matched.isNotEmpty()) return matched
                val traditionalText = text.convertedS2T()
                return gwongWanMatch(traditionalText)
        }
        private fun gwongWanMatch(text: String): List<GwongWanCharacter> {
                val entries: MutableList<GwongWanCharacter> = mutableListOf()
                val code = text.codePointAt(0)
                val command = "SELECT * FROM gwongwan_table WHERE code = $code;"
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
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
                }
                return entries
        }

        private fun unihanDefinitionMatch(text: String): String? {
                if (text.isEmpty()) return null
                val code = text.codePointAt(0)
                val command = "SELECT definition FROM definition_table WHERE code = $code LIMIT 1;"
                var definition: String? = null
                Elephant.sharedDatabase.rawQuery(command, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                                definition = cursor.getString(0)
                        }
                }
                return definition
        }
}
