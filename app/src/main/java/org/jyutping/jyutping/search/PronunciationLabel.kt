package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.linguistics.Jyutping2IPA
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.speech.Speaker
import org.jyutping.jyutping.ui.common.SeparatorMark

@Composable
fun PronunciationLabel(pronunciation: Pronunciation, word: String? = null) {
        val romanization = pronunciation.romanization
        val ipaText: String? = if (romanization.contains(PresetString.SPACE)) null else Jyutping2IPA.IPAText(romanization)
        val homophoneText: String? = if (pronunciation.homophones.isEmpty()) null else pronunciation.homophones.joinToString(separator = PresetString.SPACE)
        val collocationText: String? = if (pronunciation.collocations.isEmpty()) null else pronunciation.collocations.take(5).joinToString(separator = PresetString.SPACE)
        val ttsCollocationText: String? = if (pronunciation.collocations.isEmpty()) null else pronunciation.collocations.take(5).joinToString(separator = "；")
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(
                                modifier = Modifier.weight(1f),
                        ) {
                                Text(
                                        text = "讀音",
                                        color = colorScheme.onBackground
                                )
                                SeparatorMark()
                                Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = romanization,
                                                color = colorScheme.onBackground
                                        )
                                        ipaText?.let {
                                                Text(
                                                        text = it,
                                                        modifier = Modifier.alpha(0.75f),
                                                        color = colorScheme.onBackground
                                                )
                                        }
                                }
                        }
                        Speaker(romanization = romanization, cantonese = word)
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
                        Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Row(
                                        modifier = Modifier.weight(1f)
                                ) {
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
                                ttsCollocationText?.let { text ->
                                        Speaker(cantonese = text)
                                }
                        }
                }
                pronunciation.descriptions.forEach {
                        Row(
                                modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                                Text(
                                        text = "釋義",
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
