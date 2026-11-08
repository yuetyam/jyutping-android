package org.jyutping.jyutping.memory

import android.content.Context
import android.database.sqlite.SQLiteDatabaseLockedException
import android.net.Uri
import android.provider.DocumentsContract
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jyutping.jyutping.R

/** Process lifetime owner: neither rotation nor hiding the IME cancels an in-progress transfer. */
class InputMemoryTransferController internal constructor(
        context: Context,
        private val helper: InputMemoryHelper = InputMemoryHelper.shared(context),
        private val openImportStream: (Uri) -> InputStream? = context.contentResolver::openInputStream
) {
        companion object {
                @Volatile private var instance: InputMemoryTransferController? = null
                fun shared(context: Context): InputMemoryTransferController = instance ?: synchronized(this) {
                        instance ?: InputMemoryTransferController(context.applicationContext).also { instance = it }
                }
                fun suggestedFilename(): String = "memory-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())}.sqlite3"
                // Bound provider reads, including providers that do not report a size.
                internal const val MAX_IMPORT_BYTES = 256L * 1024 * 1024
        }

        enum class Operation { Export, Import }
        data class State(val operation: Operation? = null, val isRunning: Boolean = false)
        data class Feedback(val resource: Int, val count: Long? = null)

        private val resolver = context.contentResolver
        private val cacheDir = context.cacheDir
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        private val mutableState = MutableStateFlow(State())
        val state = mutableState.asStateFlow()
        private val mutableFeedback = MutableStateFlow<Feedback?>(null)
        val feedback = mutableFeedback.asStateFlow()

        @Synchronized
        fun beginPicker(operation: Operation): Boolean {
                if (mutableState.value.operation != null || !helper.beginTransfer()) {
                        mutableFeedback.value = Feedback(busyMessage(operation))
                        return false
                }
                mutableFeedback.value = null
                mutableState.value = State(operation)
                return true
        }

        @Synchronized
        fun completePicker(operation: Operation, uri: Uri?) {
                if (mutableState.value.isRunning) return
                if (uri == null) {
                        if (mutableState.value.operation == operation) finish()
                        return
                }
                // ActivityResultRegistry can deliver a result after process recreation.
                if (mutableState.value.operation == null && !beginPicker(operation)) {
                        if (operation == Operation.Export) deleteDocument(uri)
                        return
                }
                if (mutableState.value.operation != operation) return
                mutableState.value = State(operation, isRunning = true)
                scope.launch {
                        try {
                                val count = when (operation) {
                                        Operation.Export -> export(uri)
                                        Operation.Import -> import(uri)
                                }
                                mutableFeedback.value = Feedback(if (operation == Operation.Export) R.plurals.input_memory_export_success else R.plurals.input_memory_import_success, count)
                        } catch (error: CancellationException) {
                                throw error
                        } catch (error: Exception) {
                                val message = when {
                                        error is InputMemoryBusyError || error is SQLiteDatabaseLockedException -> busyMessage(operation)
                                        operation == Operation.Export && error is InputMemoryValidationError -> R.string.input_memory_export_validation_failed
                                        operation == Operation.Export && error is PublicationError -> R.string.input_memory_export_write_failed
                                        operation == Operation.Export -> R.string.input_memory_export_failed
                                        error is InputMemoryImportError.UnsupportedFormat -> R.string.input_memory_import_unsupported
                                        else -> R.string.input_memory_import_failed
                                }
                                mutableFeedback.value = Feedback(message)
                        } finally {
                                finish()
                        }
                }
        }

        @Synchronized
        fun failPicker(operation: Operation) {
                if (mutableState.value.operation != operation || mutableState.value.isRunning) return
                finish()
                mutableFeedback.value = Feedback(if (operation == Operation.Export) R.string.input_memory_export_failed else R.string.input_memory_import_failed)
        }

        fun dismissFeedback() {
                mutableFeedback.value = null
        }

        @Synchronized
        private fun finish() {
                if (mutableState.value.operation != null) {
                        helper.endTransfer()
                        mutableState.value = State()
                }
        }

        private suspend fun export(uri: Uri): Long {
                var workspace: File? = null
                var published = false
                try {
                        workspace = Files.createTempDirectory(cacheDir.toPath(), "memory-publish-").toFile()
                        val file = File(workspace, "export.sqlite3")
                        val count = InputMemoryExporter.exportReserved(helper, file)
                        try {
                                val output = resolver.openOutputStream(uri, "wt") ?: throw IOException("No output stream")
                                output.use { destination ->
                                        file.inputStream().use { copy(it, destination) }
                                        destination.flush()
                                }
                        } catch (error: CancellationException) {
                                throw error
                        } catch (error: Exception) {
                                throw PublicationError(error)
                        }
                        published = true
                        return count
                } finally {
                        workspace?.deleteRecursively()
                        if (!published) deleteDocument(uri)
                }
        }

        private suspend fun import(uri: Uri): Long {
                val workspace = Files.createTempDirectory(cacheDir.toPath(), "memory-import-").toFile()
                try {
                        val file = File(workspace, "import.sqlite3")
                        val input = openImportStream(uri) ?: throw IOException("No input stream")
                        input.use { source -> file.outputStream().use { copy(source, it, MAX_IMPORT_BYTES) } }
                        return InputMemoryImporter.importReserved(helper, file)
                } finally {
                        workspace.deleteRecursively()
                }
        }

        private suspend fun copy(source: InputStream, destination: OutputStream, limit: Long = Long.MAX_VALUE) {
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var total = 0L
                while (true) {
                        currentCoroutineContext().ensureActive()
                        val size = source.read(buffer)
                        if (size < 0) break
                        total += size
                        if (total > limit) throw InputMemoryImportError.UnsupportedFormat
                        destination.write(buffer, 0, size)
                }
                currentCoroutineContext().ensureActive()
        }

        private fun deleteDocument(uri: Uri) {
                try {
                        DocumentsContract.deleteDocument(resolver, uri)
                } catch (_: Exception) {
                        // Some providers do not support deletion; preserve the original failure.
                }
        }
        private fun busyMessage(operation: Operation): Int = if (operation == Operation.Export) R.string.input_memory_export_busy else R.string.input_memory_import_busy
        private class PublicationError(cause: Exception) : IOException(cause)
}
