package org.jyutping.preparing

data class LexiconEntry(
        val word: String,
        val romanization: String,
        val anchors: Long,
        val ping: Long,
        val tenKeyAnchors: Long,
        val tenKeyCode: Long,
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is LexiconEntry) return false
                return this.word == other.word && this.romanization == other.romanization
        }
        override fun hashCode(): Int = word.hashCode() * 31 + romanization.hashCode()
}
