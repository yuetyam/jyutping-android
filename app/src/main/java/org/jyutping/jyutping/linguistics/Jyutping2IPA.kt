package org.jyutping.jyutping.linguistics

object Jyutping2IPA {
        fun IPAText(syllable: String): String {
                val isBadFormat: Boolean = syllable.isBlank() || syllable == "?" || syllable == "X"
                if (isBadFormat) return "?"
                val phone = IPAPhone(syllable)
                val tone = IPATone(syllable)
                return "[ $phone $tone ]"
        }
        fun ipa(syllable: String): String {
                val isBadFormat: Boolean = syllable.isBlank() || syllable == "?" || syllable == "X"
                if (isBadFormat) return "?"
                val phone = IPAPhone(syllable)
                val tone = IPATone(syllable)
                return "$phone$tone"
        }

        private fun IPAPhone(syllable: String): String? {
                val phone = syllable.dropLast(1)
                if (phone.isBlank()) return null
                if (phone == "m") return "m̩"
                if (phone == "ng") return "ŋ̩"
                val firstTwo = phone.take(2)
                val dual = dualInitials(firstTwo)
                if (dual != null) {
                        val final = IPAFinal(phone.drop(2))
                        return "$dual$final"
                }
                val initial = IPAInitial(phone.take(1))
                if (initial != null) {
                        val final = IPAFinal(phone.drop(1))
                        return "$initial$final"
                }
                return IPAFinal(phone)
        }
        private fun dualInitials(text: String): String? {
                return when (text) {
                        "ng" -> "ŋ"
                        "gw" -> "kʷ"
                        "kw" -> "kʷʰ"
                        else -> null
                }
        }
        private fun IPAInitial(text: String): String? {
                return when (text) {
                        "b" -> "p"
                        "p" -> "pʰ"
                        "m" -> "m"
                        "f" -> "f"
                        "d" -> "t"
                        "t" -> "tʰ"
                        "n" -> "n"
                        "l" -> "l"
                        "g" -> "k"
                        "k" -> "kʰ"
                        "h" -> "h"
                        "w" -> "w"
                        "z" -> "t͡s"
                        "c" -> "t͡sʰ"
                        "s" -> "s"
                        "j" -> "j"
                        else -> null
                }
        }
        private fun IPAFinal(text: String): String? {
                return when (text) {
                        "aa" -> "aː"
                        "aai" -> "aːi"
                        "aau" -> "aːu"
                        "aam" -> "aːm"
                        "aan" -> "aːn"
                        "aang" -> "aːŋ"
                        "aap" -> "aːp̚"
                        "aat" -> "aːt̚"
                        "aak" -> "aːk̚"
                        "a" -> "ɐ"
                        "ai" -> "ɐi"
                        "au" -> "ɐu"
                        "am" -> "ɐm"
                        "an" -> "ɐn"
                        "ang" -> "ɐŋ"
                        "ap" -> "ɐp̚"
                        "at" -> "ɐt̚"
                        "ak" -> "ɐk̚"
                        "e" -> "ɛː"
                        "ei" -> "ei"
                        "eu" -> "ɛːu"
                        "em" -> "ɛːm"
                        "en" -> "en"
                        "eng" -> "ɛːŋ"
                        "ep" -> "ɛːp̚"
                        "et" -> "ɛːt̚"
                        "ek" -> "ɛːk̚"
                        "i" -> "iː"
                        "iu" -> "iːu"
                        "im" -> "iːm"
                        "in" -> "iːn"
                        "ing" -> "eŋ"
                        "ip" -> "iːp̚"
                        "it" -> "iːt̚"
                        "ik" -> "ek̚"
                        "o" -> "ɔː"
                        "oi" -> "ɔːi"
                        "ou" -> "ou"
                        "on" -> "ɔːn"
                        "ong" -> "ɔːŋ"
                        "ot" -> "ɔːt̚"
                        "ok" -> "ɔːk̚"
                        "u" -> "uː"
                        "ui" -> "uːi"
                        "um" -> "om"
                        "un" -> "uːn"
                        "ung" -> "oŋ"
                        "up" -> "op̚"
                        "ut" -> "uːt̚"
                        "uk" -> "ok̚"
                        "oe" -> "œː"
                        "oeng" -> "œːŋ"
                        "oet" -> "œːt̚"
                        "oek" -> "œːk̚"
                        "eoi" -> "ɵy"
                        "eon" -> "ɵn"
                        "eot" -> "ɵt̚"
                        "yu" -> "yː"
                        "yun" -> "yːn"
                        "yut" -> "yːt̚"
                        else -> null
                }
        }
        private fun IPATone(syllable: String): String? {
                val tone = syllable.lastOrNull()
                return when (tone) {
                        '1' -> "˥"
                        '2' -> "˧˥"
                        '3' -> "˧"
                        '4' -> "˨˩"
                        '5' -> "˩˧"
                        '6' -> "˨"
                        '7' -> "˥"
                        '8' -> "˧"
                        '9' -> "˨"
                        else -> null
                }
        }
}
