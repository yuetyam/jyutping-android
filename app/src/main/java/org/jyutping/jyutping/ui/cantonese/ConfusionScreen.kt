package org.jyutping.jyutping.ui.cantonese

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.beust.klaxon.Klaxon
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun ConfusionScreen(navController: NavHostController) {
        val entries = remember { mutableStateOf<List<Confusion>>(listOf()) }
        LaunchedEffect(Unit) {
                val inputStream = navController.context.assets.open("confusion.json")
                val bufferReader = BufferedReader(InputStreamReader(inputStream))
                val jsonString = bufferReader.readText()
                val parsed = Klaxon().parseArray<Confusion>(jsonString)
                parsed?.let {
                        entries.value = it
                }
        }
        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
                if (entries.value.isNotEmpty()) {
                        items(entries.value) {
                                ConfusionView(confusion = it)
                        }
                } else {
                        item {
                                Text(text = "loading...")
                        }
                }
        }
}

@Composable
private fun ConfusionView(confusion: Confusion) {
        Row(
                modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(text = confusion.simplified)
                Column {
                        confusion.traditional.map {
                                Row {
                                        Box {
                                                Row(
                                                        modifier = Modifier.alpha(0f),
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(text = "佔")
                                                        Text(text = "gwaang6")
                                                }
                                                Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(text = it.character)
                                                        Text(text = it.romanization)
                                                }
                                        }
                                        Text(text = it.note)
                                }
                        }
                }
                Spacer(modifier = Modifier.weight(1.0f))
        }
}

private data class Confusion (
        val simplified: String,
        val traditional: List<ConfusionTraditional>
)
private data class ConfusionTraditional (
        val character: String,
        val romanization: String,
        val note: String
)
