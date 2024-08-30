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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.linguistics.OldCantonese
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun YingWaaView(entries: List<YingWaaFanWan>) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorScheme.background, shape = RoundedCornerShape(8.dp))
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
                Text(
                        text = "文字",
                        color = colorScheme.onBackground
                )
                SeparatorMark()
                Text(
                        text = word,
                        color = colorScheme.onBackground
                )
                Text(
                        text = "《英華分韻撮要》衛三畏 1856 廣州",
                        modifier = Modifier.alpha(0.75f),
                        color = colorScheme.onBackground,
                        fontSize = 14.sp
                )
        }
}

@Composable
private fun YingWaaPronunciationView(entry: YingWaaFanWan) {
        val ipaText = OldCantonese.IPAText(entry.romanization)
        val pronunciationMark = entry.pronunciationMark?.let { YingWaaFanWan.processPronunciationMark(it) }
        val homophoneText = if (entry.homophones.isEmpty()) null else entry.homophones.joinToString(separator = PresetString.SPACE)
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row {
                                Text(
                                        text = "原文",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Text(
                                        text = entry.pronunciation,
                                        color = colorScheme.onBackground
                                )
                        }
                        pronunciationMark?.let {
                                Text(
                                        text = it,
                                        color = colorScheme.onBackground,
                                        fontStyle = FontStyle.Italic
                                )
                        }
                        entry.interpretation?.let {
                                Text(
                                        text = it,
                                        color = colorScheme.onBackground
                                )
                        }
                }
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Row {
                                Text(
                                        text = "轉寫",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Text(
                                        text = entry.romanization,
                                        color = colorScheme.onBackground
                                )
                        }
                        Text(
                                text = ipaText,
                                modifier = Modifier.alpha(0.75f),
                                color = colorScheme.onBackground
                        )
                }
                homophoneText?.let {
                        Row {
                                Text(
                                        text = "同音",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Text(
                                        text = it,
                                        color = colorScheme.onBackground
                                )
                        }
                }
        }
}
