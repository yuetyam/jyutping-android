package org.jyutping.preparing

import java.io.File

fun main() {
        val dbPath = "../app/src/main/assets/appdb.sqlite3"
        val dbFile = File(dbPath)
        if (dbFile.exists()) {
                val isDeleted = dbFile.delete()
                if (isDeleted) {
                        println("Deleted the old database file successfully")
                } else {
                        println("Failed to delete the old database file at ${dbFile.absolutePath}")
                }
        }
        val url = "jdbc:sqlite:$dbPath"
        AppDataPreparer.prepare(url)
        KeyboardDataPreparer.prepare(url)
}
