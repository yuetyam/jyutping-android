package org.jyutping.jyutping.search

data class YingWaaFanWan(
        val word: String,
        val romanization: String,
        val pronunciation: String,
        val pronunciationMark: String?,
        val interpretation: String?,
        val homophones: List<String>
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is YingWaaFanWan) return false
                if (this.word == other.word && this.romanization == other.romanization) return true
                return false
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }

        companion object {
                fun process(entries: List<YingWaaFanWan>): List<YingWaaFanWan> {
                        if (entries.isEmpty()) return entries
                        val romanizations = entries.map { it.romanization }.distinct()
                        val hasDuplicates: Boolean = romanizations.size != entries.size
                        if (!hasDuplicates) return entries
                        val processedEntries: List<YingWaaFanWan> = romanizations.mapNotNull { syllable ->
                                val filtered = entries.filter { it.romanization == syllable }
                                when (filtered.size) {
                                        0 -> null
                                        1 -> filtered.first()
                                        else -> {
                                                val example = filtered.first()
                                                val pronunciationMark: String = filtered.mapNotNull { it.pronunciationMark }.distinct().joinToString(", ")
                                                val interpretation: String = filtered.mapNotNull { it.interpretation }.distinct().joinToString(" ")
                                                YingWaaFanWan(
                                                        word = example.word,
                                                        romanization = syllable,
                                                        pronunciation = example.pronunciation,
                                                        pronunciationMark = pronunciationMark,
                                                        interpretation = interpretation,
                                                        homophones = example.homophones
                                                )
                                        }
                                }
                        }
                        return processedEntries
                }

                fun processPronunciationMark(mark: String): String? {
                        return when (mark) {
                                "X" -> null
                                "ALMOST_ALWAYS_PRO" -> "Almost Always Pronounced"
                                "ALSO_PRO" -> "Also Pronounced"
                                "CORRECTLY_READ" -> "Correctly Pronounced"
                                "FAN_WAN_PRO" -> "Read in Fan Wan"
                                "FAN_WAN_ERRONEOUSLY_READ" -> "Erroneously read in Fan Wan"
                                "FREQ_PRO" -> "Frequently Pronounced"
                                "MORE_FREQ_HEARD" -> "More frequently heard than original"
                                "OFTEN_PRO" -> "Often Pronounced"
                                "OFTEN_READ_CANTON" -> "Often read in Canton"
                                "PROPER_SOUND" -> "Proper Sound"
                                "SELDOM_HEARD" -> "Seldom Heard"
                                "SOMETIMES_READ" -> "Sometimes Read"
                                "USUALLY_PRO" -> "Usually Pronounced"
                                "VULGAR" -> "Vulgar"
                                else -> mark
                        }
                }
        }
}
