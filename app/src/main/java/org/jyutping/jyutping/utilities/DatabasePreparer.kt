package org.jyutping.jyutping.utilities

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object DatabasePreparer {

        private const val SOURCE_DATABASE_NAME = "appdb.sqlite3"
        const val DATABASE_NAME = "tmpdb.sqlite3"

        fun prepare(context: Context) {
                if (!doesDatabaseExists(context)) {
                        copyDatabase(context)
                }
        }

        private fun doesDatabaseExists(context: Context): Boolean {
                val dbFile = context.getDatabasePath(DATABASE_NAME)
                return dbFile.exists()
        }

        @Throws(IOException::class)
        private fun copyDatabase(context: Context) {
                var inStream: InputStream? = null
                var outStream: OutputStream? = null
                val destinationPath = context.applicationInfo.dataDir + "/databases/" + DATABASE_NAME
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
