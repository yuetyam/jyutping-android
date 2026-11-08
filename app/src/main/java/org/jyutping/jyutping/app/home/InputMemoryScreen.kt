package org.jyutping.jyutping.app.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.memory.InputMemoryHelper
import org.jyutping.jyutping.memory.InputMemoryTransferController
import org.jyutping.jyutping.memory.InputMemoryTransferController.Operation

@Composable
fun InputMemoryScreen() {
        val context = LocalContext.current
        val controller = remember { InputMemoryTransferController.shared(context) }
        val state by controller.state.collectAsState()
        val isTransferring by InputMemoryHelper.shared(context).isTransferring.collectAsState()
        val feedback by controller.feedback.collectAsState()
        val exporter = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.sqlite3")) {
                controller.completePicker(Operation.Export, it)
        }
        val importer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                controller.completePicker(Operation.Import, it)
        }
        LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                item {
                        Button(
                                onClick = {
                                        if (controller.beginPicker(Operation.Export)) {
                                                try {
                                                        exporter.launch(InputMemoryTransferController.suggestedFilename())
                                                } catch (_: Exception) {
                                                        controller.failPicker(Operation.Export)
                                                }
                                        }
                                },
                                enabled = isTransferring.negative
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Outlined.FileUpload,
                                                contentDescription = null
                                        )
                                        Text(
                                                text = stringResource(R.string.input_memory_export_button)
                                        )
                                        Icon(
                                                imageVector = Icons.Outlined.FileUpload,
                                                contentDescription = null,
                                                modifier = Modifier.alpha(0f)
                                        )
                                }
                        }
                }
                item {
                        Button(
                                onClick = {
                                        if (controller.beginPicker(Operation.Import)) {
                                                try {
                                                        importer.launch(arrayOf("application/vnd.sqlite3", "application/x-sqlite3", "application/octet-stream"))
                                                } catch (_: Exception) {
                                                        controller.failPicker(Operation.Import)
                                                }
                                        }
                                },
                                enabled = isTransferring.negative
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Outlined.FileDownload,
                                                contentDescription = null
                                        )
                                        Text(
                                                text = stringResource(R.string.input_memory_import_button)
                                        )
                                        Icon(
                                                imageVector = Icons.Outlined.FileDownload,
                                                contentDescription = null,
                                                modifier = Modifier.alpha(0f)
                                        )
                                }
                        }
                }
                if (state.isRunning) {
                        item {
                                CircularProgressIndicator()
                        }
                        item {
                                Text(
                                        text = stringResource(if (state.operation == Operation.Export) R.string.input_memory_export_progress else R.string.input_memory_import_progress)
                                )
                        }
                }
        }
        feedback?.let { result ->
                val count = result.count
                val message = if (count == null) stringResource(result.resource) else stringResource(result.resource, count)
                AlertDialog(
                        onDismissRequest = controller::dismissFeedback,
                        title = { Text(stringResource(R.string.input_memory_screen_title)) },
                        text = { Text(message) },
                        confirmButton = {
                                TextButton(onClick = controller::dismissFeedback) { Text(stringResource(R.string.input_memory_dismiss)) }
                        }
                )
        }
}
