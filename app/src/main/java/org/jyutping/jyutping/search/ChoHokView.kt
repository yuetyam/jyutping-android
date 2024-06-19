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
import androidx.compose.ui.unit.sp

@Composable
fun ChoHokView(entries: List<ChoHokYuetYamCitYiu>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                ChoHokWordLabel(entries.firstOrNull()?.word ?: "?")
                entries.map {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                ChoHokPronunciationView(it)
                        }
                }
        }
}

@Composable
private fun ChoHokWordLabel(word: String) {
        Row {
                Text(text = "文字")
                Text(text = ": ")
                Text(text = word)
                Text(text = "《初學粵音切要》 湛約翰 1855 香港", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        }
}

@Composable
private fun ChoHokPronunciationView(entry: ChoHokYuetYamCitYiu) {
        val homophoneText = if (entry.homophones.isEmpty()) null else entry.homophones.joinToString(separator = " ")
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row {
                                Text(text = "原文")
                                Text(text = ": ")
                                Text(text = entry.pronunciation)
                        }
                        Text(text = entry.tone)
                        Text(text = entry.faancit)
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
        }
}
