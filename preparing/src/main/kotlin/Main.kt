package org.jyutping.preparing

import java.io.File
import java.sql.DriverManager

fun main() {
        val dbPath = "../app/src/main/assets/appdb.sqlite3"
        val dbFile = File(dbPath)
        if (dbFile.exists() && !dbFile.delete()) {
                error("Failed to delete the old database file at ${dbFile.absolutePath}")
        }
        DriverManager.getConnection("jdbc:sqlite:$dbPath").use { connection ->
                KeyboardDataPreparer.prepare(connection)
                AppDataPreparer.prepare(connection)
        }
}
