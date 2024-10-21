package org.jyutping.jyutping.utilities

import android.content.Context
import org.jyutping.jyutping.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object DatabasePreparer {
        private const val SOURCE_DATABASE_NAME = "appdb.sqlite3"
        private val oldDatabaseNames: List<String> = listOf(
                "appdb-v0.10.0-tmp.sqlite3",
                "appdb-v0.10.0-tmp.sqlite3-journal",
                "appdb-v0.11.0-tmp.sqlite3",
                "appdb-v0.11.0-tmp.sqlite3-journal",
                "appdb-v0.12.0-tmp.sqlite3",
                "appdb-v0.12.0-tmp.sqlite3-journal",
                "appdb-v0.13.0-tmp.sqlite3",
                "appdb-v0.13.0-tmp.sqlite3-journal",
                "appdb-v0.14.0-tmp.sqlite3",
                "appdb-v0.14.0-tmp.sqlite3-journal",
                "appdb-v0.15.0-tmp.sqlite3",
                "appdb-v0.15.0-tmp.sqlite3-journal",
                "appdb-v0.16.0-tmp.sqlite3",
                "appdb-v0.16.0-tmp.sqlite3-journal",
                "appdb-v0.17.0-tmp.sqlite3",
                "appdb-v0.17.0-tmp.sqlite3-journal",
                "appdb-v0.18.0-tmp.sqlite3",
                "appdb-v0.18.0-tmp.sqlite3-journal",
                "appdb-v0.19.0-tmp.sqlite3",
                "appdb-v0.19.0-tmp.sqlite3-journal",
        )
        val databaseName: String = run {
                val version = BuildConfig.VERSION_NAME
                "appdb-v${version}-tmp.sqlite3"
        }
        private const val DATABASES_DIR = "/databases/"

        fun prepare(context: Context) {
                val databaseExists: Boolean = doesDatabaseExist(context)
                if (!databaseExists) {
                        deleteOldDatabases(context)
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

        private fun doesDatabaseExist(context: Context): Boolean {
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
