package org.jyutping.jyutping.search

data class YingWaaFanWan(
        val word: String,
        val romanization: String,
        val pronunciation: String,
        val note: String?,
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
                        val shouldReturnEarly: Boolean = romanizations.size == entries.size
                        if (shouldReturnEarly) return entries
                        val processedEntries: List<YingWaaFanWan> = romanizations.mapNotNull { syllable ->
                                val filtered = entries.filter { it.romanization == syllable }
                                when (filtered.size) {
                                        0 -> null
                                        1 -> filtered.first()
                                        else -> {
                                                val sample = filtered.first()
                                                val noteText: String = filtered.mapNotNull { it.note }.distinct().joinToString(", ")
                                                val interpretation: String = filtered.mapNotNull { it.interpretation }.distinct().joinToString(" ")
                                                YingWaaFanWan(
                                                        word = sample.word,
                                                        romanization = syllable,
                                                        pronunciation = sample.pronunciation,
                                                        note = noteText,
                                                        interpretation = interpretation,
                                                        homophones = sample.homophones
                                                )
                                        }
                                }
                        }
                        return processedEntries
                }
        }
}
