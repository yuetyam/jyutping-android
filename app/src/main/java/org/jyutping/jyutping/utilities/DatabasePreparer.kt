package org.jyutping.jyutping.utilities

import android.content.Context
import androidx.annotation.WorkerThread
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.Elephant
import java.io.File
import java.io.FileOutputStream

object DatabasePreparer {

        private const val SOURCE_FILENAME: String = "appdb.sqlite3"
        const val DATABASE_NAME: String = "appdb-v${BuildConfig.VERSION_CODE}-using.sqlite3"
        private const val TMP_FILENAME: String = "appdb-v${BuildConfig.VERSION_CODE}-tmp.sqlite3"
        private const val FILENAME_PREFIX: String = "appdb-v"

        @Synchronized
        @WorkerThread
        fun prepare(context: Context) {
                val target = context.getDatabasePath(DATABASE_NAME)
                if (target.exists().not()) {
                        val temporary = context.getDatabasePath(TMP_FILENAME)
                        copyDatabase(context, temporary, target)
                        deleteOldDatabases(context)
                }
                 Elephant.connectDatabase(context)
        }
        private fun copyDatabase(context: Context, temporary: File, target: File) {
                target.parentFile?.mkdirs()
                if (temporary.exists()) {
                        temporary.delete()
                }
                context.assets.open(SOURCE_FILENAME).use { input ->
                        FileOutputStream(temporary).use { output ->
                                input.copyTo(output)
                                output.fd.sync()
                        }
                }
                temporary.renameTo(target)
        }
        private fun deleteOldDatabases(context: Context) {
                val directory = context.getDatabasePath(DATABASE_NAME).parentFile ?: return
                if (directory.exists().not() || directory.isDirectory.not()) return
                directory.listFiles { _, name ->
                        name.startsWith(FILENAME_PREFIX) && name.startsWith(DATABASE_NAME).not()
                }?.forEach { file ->
                        if (file.exists() && file.isFile) {
                                file.delete()
                        }
                }
        }
}
