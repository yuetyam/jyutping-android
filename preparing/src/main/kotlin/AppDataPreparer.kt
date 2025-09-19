package org.jyutping.preparing

import java.io.InputStream
import java.sql.DriverManager
import kotlin.use

object AppDataPreparer {
        fun prepare(url: String) {
                createJyutpingTable(url)
                createCollocationTable(url)
                createDefinitionTable(url)
                createYingWaaTable(url)
                createChoHokTable(url)
                createFanWanTable(url)
                createGwongWanTable(url)
                createIndies(url)
        }
        private fun createIndies(url: String) {
                val commands: List<String> = listOf(
                        "CREATE INDEX jyutpingwordindex ON jyutpingtable(word);",
                        "CREATE INDEX jyutpingromanizationindex ON jyutpingtable(romanization);",

                        "CREATE INDEX collocationwordindex ON collocationtable(word);",
                        "CREATE INDEX collocationromanizationindex ON collocationtable(romanization);",

                        "CREATE INDEX yingwaacodeindex ON yingwaatable(code);",
                        "CREATE INDEX yingwaaromanizationindex ON yingwaatable(romanization);",

                        "CREATE INDEX chohokcodeindex ON chohoktable(code);",
                        "CREATE INDEX chohokromanizationindex ON chohoktable(romanization);",

                        "CREATE INDEX fanwancodeindex ON fanwantable(code);",
                        "CREATE INDEX fanwanromanizationindex ON fanwantable(romanization);",

                        "CREATE INDEX gwongwancodeindex ON gwongwantable(code);",
                )
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        for (command in commands) {
                                statement.executeUpdate(command)
                        }
                }
                connection.close()
                println("Successfully created app data indies.")
        }

        private fun createJyutpingTable(url: String) {
                val createTableCommand: String = "CREATE TABLE jyutpingtable(word TEXT NOT NULL, romanization TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created jyutping table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt") ?: error("Can not load jyutping.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO jyutpingtable (word, romanization) VALUES (?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
                                val badLineFormat = "bad line format: $line"
                                val parts = line.split(PresetString.TAB)
                                if (parts.size != 2) error(badLineFormat)
                                val word = parts[0]
                                val romanization = parts[1]
                                statement.setString(1, word)
                                statement.setString(2, romanization)
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted jyutping entries successfully.")
        }

        private fun createCollocationTable(url: String) {
                val createTableCommand: String = "CREATE TABLE collocationtable(word TEXT NOT NULL, romanization TEXT NOT NULL, collocation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created collocation table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("collocation.txt") ?: error("Can not load collocation.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO collocationtable (word, romanization, collocation) VALUES (?, ?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
                                val badLineFormat = "bad line format: $line"
                                val parts = line.split(PresetString.TAB)
                                if (parts.size != 3) error(badLineFormat)
                                val word = parts[0]
                                val romanization = parts[1]
                                val collocation = parts[2]
                                statement.setString(1, word)
                                statement.setString(2, romanization)
                                statement.setString(3, collocation)
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted collocation entries successfully.")
        }

        private fun createDefinitionTable(url: String) {
                val createTableCommand: String = "CREATE TABLE definitiontable(code INTEGER NOT NULL PRIMARY KEY, definition TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created definition table successfully.")
                val entries = UnihanDefinition.generate()

                val insertEntryCommand: String = "INSERT INTO definitiontable (code, definition) VALUES (?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (entry in entries) {
                                statement.setInt(1, entry.first)
                                statement.setString(2, entry.second)
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted definition entries successfully.")
        }

        private fun createYingWaaTable(url: String) {
                val createTableCommand: String = "CREATE TABLE yingwaatable(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, pronunciation TEXT NOT NULL, pronunciationmark TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created yingwaa table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("yingwaa.txt") ?: error("Can not load yingwaa.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO yingwaatable (code, word, romanization, pronunciation, pronunciationmark, interpretation) VALUES (?, ?, ?, ?, ?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
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
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted yingwaa entries successfully.")
        }

        private fun createChoHokTable(url: String) {
                val createTableCommand: String = "CREATE TABLE chohoktable(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, tone TEXT NOT NULL, faancit TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created chohok table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("chohok.txt") ?: error("Can not load chohok.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO chohoktable (code, word, romanization, initial, final, tone, faancit) VALUES (?, ?, ?, ?, ?, ?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
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
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted chohok entries successfully.")
        }

        private fun createFanWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE fanwantable(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, yamyeung TEXT NOT NULL, tone TEXT NOT NULL, rhyme TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created fanwan table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("fanwan.txt") ?: error("Can not load fanwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO fanwantable (code, word, romanization, initial, final, yamyeung, tone, rhyme, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
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
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted fanwan entries successfully.")
        }

        private fun createGwongWanTable(url: String) {
                val createTableCommand: String = "CREATE TABLE gwongwantable(code INTEGER NOT NULL, word TEXT NOT NULL, rhyme TEXT NOT NULL, subrhyme TEXT NOT NULL, subrhymeserial INTEGER NOT NULL, subrhymenumber INTEGER NOT NULL, upper TEXT NOT NULL, lower TEXT NOT NULL, initial TEXT NOT NULL, rounding TEXT NOT NULL, division TEXT NOT NULL, rhymeclass TEXT NOT NULL, repeating TEXT NOT NULL, tone TEXT NOT NULL, interpretation TEXT NOT NULL);"
                val connection = DriverManager.getConnection(url)
                connection.createStatement().use { statement ->
                        statement.executeUpdate(createTableCommand)
                }
                println("Created gwongwan table successfully.")
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("gwongwan.txt") ?: error("Can not load gwongwan.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() } }
                val insertEntryCommand: String = "INSERT INTO gwongwantable (code, word, rhyme, subrhyme, subrhymeserial, subrhymenumber, upper, lower, initial, rounding, division, rhymeclass, repeating, tone, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?);"
                connection.prepareStatement(insertEntryCommand).use { statement ->
                        for (line in sourceLines) {
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
                                statement.executeUpdate()
                        }
                }
                connection.close()
                println("Inserted gwongwan entries successfully.")
        }
}
