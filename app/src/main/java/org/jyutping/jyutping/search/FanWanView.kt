package org.jyutping.jyutping.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun FanWanView(entries: List<FanWanCuetYiu>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                FanWanWordLabel(entries.firstOrNull()?.word ?: "?")
                entries.map {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                FanWanPronunciationView(it)
                        }
                }
        }
}

@Composable
private fun FanWanWordLabel(word: String) {
        Row {
                Text(text = "檢索")
                Text(text = ": ")
                Text(text = word)
                Text(text = "《分韻撮要》", color = MaterialTheme.colorScheme.secondary)
        }
}

@Composable
private fun FanWanPronunciationView(entry: FanWanCuetYiu) {
        val homophoneText = if (entry.homophones.isEmpty()) null else entry.homophones.joinToString(separator = " ")
        Column {
                Row {
                        Text(text = "讀音")
                        Text(text = ": ")
                        Text(text = entry.pronunciation)
                }
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Row {
                                Text(text = "轉寫")
                                Text(text = ": ")
                                Text(text = entry.romanization)
                        }
                        Text(text = "ipa", color = MaterialTheme.colorScheme.secondary) // TODO: Fix IPA
                }
                homophoneText?.let {
                        Row {
                                Text(text = "同音")
                                Text(text = ": ")
                                Text(text = it)
                        }
                }
                Row {
                        Text(text = "釋義")
                        Text(text = ": ")
                        Text(text = entry.interpretation)
                }
        }
}
