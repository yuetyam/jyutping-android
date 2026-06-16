package org.jyutping.preparing

import java.io.InputStream
import java.sql.DriverManager
import kotlin.use

object AppDataPreparer {
        fun prepare(url: String) {
                createCollocationTable(url)
                createDictionaryTable(url)
                createDefinitionTable(url)
                createYingWaaTable(url)
                createChoHokTable(url)
                createFanWanTable(url)
                createGwongWanTable(url)
                createIndexes(url)
        }
        private fun createIndexes(url: String) {
                val commands: List<String> = listOf(
                        "CREATE INDEX ix_collocation_word_romanization ON collocation_table (word, romanization);",

                        "CREATE INDEX ix_dictionary_word_romanization ON dictionary_table (word, romanization);",

                        "CREATE INDEX ix_yingwaa_code ON yingwaa_table(code);",
                        "CREATE INDEX ix_yingwaa_romanization ON yingwaa_table(romanization);",

                        "CREATE INDEX ix_chohok_code ON chohok_table(code);",
                        "CREATE INDEX ix_chohok_romanization ON chohok_table(romanization);",

                        "CREATE INDEX ix_fanwan_code ON fanwan_table(code);",
                        "CREATE INDEX ix_fanwan_romanization ON fanwan_table(romanization);",

                        "CREATE INDEX ix_gwongwan_code ON gwongwan_table(code);",
                )
                DriverManager.getConnection(url).use { connection ->
                        connection.createStatement().use { statement ->
                                for (command in commands) {
                                        statement.executeUpdate(command)
                                }
                        }
                }
                println("Created app data indexes.")
        }

        @Deprecated(message = "Use core_lexicon table instead")
        private fun createJyutpingTable(url: String) {}

        private fun createCollocationTable(url: String) {
                val createTableCommand: String = "CREATE TABLE collocation_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, collocation TEXT NOT NULL, UNIQUE (word, romanization));"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("collocation.txt") ?: error("Can not load collocation.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO collocation_table (word, romanization, collocation) VALUES (?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 3) error(badLineFormat)
                        val word = parts[0]
                        val romanization = parts[1]
                        val collocation = parts[2]
                        statement.setString(1, word)
                        statement.setString(2, romanization)
                        statement.setString(3, collocation)
                }
                connection.close()
                println("Inserted collocation entries: $insertedCount")
        }

