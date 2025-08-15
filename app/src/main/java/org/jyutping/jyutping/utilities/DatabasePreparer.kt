package org.jyutping.jyutping.utilities

import android.content.Context
import android.util.Log
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.presets.PresetConstant
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object DatabasePreparer {

        /** App in-package file */
        private const val SOURCE_DATABASE_NAME: String = "appdb.sqlite3"

        /** App working database file */
        const val DATABASE_NAME: String = "appdb-v${BuildConfig.VERSION_NAME}-tmp.sqlite3"

        private const val DATABASES_DIR_NAME: String = "databases"
        private const val DATABASES_PATH_BLOCK: String = "/databases/"
        private const val FILENAME_PREFIX: String = "appdb-v"
        private const val LOG_TAG: String = PresetConstant.keyboardPackageName + ".utilities.DatabasePreparer"

        fun prepare(context: Context) {
                val databaseExists: Boolean = doesDatabaseExist(context)
                if (databaseExists.not()) {
                        deleteOldDatabases(context)
                        copyDatabase(context)
                }
        }

        private fun deleteOldDatabases(context: Context) {
                val dbDirectory = File(context.filesDir.parentFile, DATABASES_DIR_NAME)
                performDeletion(context, dbDirectory)
                val altPath = context.applicationInfo.dataDir + DATABASES_PATH_BLOCK
                val altDirectory = File(altPath)
                performDeletion(context, altDirectory)
        }

        private fun performDeletion(context: Context, directory : File) {
                if (directory.exists() && directory.isDirectory) {
                        directory.listFiles { dir, name ->
                                name.startsWith(FILENAME_PREFIX) && name.startsWith(DATABASE_NAME).not()
                        }?.forEach { file ->
                                try {
                                        if (file.exists() && file.isFile) {
                                                file.delete()
                                        }
                                        val altFile = context.getDatabasePath(file.name)
                                        if (altFile.exists() && altFile.isFile) {
                                                altFile.delete()
                                        }
                                } catch (err: Exception) {
                                        err.message?.let { Log.e(LOG_TAG, it) }
                                }
                        }
                }
        }

        private fun doesDatabaseExist(context: Context): Boolean {
                val dbFile = context.getDatabasePath(DATABASE_NAME)
                val path = context.applicationInfo.dataDir + DATABASES_PATH_BLOCK + DATABASE_NAME
                val file = File(path)
                return dbFile.exists() && file.exists()
        }

        @Throws(IOException::class)
        private fun copyDatabase(context: Context) {
                var inStream: InputStream? = null
                var outStream: OutputStream? = null
                val destinationPath = context.applicationInfo.dataDir + DATABASES_PATH_BLOCK + DATABASE_NAME
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
                } catch (err: Exception) {
                        err.message?.let {
                                Log.e(LOG_TAG, it)
                                throw Error(it)
                        }
                } finally {
                        inStream?.close()
                        outStream?.close()
                }
        }
}
