package org.jyutping.jyutping.keyboard

data class Candidate(
        val type: CandidateType = CandidateType.Cantonese,
        val text: String,
        val lexiconText: String = text,
        val romanization: String,
        val input: String,
        val mark: String = input,
        val order: Int = 0
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Candidate) return false
                if (this.type != other.type) return false
                if (this.type.isCantonese() && other.type.isCantonese()) {
                        return (this.text == other.text) && (this.romanization == other.romanization)
                } else {
                        return this.text == other.text
                }
        }
        override fun hashCode(): Int = when (type) {
                CandidateType.Cantonese -> (text.hashCode() * 31 + romanization.hashCode())
                else -> text.hashCode()
        }
}
