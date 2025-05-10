package org.jyutping.jyutping.mainapp.romanization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JyutpingTonesScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                        ) {
                                Text(text = "例字", modifier = Modifier.weight(0.25f))
                                Text(text = "粵拼", modifier = Modifier.weight(0.25f))
                                Text(text = "聲調", modifier = Modifier.weight(0.25f))
                                Text(text = "調值", modifier = Modifier.weight(0.25f))
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(8.dp))
                        ) {
                                ToneLabel(word = "芬", syllable = "fan1", toneName = "陰入", toneValue = "55/53")
                                ToneLabel(word = "粉", syllable = "fan2", toneName = "陰上", toneValue = "35")
                                ToneLabel(word = "糞", syllable = "fan3", toneName = "陰去", toneValue = "33")
                                ToneLabel(word = "焚", syllable = "fan4", toneName = "陽平", toneValue = "21/11")
                                ToneLabel(word = "憤", syllable = "fan5", toneName = "陽上", toneValue = "13/23")
                                ToneLabel(word = "份", syllable = "fan6", toneName = "陽去", toneValue = "22")
                                ToneLabel(word = "弗", syllable = "fat1", toneName = "高陰入", toneValue = "5")
                                ToneLabel(word = "法", syllable = "faat3", toneName = "低陰入", toneValue = "3")
                                ToneLabel(word = "佛", syllable = "fat6", toneName = "陽入", toneValue = "2")
                        }
                }
        }
}

@Composable
private fun ToneLabel(word: String, syllable: String, toneName: String, toneValue: String) {
        SelectionContainer {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                        Text(
                                text = word,
                                modifier = Modifier.weight(0.25f),
                                color = colorScheme.onBackground
                        )
                        Text(
                                text = syllable,
                                modifier = Modifier.weight(0.25f),
                                color = colorScheme.onBackground
                        )
                        Text(
                                text = toneName,
                                modifier = Modifier.weight(0.25f),
                                color = colorScheme.onBackground
                        )
                        Text(
                                text = toneValue,
                                modifier = Modifier.weight(0.25f),
                                color = colorScheme.onBackground
                        )
                }
        }
}
