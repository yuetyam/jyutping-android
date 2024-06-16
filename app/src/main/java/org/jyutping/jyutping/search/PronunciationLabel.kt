package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PronunciationLabel(pronunciation: Pronunciation) {
        val homophoneText: String? = if (pronunciation.homophones.isEmpty()) null else pronunciation.homophones.joinToString(separator = " ")
        val collocationText: String? = when (pronunciation.collocations.count()) {
                0 -> null
                in 1..5 -> pronunciation.collocations.joinToString(separator = " ")
                else -> pronunciation.collocations.take(5).joinToString(separator = " ")
        }
        Column {
                Row {
                        Text(text = "讀音")
                        Text(text = ": ")
                        Text(text = pronunciation.romanization)
                        // TODO: IPA
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
