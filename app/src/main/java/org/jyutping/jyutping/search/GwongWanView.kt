package org.jyutping.jyutping.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun GwongWanView(entries: List<GwongWanCharacter>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.background, shape = RoundedCornerShape(8.dp))
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
                Text(text = "文字", color = colorScheme.onBackground)
                SeparatorMark()
                Text(text = word, color = colorScheme.onBackground)
                Text(
                        text = "《大宋重修廣韻》 陳彭年等 北宋",
                        modifier = Modifier.alpha(0.75f),
                        color = colorScheme.onBackground,
                        fontSize = 14.sp
                )
        }
}

@Composable
private fun GwongWanPronunciationView(entry: GwongWanCharacter) {
        Column {
                Row {
                        Text(text = "讀音", color = colorScheme.onBackground)
                        SeparatorMark()
                        Text(text = entry.pronunciation, color = colorScheme.onBackground)
                }
                Row {
                        Text(text = "釋義", color = colorScheme.onBackground)
                        SeparatorMark()
                        Text(text = entry.interpretation, color = colorScheme.onBackground)
                }
        }
}
