package org.jyutping.preparing

import java.io.InputStream
import java.sql.DriverManager
import kotlin.use

object AppDataPreparer {
        fun prepare(url: String) {

                // Use core_lexicon table instead
                // createJyutpingTable(url)

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
                        // "CREATE INDEX ix_jyutping_word ON jyutping_table(word);",
                        // "CREATE INDEX ix_jyutping_romanization ON jyutping_table(romanization);",

                        "CREATE INDEX ix_collocation_word ON collocation_table(word);",
                        "CREATE INDEX ix_collocation_romanization ON collocation_table(romanization);",

                        "CREATE INDEX ix_dictionary_word ON dictionary_table(word);",
                        "CREATE INDEX ix_dictionary_romanization ON dictionary_table(romanization);",

                        "CREATE INDEX ix_yingwaa_code ON yingwaa_table(code);",
                        "CREATE INDEX ix_yingwaa_romanization ON yingwaa_table(romanization);",

                        "CREATE INDEX ix_chohok_code ON chohok_table(code);",
                        "CREATE INDEX ix_chohok_romanization ON chohok_table(romanization);",

                        "CREATE INDEX ix_fanwan_code ON fanwan_table(code);",
                        "CREATE INDEX ix_fanwan_romanization ON fanwan_table(romanization);",

                        "CREATE INDEX ix_gwongwan_code ON gwongwan_table(code);",
                )
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        for (command in commands) {
                                statement.executeUpdate(command)
                        }
                }
                connection.close()
                println("Successfully created app data indexes.")
        }

        private fun createJyutpingTable(url: String) {
                val createTableCommand: String = "CREATE TABLE jyutping_table(word TEXT NOT NULL, romanization TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created jyutping table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt") ?: error("Can not load jyutping.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO jyutping_table (word, romanization) VALUES (?, ?);"
                println("Inserting ${sourceLines.size} jyutping entries...")
                val inserted = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 2) error(badLineFormat)
                        val word = parts[0]
                        val romanization = parts[1]
                        statement.setString(1, word)
                        statement.setString(2, romanization)
                }
                connection.close()
                println("Inserted jyutping entries successfully: $inserted")
        }

        private fun createCollocationTable(url: String) {
                val createTableCommand: String = "CREATE TABLE collocation_table(word TEXT NOT NULL, romanization TEXT NOT NULL, collocation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created collocation table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("collocation.txt") ?: error("Can not load collocation.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO collocation_table (word, romanization, collocation) VALUES (?, ?, ?);"
                println("Inserting ${sourceLines.size} collocation entries...")
                val insertedColl = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
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
                println("Inserted collocation entries successfully: $insertedColl")
        }

        private fun createDictionaryTable(url: String) {
                val createTableCommand: String = "CREATE TABLE dictionary_table(word TEXT NOT NULL, romanization TEXT NOT NULL, description TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created dictionary table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("wordshk.txt") ?: error("Can not load wordshk.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO dictionary_table (word, romanization, description) VALUES (?, ?, ?);"
                println("Inserting ${sourceLines.size} dictionary entries...")
                val insertedColl = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
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
                println("Inserted dictionary entries successfully: $insertedColl")
        }

        private fun createDefinitionTable(url: String) {
                val createTableCommand: String = "CREATE TABLE definition_table(code INTEGER NOT NULL PRIMARY KEY, definition TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created definition table successfully.")
                val entries = UnihanDefinition.generate()
                val insertEntryCommand: String = "INSERT INTO definition_table (code, definition) VALUES (?, ?);"
                println("Inserting ${entries.size} definition entries...")
                val insertedDef = batchInsert(connection, insertEntryCommand, entries) { statement, entry ->
                        statement.setInt(1, entry.first)
                        statement.setString(2, entry.second)
                }
                connection.close()
                println("Inserted definition entries successfully: $insertedDef")
        }

        private fun createYingWaaTable(url: String) {
                val createTableCommand: String = "CREATE TABLE yingwaa_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, pronunciation TEXT NOT NULL, pronunciationmark TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created yingwaa table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("yingwaa.txt") ?: error("Can not load yingwaa.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO yingwaa_table (code, word, romanization, pronunciation, pronunciationmark, interpretation) VALUES (?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} yingwaa entries...")
                val insertedYw = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 6) error(badLineFormat)
                        val code = parts[0].toInt()
                        val word = parts[1]
                        val romanization = parts[2]
                        val pronunciation = parts[3]
                        val pronunciationMark = parts[4]
                        val interpretation = parts[5]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, romanization)
                        statement.setString(4, pronunciation)
                        statement.setString(5, pronunciationMark)
                        statement.setString(6, interpretation)
                }
                connection.close()
                println("Inserted yingwaa entries successfully: $insertedYw")
        }

        private fun createChoHokTable(url: String) {
                val createTableCommand: String = "CREATE TABLE chohok_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, tone TEXT NOT NULL, faancit TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created chohok table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("chohok.txt") ?: error("Can not load chohok.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO chohok_table (code, word, romanization, initial, final, tone, faancit) VALUES (?, ?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} chohok entries...")
                val insertedCh = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 7) error(badLineFormat)
                        val code = parts[0].toInt()
                        val word = parts[1]
                        val romanization = parts[2]
                        val initial = parts[3]
                        val final = parts[4]
                        val tone = parts[5]
                        val faancit = parts[6]
                        statement.setInt(1, code)
                        statement.setString(2, word)
                        statement.setString(3, romanization)
                        statement.setString(4, initial)
                        statement.setString(5, final)
                        statement.setString(6, tone)
                        statement.setString(7, faancit)
                }
                connection.close()
                println("Inserted chohok entries successfully: $insertedCh")
        }

        private fun createFanWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE fanwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, yamyeung TEXT NOT NULL, tone TEXT NOT NULL, rhyme TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created fanwan table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("fanwan.txt") ?: error("Can not load fanwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO fanwan_table (code, word, romanization, initial, final, yamyeung, tone, rhyme, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} fanwan entries...")
                val insertedFan = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(PresetString.TAB)
                        if (parts.size != 9) error(badLineFormat)
                        val code = parts[0].toInt()
                        val word = parts[1]
                        val romanization = parts[2]
                        val initial = parts[3]
                        val final = parts[4]
                        val yamyeung = parts[5]
                        val tone = parts[6]
                        val rhyme = parts[7]
                        val interpretation = parts[8]
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
                println("Inserted fanwan entries successfully: $insertedFan")
        }

        private fun createGwongWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE gwongwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, rhyme TEXT NOT NULL, subrhyme TEXT NOT NULL, subrhymeserial INTEGER NOT NULL, subrhymenumber INTEGER NOT NULL, upper TEXT NOT NULL, lower TEXT NOT NULL, initial TEXT NOT NULL, rounding TEXT NOT NULL, division TEXT NOT NULL, rhymeclass TEXT NOT NULL, repeating TEXT NOT NULL, tone TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created gwongwan table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("gwongwan.txt") ?: error("Can not load gwongwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO gwongwan_table (code, word, rhyme, subrhyme, subrhymeserial, subrhymenumber, upper, lower, initial, rounding, division, rhymeclass, repeating, tone, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?);"
                println("Inserting ${sourceLines.size} gwongwan entries...")
                val insertedGw = batchInsert(connection, insertEntryCommand, sourceLines) { statement, line ->
                        val badLineFormat = "bad line format: $line"
                        val parts = line.split(",")
                        if (parts.size != 15) error(badLineFormat)
                        val code = parts[0].toInt()
                        val word = parts[1]
                        val rhyme = parts[2]
                        val subrhyme = parts[3]
                        val subrhymeserial = parts[4].toInt()
                        val subrhymenumber = parts[5].toInt()
                        val upper = parts[6]
                        val lower = parts[7]
                        val initial = parts[8]
                        val rounding = parts[9]
                        val division = parts[10]
                        val rhymeclass = parts[11]
                        val repeating = parts[12]
                        val tone = parts[13]
                        val interpretation = parts[14]
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
                println("Inserted gwongwan entries successfully: $insertedGw")
        }
}
