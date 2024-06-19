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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YingWaaView(entries: List<YingWaaFanWan>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                YingWaaWordLabel(entries.firstOrNull()?.word ?: "?")
                entries.map {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                YingWaaPronunciationView(it)
                        }
                }
        }
}

@Composable
private fun YingWaaWordLabel(word: String) {
        Row {
                Text(text = "文字")
                Text(text = ": ")
                Text(text = word)
                Text(text = "《英華分韻撮要》衛三畏 1856 廣州", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        }
}

@Composable
private fun YingWaaPronunciationView(entry: YingWaaFanWan) {
        val pronunciationMark = entry.pronunciationMark?.let { YingWaaFanWan.processPronunciationMark(it) }
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
                        pronunciationMark?.let {
                                Text(text = it, fontStyle = FontStyle.Italic)
                        }
                        entry.interpretation?.let {
                                Text(text = it)
                        }
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
