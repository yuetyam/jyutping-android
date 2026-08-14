package org.jyutping.preparing

import java.sql.Connection

object AppDataPreparer {
        fun prepare(connection: Connection) {
                prepareCollocationTable(connection)
                prepareDictionaryTable(connection)
                prepareDefinitionTable(connection)
                prepareYingWaaTable(connection)
                prepareChoHokTable(connection)
                prepareFanWanTable(connection)
                prepareGwongWanTable(connection)
                createIndexes(connection)
        }

        private fun prepareCollocationTable(connection: Connection) {
                connection.execute("CREATE TABLE collocation_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, collocation TEXT NOT NULL, UNIQUE (word, romanization));")
                val entries = tabSeparatedLines("collocation.txt", 3)
                batchInsert(connection, "INSERT INTO collocation_table (word, romanization, collocation) VALUES (?, ?, ?);", entries) { statement, parts ->
                        statement.setString(1, parts[0])
                        statement.setString(2, parts[1])
                        statement.setString(3, parts[2])
                }
        }

        private fun prepareDictionaryTable(connection: Connection) {
                connection.execute("CREATE TABLE dictionary_table (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT NOT NULL, romanization TEXT NOT NULL, description TEXT NOT NULL);")
                val entries = tabSeparatedLines("wordshk.txt", 3)
                batchInsert(connection, "INSERT INTO dictionary_table (word, romanization, description) VALUES (?, ?, ?);", entries) { statement, parts ->
                        statement.setString(1, parts[0])
                        statement.setString(2, parts[1])
                        statement.setString(3, parts[2])
                }
        }

        private fun prepareDefinitionTable(connection: Connection) {
                connection.execute("CREATE TABLE definition_table (code INTEGER PRIMARY KEY, definition TEXT NOT NULL);")
                val entries = UnihanDefinition.generate()
                batchInsert(connection, "INSERT INTO definition_table (code, definition) VALUES (?, ?);", entries) { statement, entry ->
                        statement.setInt(1, entry.first)
                        statement.setString(2, entry.second)
                }
        }

        private fun prepareYingWaaTable(connection: Connection) {
                connection.execute("CREATE TABLE yingwaa_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, pronunciation TEXT NOT NULL, note TEXT NOT NULL, interpretation TEXT NOT NULL);")
                val entries = tabSeparatedLines("yingwaa.txt", 5)
                batchInsert(connection, "INSERT INTO yingwaa_table (code, word, romanization, pronunciation, note, interpretation) VALUES (?, ?, ?, ?, ?, ?);", entries) { statement, parts ->
                        statement.setInt(1, parts[0].codePointAt(0))
                        statement.setString(2, parts[0])
                        statement.setString(3, parts[1])
                        statement.setString(4, parts[2])
                        statement.setString(5, parts[3])
                        statement.setString(6, parts[4])
                }
        }

        private fun prepareChoHokTable(connection: Connection) {
                connection.execute("CREATE TABLE chohok_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, phone TEXT NOT NULL, tone TEXT NOT NULL, faancit TEXT NOT NULL);")
                val entries = tabSeparatedLines("chohok.txt", 5)
                batchInsert(connection, "INSERT INTO chohok_table (code, word, romanization, phone, tone, faancit) VALUES (?, ?, ?, ?, ?, ?);", entries) { statement, parts ->
                        statement.setInt(1, parts[0].codePointAt(0))
                        statement.setString(2, parts[0])
                        statement.setString(3, parts[1])
                        statement.setString(4, parts[2])
                        statement.setString(5, parts[3])
                        statement.setString(6, parts[4])
                }
        }

        private fun prepareFanWanTable(connection: Connection) {
                connection.execute("CREATE TABLE fanwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, romanization TEXT NOT NULL, initial TEXT NOT NULL, final TEXT NOT NULL, yamyeung TEXT NOT NULL, tone TEXT NOT NULL, rhyme TEXT NOT NULL, interpretation TEXT NOT NULL);")
                val entries = tabSeparatedLines("fanwan.txt", 8)
                batchInsert(connection, "INSERT INTO fanwan_table (code, word, romanization, initial, final, yamyeung, tone, rhyme, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);", entries) { statement, parts ->
                        statement.setInt(1, parts[0].codePointAt(0))
                        statement.setString(2, parts[0])
                        statement.setString(3, parts[1])
                        statement.setString(4, parts[2])
                        statement.setString(5, parts[3])
                        statement.setString(6, parts[4])
                        statement.setString(7, parts[5])
                        statement.setString(8, parts[6])
                        statement.setString(9, parts[7])
                }
        }

        private fun prepareGwongWanTable(connection: Connection) {
                connection.execute("CREATE TABLE gwongwan_table(code INTEGER NOT NULL, word TEXT NOT NULL, rhyme TEXT NOT NULL, subrhyme TEXT NOT NULL, subrhymeserial INTEGER NOT NULL, subrhymenumber INTEGER NOT NULL, upper TEXT NOT NULL, lower TEXT NOT NULL, initial TEXT NOT NULL, rounding TEXT NOT NULL, division TEXT NOT NULL, rhymeclass TEXT NOT NULL, repeating TEXT NOT NULL, tone TEXT NOT NULL, interpretation TEXT NOT NULL);")
                val entries = readResourceLines("gwongwan.txt").filter(String::isNotBlank).map { line ->
                        val parts = line.split(",")
                        require(parts.size == 14) { "gwongwan.txt: bad line format: $line" }
                        parts
                }
                batchInsert(connection, "INSERT INTO gwongwan_table (code, word, rhyme, subrhyme, subrhymeserial, subrhymenumber, upper, lower, initial, rounding, division, rhymeclass, repeating, tone, interpretation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", entries) { statement, parts ->
                        statement.setInt(1, parts[0].codePointAt(0))
                        statement.setString(2, parts[0])
                        statement.setString(3, parts[1])
                        statement.setString(4, parts[2])
                        statement.setInt(5, parts[3].toInt())
                        statement.setInt(6, parts[4].toInt())
                        statement.setString(7, parts[5])
                        statement.setString(8, parts[6])
                        statement.setString(9, parts[7])
                        statement.setString(10, parts[8])
                        statement.setString(11, parts[9])
                        statement.setString(12, parts[10])
                        statement.setString(13, parts[11])
                        statement.setString(14, parts[12])
                        statement.setString(15, parts[13])
                }
        }

        private fun tabSeparatedLines(fileName: String, fieldCount: Int): List<List<String>> {
                return readResourceLines(fileName).filter(String::isNotBlank).map { line ->
                        val parts = line.split(PresetString.TAB)
                        require(parts.size == fieldCount) { "$fileName: bad line format: $line" }
                        parts
                }
        }

        private fun createIndexes(connection: Connection) {
                val commands = listOf(
                        "CREATE INDEX ix_collocation_unified ON collocation_table (word, romanization);",
                        "CREATE INDEX ix_dictionary_unified ON dictionary_table (word, romanization);",
                        "CREATE INDEX ix_yingwaa_code ON yingwaa_table (code);",
                        "CREATE INDEX ix_yingwaa_romanization ON yingwaa_table (romanization);",
                        "CREATE INDEX ix_chohok_code ON chohok_table (code);",
                        "CREATE INDEX ix_chohok_romanization ON chohok_table (romanization);",
                        "CREATE INDEX ix_fanwan_code ON fanwan_table (code);",
                        "CREATE INDEX ix_fanwan_romanization ON fanwan_table (romanization);",
                        "CREATE INDEX ix_gwongwan_code ON gwongwan_table (code);",
                )
                commands.forEach(connection::execute)
        }
}
