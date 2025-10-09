package org.jyutping.jyutping.search

data class Pronunciation(
        val romanization: String,
        val homophones: List<String> = listOf(),
        val collocations: List<String> = listOf(),
        val descriptions: List<String> = listOf()
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Pronunciation) return false
                return this.romanization == other.romanization
        }
        override fun hashCode(): Int {
                return romanization.hashCode()
        }
}

data class CantoneseLexicon(
        val text: String,
        val pronunciations: List<Pronunciation> = listOf(),
        val note: String? = null,
        val unihanDefinition: String? = null
)
