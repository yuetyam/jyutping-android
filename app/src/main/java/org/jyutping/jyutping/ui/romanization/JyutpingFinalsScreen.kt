package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun JyutpingFinalsScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                items(entries) {
                        SyllableCell(syllable = it)
                }
        }
}

private fun generateFinalEntries(): List<Syllable> {

val textContent: String = """
例字,國際音標,粵拼
駕 gaa3,[ aː ],aa
界 gaai3,[ aːi ],aai
教 gaau3,[ aːu ],aau
鑑 gaam3,[ aːm ],aam
諫 gaan3,[ aːn ],aan
耕 gaang1,[ aːŋ ],aang
甲 gaap3,[ aːp̚ ],aap
戛 gaat3,[ aːt̚ ],aat
格 gaak3,[ aːk̚ ],aak
㗎 ga3,[ ɐ ],a
計 gai3,[ ɐi ],ai
救 gau3,[ ɐu ],au
禁 gam3,[ ɐm ],am
斤 gan1,[ ɐn ],an
庚 gang1,[ ɐŋ ],ang
急 gap1,[ ɐp̚ ],ap
吉 gat1,[ ɐt̚ ],at
北 bak1,[ ɐk̚ ],ak
嘅 ge3,[ ɛː ],e
記 gei3,[ ei ],ei
掉 deu6,[ ɛːu ],eu
𦧷 lem2,[ ɛːm ],em
鏡 geng3,[ ɛːŋ ],eng
夾 gep6,[ ɛːp̚ ],ep
坺 pet6,[ ɛːt̚ ],et
踢 tek3,[ ɛːk̚ ],ek
意 ji3,[ iː ],i
叫 giu3,[ iːu ],iu
劍 gim3,[ iːm ],im
見 gin3,[ iːn ],in
敬 ging3,[ eŋ ],ing
劫 gip3,[ iːp̚ ],ip
結 git3,[ iːt̚ ],it
極 gik6,[ ek̚ ],ik
個 go3,[ ɔː ],o
菜 coi3,[ ɔːi ],oi
告 gou3,[ ou ],ou
幹 gon3,[ ɔːn ],on
鋼 gong3,[ ɔːŋ ],ong
割 got3,[ ɔːt̚ ],ot
各 gok3,[ ɔːk̚ ],ok
夫 fu1,[ uː ],u
灰 fui1,[ uːi ],ui
寬 fun1,[ uːn ],un
封 fung1,[ oŋ ],ung
闊 fut3,[ uːt̚ ],ut
福 fuk1,[ ok̚ ],uk
鋸 goe3,[ œː ],oe
姜 goeng1,[ œːŋ ],oeng
腳 goek3,[ œːk̚ ],oek
歲 seoi3,[ ɵy ],eoi
信 seon3,[ ɵn ],eon
術 seot6,[ ɵt̚ ],eot
恕 syu3,[ yː ],yu
算 syun3,[ yːn ],yun
雪 syut3,[ yːt̚ ],yut
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

private val entries: List<Syllable> = generateFinalEntries()
