package org.jyutping.preparing

import java.io.File
import java.sql.DriverManager
import kotlin.use

fun main() {
        val dbPath = "../app/src/main/assets/appdb.sqlite3"
        val dbFile = File(dbPath)
        if (dbFile.exists()) {
                val isDeleted = dbFile.delete()
                if (isDeleted) {
                        println("Deleted the old database file.")
                } else {
                        println("Failed to delete the old database file at ${dbFile.absolutePath}")
                }
        }
        val url = "jdbc:sqlite:$dbPath"
        AppDataPreparer.prepare(url)
        KeyboardDataPreparer.prepare(url)
        fineTun(url)
}

private fun fineTun(url: String) {
        val commands: List<String> = listOf(
                "PRAGMA journal_mode = DELETE;",
                "VACUUM;",
                "ANALYZE;",
        )
        DriverManager.getConnection(url).use { connection ->
                connection.createStatement().use { statement ->
                        commands.forEach { command ->
                                statement.execute(command)
                        }
                }
        }
        println("Fine-tuned database.")
}
