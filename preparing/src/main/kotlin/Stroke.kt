package org.jyutping.preparing

import java.io.InputStream
import java.sql.Connection
import java.sql.DriverManager

data class Stroke(
        val word : String,
        val stroke: String,
        val complex: Int,
        val ping: Int,
        val code: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Stroke) return false
                return this.word == other.word && this.stroke == other.stroke
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + stroke.hashCode()
        }

        companion object {
                fun generate(): List<Stroke> {
                        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")
                        val  createTableCommand: String = "CREATE TABLE stroketable(word TEXT NOT NULL, stroke TEXT NOT NULL);"
                        connection.createStatement().execute(createTableCommand)

                        val insertCommand: String = "INSERT INTO stroketable (word, stroke) VALUES (?, ?);"
                        val strokeData = readStrokeData()
                        println("Inserting ${strokeData.size} stroke rows (temp DB)...")
                        val inserted = batchInsert(connection, insertCommand, strokeData) { statement, obj ->
                                statement.setString(1, obj.first)
                                statement.setString(2, obj.second)
                        }
                        println("Inserted stroke rows (temp DB): $inserted")
                        val createIndexCommand: String = "CREATE INDEX strokewordindex ON stroketable(word);"
                        connection.createStatement().execute(createIndexCommand)

                        val characters = fetchCharacters()
                        val strokeObjects: MutableList<Stroke> = mutableListOf()
                        connection.createStatement().use { statement ->
                                for (character in characters) {
                                        val queryCommand: String = "SELECT stroke FROM stroketable WHERE word = '${character}';"
                                        val rs = statement.executeQuery(queryCommand)
                                        while (rs.next()) {
                                                val strokeText = rs.getString(1)
                                                val obj = convert(word = character, strokeText = strokeText)
                                                strokeObjects.add(obj)
                                        }
                                        rs.close()
                                }
                                statement.close()
                        }
                        connection.close()
                        return strokeObjects.distinct()
                }
                private fun readStrokeData(): List<Pair<String, String>> {
                        val inputStream: InputStream = object {}.javaClass.classLoader.getResourceAsStream("stroke.txt") ?: error("Can not load stroke.txt")
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
                private fun convert(word: String, strokeText: String): Stroke {
                        val complex = strokeText.length
                        val codes = strokeText.mapNotNull { codeMap[it] }
                        if (codes.count() != complex) error("bad stroke format: $word = $strokeText")
                        val strokeCodeText = codes.joinToString(separator = PresetString.EMPTY) { it.toString() }
                        val code: Long = codes.decimalCombined()
                        return Stroke(word = word, stroke = strokeCodeText, complex = complex, ping = strokeText.hashCode(), code = code)
                }
                private val codeMap: HashMap<Char, Int> = hashMapOf(
                        'w' to 1,
                        's' to 2,
                        'a' to 3,
                        'd' to 4,
                        'z' to 5,

                        'h' to 1,
                        // 's' to 2,
                        'p' to 3,
                        'n' to 4,
                        // 'z' to 5,

                        '1' to 1,
                        '2' to 2,
                        '3' to 3,
                        '4' to 4,
                        '5' to 5,
                )
        }
}
