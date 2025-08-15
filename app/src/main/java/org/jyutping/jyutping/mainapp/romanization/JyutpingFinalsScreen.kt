package org.jyutping.jyutping.mainapp.romanization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.speech.Speaker

@Composable
fun JyutpingFinalsScreen() {
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
                                Text(text = "例字", modifier = Modifier.weight(0.45f))
                                Text(text = "韻母", modifier = Modifier.weight(0.25f))
                                Text(text = "國際音標", modifier = Modifier.weight(0.3f))
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "駕", syllable = "gaa3", jyutping = "aa", ipa = "[ aː ]")
                                FinalLabel(word = "界", syllable = "gaai3", jyutping = "aai", ipa = "[ aːi ]")
                                FinalLabel(word = "教", syllable = "gaau3", jyutping = "aau", ipa = "[ aːu ]")
                                FinalLabel(word = "鑑", syllable = "gaam3", jyutping = "aam", ipa = "[ aːm ]")
                                FinalLabel(word = "諫", syllable = "gaan3", jyutping = "aan", ipa = "[ aːn ]")
                                FinalLabel(word = "耕", syllable = "gaang1", jyutping = "aang", ipa = "[ aːŋ ]")
                                FinalLabel(word = "甲", syllable = "gaap3", jyutping = "aap", ipa = "[ aːp̚ ]")
                                FinalLabel(word = "戛", syllable = "gaat3", jyutping = "aat", ipa = "[ aːt̚ ]")
                                FinalLabel(word = "格", syllable = "gaak3", jyutping = "aak", ipa = "[ aːk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "㗎", syllable = "ga3", jyutping = "a", ipa = "[ ɐ ]")
                                FinalLabel(word = "計", syllable = "gai3", jyutping = "ai", ipa = "[ ɐi ]")
                                FinalLabel(word = "救", syllable = "gau3", jyutping = "au", ipa = "[ ɐu ]")
                                FinalLabel(word = "禁", syllable = "gam3", jyutping = "am", ipa = "[ ɐm ]")
                                FinalLabel(word = "斤", syllable = "gan1", jyutping = "an", ipa = "[ ɐn ]")
                                FinalLabel(word = "庚", syllable = "gang1", jyutping = "ang", ipa = "[ ɐŋ ]")
                                FinalLabel(word = "急", syllable = "gap1", jyutping = "ap", ipa = "[ ɐp̚ ]")
                                FinalLabel(word = "吉", syllable = "gat1", jyutping = "at", ipa = "[ ɐt̚ ]")
                                FinalLabel(word = "北", syllable = "bak1", jyutping = "ak", ipa = "[ ɐk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "嘅", syllable = "ge3", jyutping = "e", ipa = "[ ɛː ]")
                                FinalLabel(word = "記", syllable = "gei3", jyutping = "ei", ipa = "[ ei ]")
                                FinalLabel(word = "掉", syllable = "deu6", jyutping = "eu", ipa = "[ ɛːu ]")
                                FinalLabel(word = "𦧷", syllable = "lem2", jyutping = "em", ipa = "[ ɛːm ]")
                                FinalLabel(word = "鏡", syllable = "geng3", jyutping = "eng", ipa = "[ ɛːŋ ]")
                                FinalLabel(word = "夾", syllable = "gep6", jyutping = "ep", ipa = "[ ɛːp̚ ]")
                                FinalLabel(word = "坺", syllable = "pet6", jyutping = "et", ipa = "[ ɛːt̚ ]")
                                FinalLabel(word = "踢", syllable = "tek3", jyutping = "ek", ipa = "[ ɛːk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "意", syllable = "ji3", jyutping = "i", ipa = "[ iː ]")
                                FinalLabel(word = "叫", syllable = "giu3", jyutping = "iu", ipa = "[ iːu ]")
                                FinalLabel(word = "劍", syllable = "gim3", jyutping = "im", ipa = "[ iːm ]")
                                FinalLabel(word = "見", syllable = "gin3", jyutping = "in", ipa = "[ iːn ]")
                                FinalLabel(word = "敬", syllable = "ging3", jyutping = "ing", ipa = "[ eŋ ]")
                                FinalLabel(word = "劫", syllable = "gip3", jyutping = "ip", ipa = "[ iːp̚ ]")
                                FinalLabel(word = "結", syllable = "git3", jyutping = "it", ipa = "[ iːt̚ ]")
                                FinalLabel(word = "極", syllable = "gik6", jyutping = "ik", ipa = "[ ek̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "個", syllable = "go3", jyutping = "o", ipa = "[ ɔː ]")
                                FinalLabel(word = "蓋", syllable = "goi3", jyutping = "oi", ipa = "[ ɔːi ]")
                                FinalLabel(word = "告", syllable = "gou3", jyutping = "ou", ipa = "[ ɔːu ]")
                                FinalLabel(word = "幹", syllable = "gon3", jyutping = "on", ipa = "[ ɔːn ]")
                                FinalLabel(word = "鋼", syllable = "gong3", jyutping = "ong", ipa = "[ ɔːŋ ]")
                                FinalLabel(word = "割", syllable = "got3", jyutping = "ot", ipa = "[ ɔːt̚ ]")
                                FinalLabel(word = "各", syllable = "gok3", jyutping = "ok", ipa = "[ ɔːk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "夫", syllable = "fu1", jyutping = "u", ipa = "[ uː ]")
                                FinalLabel(word = "灰", syllable = "fui1", jyutping = "ui", ipa = "[ uːi ]")
                                FinalLabel(word = "寬", syllable = "fun1", jyutping = "un", ipa = "[ uːn ]")
                                FinalLabel(word = "封", syllable = "fung1", jyutping = "ung", ipa = "[ oːŋ ]")
                                FinalLabel(word = "闊", syllable = "fut3", jyutping = "ut", ipa = "[ uːt̚ ]")
                                FinalLabel(word = "福", syllable = "fuk1", jyutping = "uk", ipa = "[ oːk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "鋸", syllable = "goe3", jyutping = "oe", ipa = "[ œː ]")
                                FinalLabel(word = "姜", syllable = "goeng1", jyutping = "oeng", ipa = "[ œːŋ ]")
                                FinalLabel(word = "*", syllable = "goet4", jyutping = "oet", ipa = "[ œːt̚ ]")
                                FinalLabel(word = "腳", syllable = "goek3", jyutping = "oek", ipa = "[ œːk̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "歲", syllable = "seoi3", jyutping = "eoi", ipa = "[ ɵːi ]")
                                FinalLabel(word = "信", syllable = "seon3", jyutping = "eon", ipa = "[ ɵːn ]")
                                FinalLabel(word = "術", syllable = "seot6", jyutping = "eot", ipa = "[ ɵːt̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "恕", syllable = "syu3", jyutping = "yu", ipa = "[ yː ]")
                                FinalLabel(word = "算", syllable = "syun3", jyutping = "yun", ipa = "[ yːn ]")
                                FinalLabel(word = "雪", syllable = "syut3", jyutping = "yut", ipa = "[ yːt̚ ]")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .background(color = colorScheme.background, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                FinalLabel(word = "唔", syllable = "m4", jyutping = "m", ipa = "[ m̩ ]")
                                FinalLabel(word = "吳", syllable = "ng4", jyutping = "ng", ipa = "[ ŋ̩ ]")
                        }
                }
        }
}

@Composable
private fun FinalLabel(word: String, syllable: String, jyutping: String, ipa: String) {
        SelectionContainer {
                Row(
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Row(
                                modifier = Modifier.weight(0.45f),
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box {
                                        Text(
                                                text = "佔 gaang4",
                                                modifier = Modifier.alpha(0f)
                                        )
                                        Text(text = "$word $syllable")
                                }
                                Speaker(cantonese = word, romanization = syllable)
                        }
                        Text(
                                text = jyutping,
                                modifier = Modifier.weight(0.25f),
                                fontFamily = FontFamily.Monospace
                        )
                        Text(
                                text = ipa,
                                modifier = Modifier.weight(0.3f)
                        )
                }
        }
}
