package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun JyutpingInitialsScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                item {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                        ) {
                                Text(text = "例字", modifier = Modifier.weight(0.4f))
                                Text(text = "聲母", modifier = Modifier.weight(0.3f))
                                Text(text = "國際音標", modifier = Modifier.weight(0.3f))
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "巴", syllable = "baa1", jyutping = "b", ipa = "[ p ]")
                                InitialLabel(word = "趴", syllable = "paa1", jyutping = "p", ipa = "[ pʰ ]")
                                InitialLabel(word = "媽", syllable = "maa1", jyutping = "m", ipa = "[ m ]")
                                InitialLabel(word = "花", syllable = "faa1", jyutping = "f", ipa = "[ f ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "打", syllable = "daa2", jyutping = "d", ipa = "[ t ]")
                                InitialLabel(word = "他", syllable = "taa1", jyutping = "t", ipa = "[ tʰ ]")
                                InitialLabel(word = "拿", syllable = "naa4", jyutping = "n", ipa = "[ n ]")
                                InitialLabel(word = "啦", syllable = "laa1", jyutping = "l", ipa = "[ l ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "家", syllable = "gaa1", jyutping = "g", ipa = "[ k ]")
                                InitialLabel(word = "卡", syllable = "kaa1", jyutping = "k", ipa = "[ kʰ ]")
                                InitialLabel(word = "蝦", syllable = "haa1", jyutping = "h", ipa = "[ h ]")
                                InitialLabel(word = "牙", syllable = "ngaa4", jyutping = "ng", ipa = "[ ŋ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "瓜", syllable = "gwaa1", jyutping = "gw", ipa = "[ kʷ ]")
                                InitialLabel(word = "夸", syllable = "kwaa1", jyutping = "kw", ipa = "[ kʷʰ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "渣", syllable = "zaa1", jyutping = "z", ipa = "t͡s ~ t͡ʃ")
                                InitialLabel(word = "叉", syllable = "caa1", jyutping = "c", ipa = "t͡sʰ ~ t͡ʃʰ")
                                InitialLabel(word = "沙", syllable = "saa1", jyutping = "s", ipa = "s ~ ʃ")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        ) {
                                InitialLabel(word = "蛙", syllable = "waa1", jyutping = "w", ipa = "[ w ]")
                                InitialLabel(word = "也", syllable = "jaa5", jyutping = "j", ipa = "[ j ]")
                        }
                }
        }
}

@Composable
private fun InitialLabel(word: String, syllable: String, jyutping: String, ipa: String) {
        SelectionContainer {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                        Text(
                                text = "$word $syllable",
                                modifier = Modifier.weight(0.4f),
                                color = colorScheme.onBackground
                        )
                        Text(
                                text = jyutping,
                                modifier = Modifier.weight(0.3f),
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily.Monospace
                        )
                        Text(
                                text = ipa,
                                modifier = Modifier.weight(0.3f),
                                color = colorScheme.onBackground
                        )
                }
        }
}
