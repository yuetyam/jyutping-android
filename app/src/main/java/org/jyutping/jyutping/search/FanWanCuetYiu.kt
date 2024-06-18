package org.jyutping.jyutping.search

data class FanWanCuetYiu(
        val word: String,
        val pronunciation: String,
        val romanization: String,
        val homophones: List<String>,
        val interpretation: String
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is FanWanCuetYiu) return false
                if (this.word == other.word && this.romanization == other.romanization) return true
                return false
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }
}
