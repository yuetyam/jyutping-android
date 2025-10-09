package org.jyutping.preparing

import java.io.InputStream
import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.max
import kotlin.use

data class Quick(
        val word: String,
        val quick5: String,
        val quick3: String,
        val q5complex: Int,
        val q3complex: Int,
        val q5code: Long,
        val q3code: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Quick) return false
                return word == other.word
        }
        override fun hashCode(): Int {
                return word.hashCode()
        }
        companion object {
                fun generate(): List<Quick> {
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
                        val words = fetchWords()
                        val objects: MutableList<Quick> = mutableListOf()
                        connection.createStatement().use { statement ->
                                for (word in words) {
                                        when (word.characterCount()) {
                                                1 -> {
                                                        val cangjie5Matches: MutableList<String> = mutableListOf()
                                                        val cangjie3Matches: MutableList<String> = mutableListOf()
                                                        val query5: String = "SELECT cangjie FROM cangjie5table WHERE word = '${word}';"
                                                        val rs5 = statement.executeQuery(query5)
                                                        while (rs5.next()) {
                                                                cangjie5Matches.add(rs5.getString(1))
                                                        }
                                                        rs5.close()
                                                        val query3: String = "SELECT cangjie FROM cangjie3table WHERE word = '${word}';"
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
                                                                        val quick5: String = if (cangjie5.count() > 2) "${cangjie5.first()}${cangjie5.last()}" else cangjie5
                                                                        val quick3: String = if (cangjie3.count() > 2) "${cangjie3.first()}${cangjie3.last()}" else cangjie3
                                                                        val q5complex = quick5.count()
                                                                        val q3complex = quick3.count()
                                                                        val q5code: Long = quick5.charCode ?: 0
                                                                        val q3code: Long = quick3.charCode ?: 0
                                                                        val obj = Quick(word = word, quick5 = quick5, quick3 = quick3, q5complex = q5complex, q3complex = q3complex, q5code = q5code, q3code = q3code)
                                                                        objects.add(obj)
                                                                }
                                                        }
                                                }
                                                else -> {
                                                        val characters = word.codePoints().toArray().map { Character.toString(it) }
                                                        val cangjie5Sequence: MutableList<String> = mutableListOf()
                                                        val cangjie3Sequence: MutableList<String> = mutableListOf()
                                                        for (char in characters) {
                                                                var cangjie5: String? = null
                                                                var cangjie3: String? = null
                                                                val query5: String = "SELECT cangjie FROM cangjie5table WHERE word = '${char}' LIMIT 1;"
                                                                val rs5 = statement.executeQuery(query5)
                                                                if (rs5.next()) {
                                                                        cangjie5 = rs5.getString(1)
                                                                }
                                                                rs5.close()
                                                                val query3: String = "SELECT cangjie FROM cangjie3table WHERE word = '${char}' LIMIT 1;"
                                                                val rs3 = statement.executeQuery(query3)
                                                                if (rs3.next()) {
                                                                        cangjie3 = rs3.getString(1)
                                                                }
                                                                rs3.close()
                                                                cangjie5Sequence.add(cangjie5 ?: "X")
                                                                cangjie3Sequence.add(cangjie3 ?: "X")
                                                        }
                                                        val quick5 = cangjie5Sequence.joinToString(separator = PresetString.EMPTY) { if (it.count() > 2) "${it.first()}${it.last()}" else it }
                                                        val quick3 = cangjie3Sequence.joinToString(separator = PresetString.EMPTY) { if (it.count() > 2) "${it.first()}${it.last()}" else it }
                                                        val q5complex = quick5.count()
                                                        val q3complex = quick3.count()
                                                        val q5code: Long = quick5.charCode ?: 0
                                                        val q3code: Long = quick3.charCode ?: 0
                                                        val obj = Quick(word = word, quick5 = quick5, quick3 = quick3, q5complex = q5complex, q3complex = q3complex, q5code = q5code, q3code = q3code)
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
                private fun fetchWords(): List<String> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("jyutping.txt") ?: error("Can not load jyutping.txt")
                        val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                        val words = sourceLines.map { text ->
                                val parts = text.trim().split("\t").map { it.trim() }
                                if (parts.count() != 2) error("bad line format: $text")
                                parts[0]
                        }
                        return words.distinct()
                }
        }
}