        private fun createDictionaryTable(url: String) {
                val createTableCommand: String = "CREATE TABLE dictionary_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, description TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("wordshk.txt") ?: error("Can not load wordshk.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO dictionary_table (word, romanization, description) VALUES (?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 3) error(badLineFormat)
                        val word = parts[0]
                        val romanization = parts[1]
                        val description = parts[2]
                        statement.setString(1, word)
                        statement.setString(2, romanization)
                        statement.setString(3, description)
                }
                connection.close()
                println("Inserted dictionary entries: $insertedCount")
        }

        private fun createDefinitionTable(url: String) {
                val createTableCommand: String = "CREATE TABLE definition_table (code INTEGER PRIMARY KEY, definition TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val entries = UnihanDefinition.generate()
                val insertEntryCommand: String = "INSERT INTO definition_table (code, definition) VALUES (?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setInt(1, entry.first)
                        statement.setString(2, entry.second)
                }
                connection.close()
                println("Inserted definition entries: $insertedCount")
        }

        private fun createYingWaaTable(url: String) {
                val createTableCommand: String = "CREATE TABLE yingwaa_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, pronunciation TEXT NOT NULL, note TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("yingwaa.txt") ?: error("Can not load yingwaa.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO yingwaa_table (code, word, romanization, pronunciation, note, interpretation) VALUES (?, ?, ?, ?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 5) error(badLineFormat)
                        val word = parts[0]
                        if (word.isBlank()) error(badLineFormat)
                        val code = word.codePointAt(0)
                        val romanization = parts[1]
                        val pronunciation = parts[2]
                        val note = parts[3]
                        val interpretation = parts[4]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, romanization)
                        statement.setString(4, pronunciation)
                        statement.setString(5, note)
                        statement.setString(6, interpretation)
                }
                connection.close()
                println("Inserted yingwaa entries: $insertedCount")
        }

        private fun createChoHokTable(url: String) {
                val createTableCommand: String = "CREATE TABLE chohok_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, phone TEXT NOT NULL, tone TEXT NOT NULL, faancit TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("chohok.txt") ?: error("Can not load chohok.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO chohok_table (code, word, romanization, phone, tone, faancit) VALUES (?, ?, ?, ?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 5) error(badLineFormat)
                        val word = parts[0]
                        if (word.isBlank()) error(badLineFormat)
                        val code = word.codePointAt(0)
                        val romanization = parts[1]
                        val phone = parts[2]
                        val tone = parts[3]
                        val faancit = parts[4]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, romanization)
                        statement.setString(4, phone)
                        statement.setString(5, tone)
                        statement.setString(6, faancit)
                }
                connection.close()
                println("Inserted chohok entries: $insertedCount")
        }

        private fun createFanWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE fanwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, yamyeung TEXT NOT NULL, tone TEXT NOT NULL, rhyme TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("fanwan.txt") ?: error("Can not load fanwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO fanwan_table (code, word, romanization, initial, final, yamyeung, tone, rhyme, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "FanWan, bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 8) error(badLineFormat)
                        val word = parts[0]
                        if (word.isBlank()) error(badLineFormat)
                        val code = word.codePointAt(0)
                        val romanization = parts[1]
                        val initial = parts[2]
                        val final = parts[3]
                        val yamyeung = parts[4]
                        val tone = parts[5]
                        val rhyme = parts[6]
                        val interpretation = parts[7]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, romanization)
                        statement.setString(4, initial)
                        statement.setString(5, final)
                        statement.setString(6, yamyeung)
                        statement.setString(7, tone)
                        statement.setString(8, rhyme)
                        statement.setString(9, interpretation)
                }
                connection.close()
                println("Inserted fanwan entries: $insertedCount")
        }

        private fun createGwongWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE gwongwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, rhyme TEXT NOT NULL, subrhyme TEXT NOT NULL, subrhymeserial INTEGER NOT NULL, subrhymenumber INTEGER NOT NULL, upper TEXT NOT NULL, lower TEXT NOT NULL, initial TEXT NOT NULL, rounding TEXT NOT NULL, division TEXT NOT NULL, rhymeclass TEXT NOT NULL, repeating TEXT NOT NULL, tone TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("gwongwan.txt") ?: error("Can not load gwongwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO gwongwan_table (code, word, rhyme, subrhyme, subrhymeserial, subrhymenumber, upper, lower, initial, rounding, division, rhymeclass, repeating, tone, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?);"
                val insertedCount = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(",")
                        if (parts.size != 14) error(badLineFormat)
                        val word = parts[0]
                        if (word.isBlank()) error(badLineFormat)
                        val code = word.codePointAt(0)
                        val rhyme = parts[1]
                        val subrhyme = parts[2]
                        val subrhymeserial = parts[3].toInt()
                        val subrhymenumber = parts[4].toInt()
                        val upper = parts[5]
                        val lower = parts[6]
                        val initial = parts[7]
                        val rounding = parts[8]
                        val division = parts[9]
                        val rhymeclass = parts[10]
                        val repeating = parts[11]
                        val tone = parts[12]
                        val interpretation = parts[13]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, rhyme)
                        statement.setString(4, subrhyme)
                        statement.setInt(5, subrhymeserial)
                        statement.setInt(6, subrhymenumber)
                        statement.setString(7, upper)
                        statement.setString(8, lower)
                        statement.setString(9, initial)
                        statement.setString(10, rounding)
                        statement.setString(11, division)
                        statement.setString(12, rhymeclass)
                        statement.setString(13, repeating)
                        statement.setString(14, tone)
                        statement.setString(15, interpretation)
                }
                connection.close()
                println("Inserted gwongwan entries: $insertedCount")
        }
}
