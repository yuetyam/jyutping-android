package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.linguistics.Jyutping2IPA
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun PronunciationLabel(pronunciation: Pronunciation) {
        val romanization = pronunciation.romanization
        val ipaText: String? = when {
                romanization.contains(" ") -> null
                else -> Jyutping2IPA.IPAText(romanization)
        }
        val homophoneText: String? = if (pronunciation.homophones.isEmpty()) null else pronunciation.homophones.joinToString(separator = PresetString.SPACE)
        val collocationText: String? = when (pronunciation.collocations.size) {
                0 -> null
                in 1..5 -> pronunciation.collocations.joinToString(separator = PresetString.SPACE)
                else -> pronunciation.collocations.take(5).joinToString(separator = PresetString.SPACE)
        }
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Row {
                                Text(
                                        text = "讀音",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Text(
                                        text = romanization,
                                        color = colorScheme.onBackground
                                )
                        }
                        ipaText?.let {
                                Text(
                                        text = it,
                                        modifier = Modifier.alpha(0.75f),
                                        color = colorScheme.onBackground
                                )
                        }
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
                collocationText?.let {
                        Row {
                                Text(
                                        text = "詞例",
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
