package org.jyutping.jyutping.models

import org.jyutping.jyutping.extensions.latinLetterOnly
import org.jyutping.jyutping.extensions.strippedTones
import org.jyutping.jyutping.ninekey.Combo
import org.jyutping.jyutping.ninekey.decimalCombinedCode
import org.jyutping.jyutping.presets.PresetString

data class ExtraEntry(
        /** Cantonese */
        val word: String,

        /** Jyutping */
        val romanization: String,

        /** Length of the letter-only romanization (no tones & no spaces) */
        val complex: Int,

        /** Conjoined code of the letter-only romanization (no tones & no spaces) */
        val spell: Long,

        /** Conjoined keypad code of the letter-only romanization (no tones & no spaces) */
        val nineKey: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ExtraEntry) return false
                return (word == other.word) && (romanization == other.romanization)
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }

        companion object {
                fun search(keys: List<VirtualInputKey>): List<Lexicon> {
                        val spell: Long = keys.conjoinedCode
                        val letterCount: Int = keys.size
                        val input: String = keys.joinToString(separator = PresetString.EMPTY) { it.text }
                        return entries.filter { (it.spell == spell) && (it.complex == letterCount) }
                                .map { Lexicon(text = it.word, romanization = it.romanization, input = input, mark = it.romanization.strippedTones()) }
                }

                fun nineKeySearch(combos: List<Combo>): List<Lexicon> {
                        val code: Long = combos.decimalCombinedCode
                        val letterCount: Int = combos.size
                        return entries.filter { (it.nineKey == code) && (it.complex == letterCount) }
                                .map { Lexicon(text = it.word, romanization = it.romanization, input = it.romanization.latinLetterOnly(), mark = it.romanization.strippedTones()) }
                }

                private val entries: Set<ExtraEntry> = setOf(
                        ExtraEntry(word = "啤", romanization = "bi1", complex = 2, spell = 2128L, nineKey = 24L),
                        ExtraEntry(word = "啤女", romanization = "bi4 neoi2", complex = 6, spell = 212833243428L, nineKey = 246364L),
                        ExtraEntry(word = "啤仔", romanization = "bi1 zai2", complex = 5, spell = 2128452028L, nineKey = 24924L),
                        ExtraEntry(word = "啤啤", romanization = "bi4 bi1", complex = 4, spell = 21282128L, nineKey = 2424L),
                        ExtraEntry(word = "啤啤車", romanization = "bi4 bi1 ce1", complex = 6, spell = 212821282224L, nineKey = 242423L),
                        ExtraEntry(word = "啤啤牀", romanization = "bi4 bi1 cong4", complex = 8, spell = 2128212822343326L, nineKey = 24242664L),
                        ExtraEntry(word = "啤啤女", romanization = "bi4 bi1 neoi2", complex = 8, spell = 2128212833243428L, nineKey = 24246364L),
                        ExtraEntry(word = "啤啤衫", romanization = "bi4 bi1 saam1", complex = 8, spell = 2128212838202032L, nineKey = 24247226L),
                        ExtraEntry(word = "啤啤仔", romanization = "bi4 bi1 zai2", complex = 7, spell = 21282128452028L, nineKey = 2424924L),
                        ExtraEntry(word = "生啤啤", romanization = "saang1 bi4 bi1", complex = 9, spell = 382020332621282128L, nineKey = 722642424L),
                        ExtraEntry(word = "欸", romanization = "e6", complex = 1, spell = 24L, nineKey = 3L),
                        ExtraEntry(word = "誒", romanization = "e6", complex = 1, spell = 24L, nineKey = 3L),
                        ExtraEntry(word = "欸", romanization = "ei6", complex = 2, spell = 2428L, nineKey = 34L),
                        ExtraEntry(word = "誒", romanization = "ei6", complex = 2, spell = 2428L, nineKey = 34L),
                        ExtraEntry(word = "䊦", romanization = "et3", complex = 2, spell = 2439L, nineKey = 38L),
                        ExtraEntry(word = "籺", romanization = "et3", complex = 2, spell = 2439L, nineKey = 38L),
                        ExtraEntry(word = "覅", romanization = "fiu3", complex = 3, spell = 252840L, nineKey = 348L),
                        ExtraEntry(word = "𡠍", romanization = "fiu3", complex = 3, spell = 252840L, nineKey = 348L),
                        ExtraEntry(word = "𧟰", romanization = "fiu3", complex = 3, spell = 252840L, nineKey = 348L),
                        ExtraEntry(word = "𠺪", romanization = "he3", complex = 2, spell = 2724L, nineKey = 43L),
                        ExtraEntry(word = "嗗", romanization = "gut6", complex = 3, spell = 264039L, nineKey = 488L),
                        ExtraEntry(word = "摑", romanization = "gwaak3", complex = 5, spell = 2642202030L, nineKey = 49225L),
                        ExtraEntry(word = "嚕", romanization = "lu1", complex = 2, spell = 3140L, nineKey = 58L),
                        ExtraEntry(word = "𠁣", romanization = "ngi1", complex = 3, spell = 332628L, nineKey = 644L),
                        ExtraEntry(word = "𠃛", romanization = "nget1", complex = 4, spell = 33262439L, nineKey = 6438L),
                        ExtraEntry(word = "𠸊", romanization = "tap1", complex = 3, spell = 392035L, nineKey = 827L),
                        ExtraEntry(word = "扤", romanization = "at1", complex = 2, spell = 2039L, nineKey = 28L),
                        ExtraEntry(word = "扤實", romanization = "at1 sat6", complex = 5, spell = 2039382039L, nineKey = 28728L),
                        ExtraEntry(word = "扤死貓", romanization = "at1 sei2 maau1", complex = 9, spell = 203938242832202040L, nineKey = 287346228L),
                        ExtraEntry(word = "嗒", romanization = "dep1", complex = 3, spell = 232435L, nineKey = 337L),
                        ExtraEntry(word = "嗒嘢", romanization = "dep1 je5", complex = 5, spell = 2324352924L, nineKey = 33753L),
                        ExtraEntry(word = "嗒糖", romanization = "dep1 tong4", complex = 7, spell = 23243539343326L, nineKey = 3378664L),
                        ExtraEntry(word = "嗒落有味", romanization = "dep1 lok6 jau5 mei6", complex = 12, spell = 6338101551689960828L, nineKey = 337565528634L),
                        ExtraEntry(word = "嘰咭", romanization = "gi1 gat6", complex = 5, spell = 2628262039L, nineKey = 44428L),
                        ExtraEntry(word = "嘰嘰咭咭", romanization = "gi1 gi1 gat6 gat6", complex = 10, spell = 7835884188329710423L, nineKey = 4444428428L),
                        ExtraEntry(word = "嘰哩咕嚕", romanization = "gi1 li1 gu1 lu1", complex = 8, spell = 2628312826403140L, nineKey = 44544858L),
                        ExtraEntry(word = "喲", romanization = "jo1", complex = 2, spell = 2934L, nineKey = 56L),
                        ExtraEntry(word = "哎喲", romanization = "aai1 jo1", complex = 5, spell = 2020282934L, nineKey = 22456L),
                        ExtraEntry(word = "哎喲", romanization = "ai1 jo1", complex = 4, spell = 20282934L, nineKey = 2456L),
                        ExtraEntry(word = "𠸉", romanization = "kak1", complex = 3, spell = 302030L, nineKey = 525L),
                        ExtraEntry(word = "嘞𠸉", romanization = "lak1 kak1", complex = 6, spell = 312030302030L, nineKey = 525525L),
                        ExtraEntry(word = "嘞嘞𠸉𠸉", romanization = "lak1 lak1 kak1 kak1", complex = 12, spell = 3636023504964717390L, nineKey = 525525525525L),
                        ExtraEntry(word = "哩", romanization = "li1", complex = 2, spell = 3128L, nineKey = 54L),
                        ExtraEntry(word = "哩個", romanization = "li1 go3", complex = 4, spell = 31282634L, nineKey = 5446L),
                        ExtraEntry(word = "花哩綠", romanization = "faa1 li1 luk1", complex = 8, spell = 2520203128314030L, nineKey = 32254585L),
                        ExtraEntry(word = "𡃈", romanization = "kwak1", complex = 4, spell = 30422030L, nineKey = 5925L),
                        ExtraEntry(word = "𡃈", romanization = "kwaak1", complex = 5, spell = 3042202030L, nineKey = 59225L),
                        ExtraEntry(word = "𡁸", romanization = "kwaak1", complex = 5, spell = 3042202030L, nineKey = 59225L),
                        ExtraEntry(word = "𠽤嚦𡃈嘞", romanization = "kik1 lik1 kwak1 lak1", complex = 13, spell = 7661401431458112094L, nineKey = 5455455925525L),
                        ExtraEntry(word = "𠽤嚦𡃈嘞", romanization = "kik1 lik1 kwaak1 laak1", complex = 15, spell = 4686176365263980782L, nineKey = 545545592255225L),
                        ExtraEntry(word = "𠵇", romanization = "keu4", complex = 3, spell = 302440L, nineKey = 538L),
                        ExtraEntry(word = "𠺫", romanization = "leu1", complex = 3, spell = 312440L, nineKey = 538L),
                        ExtraEntry(word = "𠵇𠺫", romanization = "keu4 leu1", complex = 6, spell = 302440312440L, nineKey = 538538L),
                        ExtraEntry(word = "𠮩𠹌", romanization = "liu1 lang1", complex = 7, spell = 31284031203326L, nineKey = 5485264L),
                        ExtraEntry(word = "啤", romanization = "pe1", complex = 2, spell = 3524L, nineKey = 73L),
                        ExtraEntry(word = "啤牌", romanization = "pe1 paai2", complex = 6, spell = 352435202028L, nineKey = 737224L),
                        ExtraEntry(word = "𢚖", romanization = "ti4", complex = 2, spell = 3928L, nineKey = 84L),
                        ExtraEntry(word = "發𢚖騰", romanization = "faat3 ti4 tang4", complex = 10, spell = 6755295319129651710L, nineKey = 3228848264L),
                        ExtraEntry(word = "啫", romanization = "zoe1", complex = 3, spell = 453424L, nineKey = 963L),
                        ExtraEntry(word = "啫啫", romanization = "zoe1 zoe1", complex = 6, spell = 453424453424L, nineKey = 963963L),
                        ExtraEntry(word = "啫啫煲", romanization = "zoe1 zoe1 bou1", complex = 9, spell = 453424453424213440L, nineKey = 963963268L),
                )
        }
}
