package org.jyutping.jyutping.app.cantonese

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jyutping.jyutping.speech.Speaker
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Throws(IOException::class)
private fun fetchConfusionJsonContent(navController: NavHostController): String? {
        val inputStream = navController.context.assets.open("confusion.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        var lineString: String?
        val builder = StringBuilder()
        try {
                while (reader.readLine().also { lineString = it } != null) {
                        builder.append(lineString)
                        builder.append("\n")
                }
        } catch (e: IOException) {
                throw Error(e.message)
        } finally {
                reader.close()
        }
        if (builder.isEmpty()) return null
        return builder.toString()
}

@Composable
fun ConfusionScreen(navController: NavHostController) {
        val entries = remember { mutableStateOf<List<ConfusionElement>>(listOf()) }
        LaunchedEffect(Unit) {
                val fetched = fetchConfusionJsonContent(navController)
                val parsed = fetched?.let { Json.decodeFromString<List<ConfusionElement>>(it) }
                parsed?.let { entries.value = it }
        }
        SelectionContainer {
                LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        items(entries.value) {
                                ConfusionElementView(it)
                        }
                }
        }
}

@Composable
private fun ConfusionElementView(element: ConfusionElement) {
        Row(
                modifier = Modifier
                        .background(
                                color = colorScheme.background,
                                shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(text = element.simplified, color = colorScheme.onBackground)
                Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                        element.traditional.map {
                                Row(
                                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Speaker(romanization = it.romanization, cantonese = it.character)
                                                Box {
                                                        Row(
                                                                modifier = Modifier.alpha(0f),
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Text(text = "佔")
                                                                Text(text = "cung4")
                                                        }
                                                        Row(
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                                Text(text = it.character, color = colorScheme.onBackground)
                                                                Text(text = it.romanization, color = colorScheme.onBackground)
                                                        }
                                                }
                                        }
                                        Text(text = it.note, color = colorScheme.onBackground)
                                }
                        }
                }
                Spacer(modifier = Modifier.weight(1.0f))
        }
}

@Serializable
private data class ConfusionElement (
        val simplified: String,
        val traditional: List<ConfusionTraditional>
)

@Serializable
private data class ConfusionTraditional (
        val character: String,
        val romanization: String,
        val note: String
)
