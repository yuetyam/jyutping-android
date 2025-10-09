package org.jyutping.preparing

import java.io.InputStream
import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.max
import kotlin.use

data class Cangjie(
        val word: String,
        val cangjie5: String,
        val cangjie3: String,
        val c5complex: Int,
        val c3complex: Int,
        val c5code: Long,
        val c3code: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Cangjie) return false
                return word == other.word
        }
        override fun hashCode(): Int {
                return word.hashCode()
        }
        companion object {
                fun generate(): List<Cangjie> {
                        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
                        run {
                                val  createTableCommand: String = "CREATE TABLE cangjie5table(word TEXT NOT NULL, cangjie TEXT NOT NULL);"
                                connection.createStatement().execute(createTableCommand)
                                val insertCommand: String = "INSERT INTO cangjie5table (word, cangjie) VALUES (?, ?);"
                                val cangjie5Data = readCangjie5Data()
                                println("Inserting ${cangjie5Data.size} cangjie5 rows (temp DB)...")
                                        val inserted = batchInsert(connection, insertCommand, cangjie5Data) { statement, obj ->
                                                statement.setString(1, obj.first)
                                                statement.setString(2, obj.second)
                                        }
                                        println("Inserted cangjie5 rows (temp DB): $inserted")
                                val createIndexCommand: String = "CREATE INDEX cangjie5wordindex ON cangjie5table(word);"
                                connection.createStatement().execute(createIndexCommand)
                        }
                        run {
                                val  createTableCommand: String = "CREATE TABLE cangjie3table(word TEXT NOT NULL, cangjie TEXT NOT NULL);"
                                connection.createStatement().execute(createTableCommand)
                                val insertCommand: String = "INSERT INTO cangjie3table (word, cangjie) VALUES (?, ?);"
                                val cangjie3Data = readCangjie3Data()
                                println("Inserting ${cangjie3Data.size} cangjie3 rows (temp DB)...")
                                val inserted3 = batchInsert(connection, insertCommand, cangjie3Data) { statement, obj ->
                                        statement.setString(1, obj.first)
                                        statement.setString(2, obj.second)
                                }
                                println("Inserted cangjie3 rows (temp DB): $inserted3")
                                val createIndexCommand: String = "CREATE INDEX cangjie3wordindex ON cangjie3table(word);"
                                connection.createStatement().execute(createIndexCommand)
                        }
                        val characters = fetchCharacters()
                        val objects: MutableList<Cangjie> = mutableListOf()
                        connection.createStatement().use { statement ->
                                for (character in characters) {
                                        val cangjie5Matches: MutableList<String> = mutableListOf()
                                        val cangjie3Matches: MutableList<String> = mutableListOf()
                                        val query5: String = "SELECT cangjie FROM cangjie5table WHERE word = '${character}';"
                                        val rs5 = statement.executeQuery(query5)
                                        while (rs5.next()) {
                                                cangjie5Matches.add(rs5.getString(1))
                                        }
                                        rs5.close()
                                        val query3: String = "SELECT cangjie FROM cangjie3table WHERE word = '${character}';"
                                        val rs3 = statement.executeQuery(query3)
                                        while (rs3.next()) {
                                                cangjie3Matches.add(rs3.getString(1))
                                        }
                                        rs3.close()
                                        if (cangjie5Matches.isNotEmpty() || cangjie3Matches.isNotEmpty()) {
                                                val upperBound: Int = max(cangjie5Matches.count(), cangjie3Matches.count())
                                                for (index in 0 until upperBound) {
                                                        val cangjie5: String = cangjie5Matches.getOrNull(index) ?: "X"
                                                        val cangjie3: String = cangjie3Matches.getOrNull(index) ?: "X"
                                                        val c5complex = cangjie5.count()
                                                        val c3complex = cangjie3.count()
                                                        val c5code: Long = cangjie5.charCode ?: 0
                                                        val c3code: Long = cangjie3.charCode ?: 0
                                                        val obj = Cangjie(word = character, cangjie5 = cangjie5, cangjie3 = cangjie3, c5complex = c5complex, c3complex = c3complex, c5code = c5code, c3code = c3code)
                                                        objects.add(obj)
                                                }
                                        }
                                }
                                statement.close()
                        }
                        connection.close()
                        return objects.distinct()
                }
                private fun readCangjie5Data(): List<Pair<String, String>> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("cangjie5.txt") ?: error("Can not load cangjie5.txt")
                        val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                        val entries = sourceLines.map { text ->
                                val parts = text.trim().split("\t").map { it.trim() }
                                if (parts.count() != 2) error("bad line format: $text")
                                val word = parts[0]
                                val stroke = parts[1]
                                Pair(word, stroke)
                        }
                        return entries.distinct()
                }
                private fun readCangjie3Data(): List<Pair<String, String>> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("cangjie3.txt") ?: error("Can not load cangjie3.txt")
                        val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                        val entries = sourceLines.map { text ->
                                val parts = text.trim().split("\t").map { it.trim() }
                                if (parts.count() != 2) error("bad line format: $text")
                                val word = parts[0]
                                val stroke = parts[1]
                                Pair(word, stroke)
                        }
                        return entries.distinct()
                }
                private fun fetchCharacters(): List<String> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt") ?: error("Can not load jyutping.txt")
                        val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                        val characters = sourceLines.mapNotNull { text ->
                                val parts = text.trim().split("\t").map { it.trim() }
                                if (parts.count() != 2) error("bad line format: $text")
                                val word = parts[0]
                                if (word.codePoints().count() == 1L) word else null
                        }
                        return characters.distinct()
                }
        }
}
