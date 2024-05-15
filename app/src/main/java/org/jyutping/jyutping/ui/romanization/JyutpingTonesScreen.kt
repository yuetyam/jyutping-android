package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                                Text(text = "例字", modifier = Modifier.weight(0.3f))
                                Text(text = "調值", modifier = Modifier.weight(0.25f))
                                Text(text = "聲調", modifier = Modifier.weight(0.25f))
                                Text(text = "粵拼", modifier = Modifier.weight(0.2f))
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(shape = RoundedCornerShape(size = 8.dp))
                                        .background(color = MaterialTheme.colorScheme.background)
                        ) {
                                ToneLabel(word = "芬", syllable = "fan1", value = "55/53", name = "陰入", jyutping = "1")
                                ToneLabel(word = "粉", syllable = "fan2", value = "35", name = "陰上", jyutping = "2")
                                ToneLabel(word = "訓", syllable = "fan3", value = "33", name = "陰去", jyutping = "3")
                                ToneLabel(word = "焚", syllable = "fan4", value = "21/11", name = "陽平", jyutping = "4")
                                ToneLabel(word = "奮", syllable = "fan5", value = "13/23", name = "陽上", jyutping = "5")
                                ToneLabel(word = "份", syllable = "fan6", value = "22", name = "陽去", jyutping = "6")
                                ToneLabel(word = "忽", syllable = "fat1", value = "5", name = "高陰入", jyutping = "1")
                                ToneLabel(word = "法", syllable = "faat3", value = "3", name = "低陰入", jyutping = "3")
                                ToneLabel(word = "罰", syllable = "fat6", value = "2", name = "陽入", jyutping = "6")
                        }
                }
        }
}

@Composable
private fun ToneLabel(word: String, syllable: String, value: String, name: String, jyutping: String) {
        SelectionContainer {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                        Text(text = "$word $syllable", modifier = Modifier.weight(0.3f))
                        Text(text = value, modifier = Modifier.weight(0.25f))
                        Text(text = name, modifier = Modifier.weight(0.25f))
                        Text(text = jyutping, modifier = Modifier.weight(0.2f))
                }
        }
}
