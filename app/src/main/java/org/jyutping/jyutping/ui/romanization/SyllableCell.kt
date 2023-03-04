package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SyllableCell(syllable: Syllable) {
        SelectionContainer {
                Row {
                        Text(text = syllable.text, Modifier.weight(0.4f))
                        Text(text = syllable.ipa, Modifier.weight(0.3f))
                        Text(text = syllable.jyutping, Modifier.weight(0.3f))
                }
        }
}

class Syllable(val text: String, val ipa: String, val jyutping: String)
