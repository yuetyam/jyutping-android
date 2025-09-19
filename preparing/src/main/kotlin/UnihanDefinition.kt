package org.jyutping.preparing

import java.io.InputStream
import java.sql.Connection
import java.sql.DriverManager
import kotlin.streams.toList
import kotlin.use

object UnihanDefinition {
        fun generate(): List<Pair<Int, String>> {
                val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
                val  createTableCommand: String = "CREATE TABLE definitiontable(word TEXT NOT NULL, definition TEXT NOT NULL);"
                connection.createStatement().execute(createTableCommand)

                val insertCommand: String = "INSERT INTO definitiontable (word, definition) VALUES (?, ?);"
                val strokeData = readDefinitionData()
                connection.prepareStatement(insertCommand).use { statement ->
                        for (obj in strokeData) {
                                statement.setString(1, obj.first)
                                statement.setString(2, obj.second)
                                statement.executeUpdate()
                        }
                        statement.close()
                }
                val createIndexCommand: String = "CREATE INDEX definitionwordindex ON definitiontable(word);"
                connection.createStatement().execute(createIndexCommand)

                val characters = fetchCharacters()
                val pairs: MutableList<Pair<Int, String>> = mutableListOf()
                connection.createStatement().use { statement ->
                        for (character in characters) {
                                val queryCommand: String = "SELECT definition FROM definitiontable WHERE word = '${character}' LIMIT 1;"
                                val rs = statement.executeQuery(queryCommand)
                                while (rs.next()) {
                                        val definition = rs.getString(1)
                                        val code = character.codePointAt(0)
                                        val pair = Pair(code, definition)
                                        pairs.add(pair)
                                }
                                rs.close()
                        }
                        statement.close()
                }
                connection.close()
                return pairs
        }
        private fun readDefinitionData(): List<Pair<String, String>> {
                val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("definition.txt") ?: error("Can not load definition.txt")
                val sourceLines = inputStream.bufferedReader().use { it.readLines().filter { line -> line.isNotBlank() && line.startsWith("#").not() } }
                val entries = sourceLines.map { text ->
                        val parts = text.trim().split("\t").map { it.trim() }
                        if (parts.count() != 3) error("bad line format: $text")
                        val word = parts[0]
                        val definition = parts[2]
                        Pair(word, definition)
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
