package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JyutpingTonesScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                items(tones) {
                        ToneCell(tone = it)
                }
        }
}

@Composable
private fun ToneCell(tone: Tone) {
        SelectionContainer {
                Row {
                        Text(text = tone.text, Modifier.weight(0.3f))
                        Text(text = tone.value, Modifier.weight(0.3f))
                        Text(text = tone.name, Modifier.weight(0.2f))
                        Text(text = tone.jyutping, Modifier.weight(0.2f))
                }
        }
}

private class Tone(val text: String, val value: String, val name: String, val jyutping: String)

private val tones: List<Tone> = generateTones()

private fun generateTones(): List<Tone> {
        val textContent: String = """
                例字,調值,聲調,粵拼
                芬 fan1,55/53,陰平,1
                粉 fan2,35,陰上,2
                訓 fan3,33,陰去,3
                焚 fan4,11/21,陽平,4
                奮 fan5,13/23,陽上,5
                份 fan6,22,陽去,6
                忽 fat1,5,高陰入,1
                法 faat3,3,低陰入,3
                罰 fat6,2,陽入,6
        """.trimIndent()
        val textLines = textContent.split("\n")
        var entryList: List<Tone> = listOf()
        textLines.forEach { line ->
                val parts = line.split(",")
                val entry = Tone(text = parts[0], value = parts[1], name = parts[2], jyutping = parts[3])
                entryList = entryList + entry
        }
        return entryList
}
