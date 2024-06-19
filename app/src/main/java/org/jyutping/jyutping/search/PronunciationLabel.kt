package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.linguistics.Jyutping2IPA

@Composable
fun PronunciationLabel(pronunciation: Pronunciation) {
        val romanization = pronunciation.romanization
        val ipaText: String? = when {
                romanization.contains(" ") -> null
                else -> Jyutping2IPA.IPAText(romanization)
        }
        val homophoneText: String? = if (pronunciation.homophones.isEmpty()) null else pronunciation.homophones.joinToString(separator = " ")
        val collocationText: String? = when (pronunciation.collocations.size) {
                0 -> null
                in 1..5 -> pronunciation.collocations.joinToString(separator = " ")
                else -> pronunciation.collocations.take(5).joinToString(separator = " ")
        }
        Column {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Row {
                                Text(text = "讀音")
                                Text(text = ": ")
                                Text(text = romanization)
                        }
                        ipaText?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.secondary)
                        }
                }
                homophoneText?.let {
                        Row {
                                Text(text = "同音")
                                Text(text = ": ")
                                Text(text = it)
                        }
                }
                collocationText?.let {
                        Row {
                                Text(text = "詞例")
                                Text(text = ": ")
                                Text(text = it)
                        }
                }
        }
}
