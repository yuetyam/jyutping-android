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
fun GwongWanView(entries: List<GwongWanCharacter>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                GwongWanWordLabel(entries.firstOrNull()?.word ?: "?")
                entries.map {
                        Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                                HorizontalDivider()
                                GwongWanPronunciationView(it)
                        }
                }
        }
}

@Composable
private fun GwongWanWordLabel(word: String) {
        Row {
                Text(text = "文字")
                Text(text = ": ")
                Text(text = word)
                Text(text = "《大宋重修廣韻》 陳彭年等 北宋", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        }
}

@Composable
private fun GwongWanPronunciationView(entry: GwongWanCharacter) {
        Column {
                Row {
                        Text(text = "讀音")
                        Text(text = ": ")
                        Text(text = entry.pronunciation)
                }
                Row {
                        Text(text = "釋義")
                        Text(text = ": ")
                        Text(text = entry.interpretation)
                }
        }
}
