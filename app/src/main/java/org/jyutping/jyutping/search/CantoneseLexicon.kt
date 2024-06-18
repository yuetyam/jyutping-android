package org.jyutping.jyutping.search

data class Pronunciation(
        val romanization: String,
        val homophones: List<String> = listOf(),
        val interpretation: String? = null,
        val collocations: List<String> = listOf()
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ChoHokYuetYamCitYiu) return false
                if (this.romanization == other.romanization) return true
                return false
        }
        override fun hashCode(): Int {
                return romanization.hashCode()
        }
}

data class CantoneseLexicon(
        val text: String,
        val pronunciations: List<Pronunciation> = listOf(),
        val note: String? = null
)
