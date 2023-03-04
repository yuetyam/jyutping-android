package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun JyutpingInitialsScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
                items(entries) {
                        SyllableCell(syllable = it)
                }
        }
}

private fun generateInitialEntries(): List<Syllable> {
        val textContent: String = """
                例字,IPA,粵拼
                巴 baa1,[ p ],b
                趴 paa1,[ pʰ ],p
                媽 maa1,[ m ],m
                花 faa1,[ f ],f
                打 daa2,[ t ],d
                他 taa1,[ tʰ ],t
                拿 naa4,[ n ],n
                啦 laa1,[ l ],l
                家 gaa1,[ k ],g
                卡 kaa1,[ kʰ ],k
                牙 ngaa4,[ ŋ ],ng
                蝦 haa1,[ h ],h
                瓜 gwaa1,[ kʷ ],gw
                夸 kwaa1,[ kʷʰ ],kw
                娃 waa1,[ w ],w
                渣 zaa1,t͡s~t͡ʃ,z
                叉 caa1,t͡sʰ~t͡ʃʰ,c
                沙 saa1,s~ʃ,s
                也 jaa5,[ j ],j
        """.trimIndent()
        val textLines = textContent.split("\n")
        var entryList: List<Syllable> = listOf()
        textLines.forEach { line ->
                val parts = line.split(",")
                val entry = Syllable(text = parts[0], ipa = parts[1], jyutping = parts[2])
                entryList = entryList + entry
        }
        return entryList
}
private val entries: List<Syllable> = generateInitialEntries()
