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
                例字,IPA,粵拼
                渣 zaa1,[ aː ],aa
                齋 zaai1,[ aːi ],aai
                嘲 zaau1,[ aːu ],aau
                站 zaam6,[ aːm ],aam
                讚 zaan3,[ aːn ],aan
                爭 zaang1,[ aːŋ ],aang
                雜 zaap6,[ aːp̚ ],aap
                扎 zaat3,[ aːt̚ ],aat
                責 zaak3,[ aːk̚ ],aak
                嘞 la3,[ ɐ ],a
                擠 zai1,[ ɐi ],ai
                周 zau1,[ ɐu ],au
                針 zam1,[ ɐm ],am
                真 zan1,[ ɐn ],an
                增 zang1,[ ɐŋ ],ang
                汁 zap1,[ ɐp̚ ],ap
                質 zat1,[ ɐt̚ ],at
                則 zak1,[ ɐk̚ ],ak
                遮 ze1,[ ɛː ],e
                悲 bei1,[ ei ],ei
                掉 deu6,[ ɛːu ],eu
                𦧷 lem2,[ ɛːm ],em
                ？,[ en ],en
                鄭 zeng6,[ ɛːŋ ],eng
                夾 gep6,[ ɛːp̚ ],ep
                坺 pet6,[ ɛːt̚ ],et
                隻 zek3,[ ɛːk̚ ],ek
                之 zi1,[ iː ],i
                招 ziu1,[ iːu ],iu
                尖 zim1,[ iːm ],im
                煎 zin1,[ iːn ],in
                晶 zing1,[ eŋ ],ing
                接 zip3,[ iːp̚ ],ip
                節 zit3,[ iːt̚ ],it
                即 zik1,[ ek̚ ],ik
                左 zo2,[ ɔː ],o
                栽 zoi1,[ ɔːi ],oi
                租 zou1,[ ou ],ou
                肝 gon1,[ ɔːn ],on
                裝 zong1,[ ɔːŋ ],ong
                喝 hot3,[ ɔːt̚ ],ot
                作 zok3,[ ɔːk̚ ],ok
                夫 fu1,[ uː ],u
                灰 fui1,[ uːi ],ui
                ？,[ om ],um
                寬 fun1,[ uːn ],un
                封 fung1,[ oŋ ],ung
                ？,[ op̚ ],up
                闊 fut3,[ uːt̚ ],ut
                福 fuk1,[ ok̚ ],uk
                靴 hoe1,[ œː ],oe
                香 hoeng1,[ œːŋ ],oeng
                ？,[ œːt̚ ],oet
                腳 goek3,[ œːk̚ ],oek
                追 zeoi1,[ ɵy ],eoi
                津 zeon1,[ ɵn ],eon
                卒 zeot1,[ ɵt̚ ],eot
                書 syu1,[ yː ],yu
                酸 syun1,[ yːn ],yun
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
