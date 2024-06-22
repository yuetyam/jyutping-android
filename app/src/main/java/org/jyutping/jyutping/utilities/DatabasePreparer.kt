package org.jyutping.jyutping.utilities

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import org.jyutping.jyutping.BuildConfig

object DatabasePreparer {

        private const val SOURCE_DATABASE_NAME = "appdb.sqlite3"
        private val oldDatabaseNames: List<String> = listOf(
                "tmpdb.sqlite3",
                "tmpdb.sqlite3-journal"
        )
        val databaseName: String = run {
                val version = BuildConfig.VERSION_NAME
                "appdb-v${version}-tmp.sqlite3"
        }
        private const val DATABASES_DIR = "/databases/"

        fun prepare(context: Context) {
                deleteOldDatabases(context)
                if (!doesDatabaseExists(context)) {
                        copyDatabase(context)
                }
        }
        private fun deleteOldDatabases(context: Context) {
                oldDatabaseNames.map {
                        val path = context.applicationInfo.dataDir + DATABASES_DIR + it
                        val file = File(path)
                        if (file.exists()) {
                                try {
                                        file.delete()
                                } catch (e: Exception) {
                                        Log.e("DeleteDatabase", "Error deleting file: $path", e)
                                }
                        }
                }
        }
        private fun doesDatabaseExists(context: Context): Boolean {
                val dbPath = context.applicationInfo.dataDir + DATABASES_DIR + databaseName
                val dbFile = File(dbPath)
                return dbFile.exists()
                // val dbFile = context.getDatabasePath(DATABASE_NAME)
                // return dbFile.exists()
        }

        @Throws(IOException::class)
        private fun copyDatabase(context: Context) {
                var inStream: InputStream? = null
                var outStream: OutputStream? = null
                val destinationPath = context.applicationInfo.dataDir + DATABASES_DIR + databaseName
                // val destinationPath = context.getDatabasePath(SOURCE_DATABASE_NAME).path.dropLast(SOURCE_DATABASE_NAME.length) + DATABASE_NAME
                val destinationFile = File(destinationPath)
                try {
                        inStream = context.assets.open(SOURCE_DATABASE_NAME)
                        outStream = FileOutputStream(destinationFile)
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inStream.read(buffer).also { length = it } > 0) {
                                outStream.write(buffer, 0, length)
                        }
                        outStream.flush()
                } catch (e: IOException) {
                        throw Error(e.message)
                } finally {
                        inStream?.close()
                        outStream?.close()
                }
        }
}
