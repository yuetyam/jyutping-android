package org.jyutping.jyutping.utilities

data class PinyinLexicon(

        /// Cantonese Chinese word.
        val text: String,

        /// Pinyin romanization for word text.
        val pinyin: String,

        /// User input.
        val input: String,

        /// Formatted user input for pre-edit display
        val mark: String,

        /// Rank, smaller is preferred.
        val order: Int
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinLexicon) return false
                return this.text == other.text && this.input == other.input
        }
        override fun hashCode(): Int {
                return text.hashCode() * 31 + input.hashCode()
        }
}
