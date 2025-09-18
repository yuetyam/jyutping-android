package org.jyutping.jyutping.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.linguistics.OldCantonese
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.speech.Speaker
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun ChoHokView(entries: List<ChoHokYuetYamCitYiu>) {
        Column {
                Text(
                        text = "《初學粵音切要》 湛約翰 1855 香港",
                        modifier = Modifier.padding(horizontal = 8.dp).alpha(0.75f),
                        color = colorScheme.onBackground,
                        fontSize = 13.sp
                )
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        entries.firstOrNull()?.word?.let {
                                WordTextLabel(it)
                        }
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
}

@Composable
private fun ChoHokPronunciationView(entry: ChoHokYuetYamCitYiu) {
        val ipaText = OldCantonese.IPAText(entry.romanization)
        val homophoneText = if (entry.homophones.isEmpty()) null else entry.homophones.joinToString(separator = PresetString.SPACE)
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row {
                                Text(
                                        text = "讀音",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Text(
                                        text = entry.pronunciation,
                                        color = colorScheme.onBackground
                                )
                        }
                        Text(
                                text = entry.tone,
                                color = colorScheme.onBackground
                        )
                        Text(
                                text = entry.faancit,
                                color = colorScheme.onBackground
                        )
                }
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                        Spacer(modifier = Modifier.weight(1f))
                        Speaker(romanization = entry.romanization)
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
