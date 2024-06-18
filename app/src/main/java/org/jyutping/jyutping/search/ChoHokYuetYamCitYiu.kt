package org.jyutping.jyutping.search

data class ChoHokYuetYamCitYiu(
        val word: String,
        val pronunciation: String,
        val tone: String,
        val faancit: String,
        val romanization: String,
        val homophones: List<String>
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is ChoHokYuetYamCitYiu) return false
                if (this.word == other.word && this.romanization == other.romanization) return true
                return false
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }
}
