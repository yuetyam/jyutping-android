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
                "tmpdb.sqlite3-journal",
                "appdb-v0.1.0-tmp.sqlite3",
                "appdb-v0.1.0-tmp.sqlite3-journal",
                "appdb-v0.2.0-tmp.sqlite3",
                "appdb-v0.2.0-tmp.sqlite3-journal",
                "appdb-v0.3.0-tmp.sqlite3",
                "appdb-v0.3.0-tmp.sqlite3-journal",
                "appdb-v0.4.0-tmp.sqlite3",
                "appdb-v0.4.0-tmp.sqlite3-journal",
                "appdb-v0.5.0-tmp.sqlite3",
                "appdb-v0.5.0-tmp.sqlite3-journal",
                "appdb-v0.6.0-tmp.sqlite3",
                "appdb-v0.6.0-tmp.sqlite3-journal",
        )
        val databaseName: String = run {
                val version = BuildConfig.VERSION_NAME
                "appdb-v${version}-tmp.sqlite3"
        }
        private const val DATABASES_DIR = "/databases/"

        fun prepare(context: Context) {
                deleteOldDatabases(context)
                val shouldCopyDatabase: Boolean = !(doesDatabaseExists(context))
                if (shouldCopyDatabase) {
                        copyDatabase(context)
                }
        }

        @Throws(SecurityException::class)
        private fun deleteOldDatabases(context: Context) {
                oldDatabaseNames.map {
                        try {
                                val dbFile = context.getDatabasePath(it)
                                if (dbFile.exists() && dbFile.isFile) {
                                        dbFile.delete()
                                }
                                val path = context.applicationInfo.dataDir + DATABASES_DIR + it
                                val file = File(path)
                                if (file.exists() && file.isFile) {
                                        file.delete()
                                } else { }
                        } catch (e: SecurityException) {
                                throw Error(e.message)
                        }
                }
        }

        private fun doesDatabaseExists(context: Context): Boolean {
                val dbFile = context.getDatabasePath(databaseName)
                val path = context.applicationInfo.dataDir + DATABASES_DIR + databaseName
                val file = File(path)
                return dbFile.exists() && file.exists()
        }

        @Throws(IOException::class)
        private fun copyDatabase(context: Context) {
                var inStream: InputStream? = null
                var outStream: OutputStream? = null
                val destinationPath = context.applicationInfo.dataDir + DATABASES_DIR + databaseName
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
