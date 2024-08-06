package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.HongKongVariantConverter
import org.jyutping.jyutping.utilities.Simplifier
import org.jyutping.jyutping.utilities.TaiwanVariantConverter

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
        operator fun plus(another: Candidate): Candidate {
                val newText = this.text + another.text
                val newRomanization = this.romanization + String.space + another.romanization
                val newInput = this.input + another.input
                val newMark = this.mark + String.space + another.mark
                return Candidate(text = newText, romanization = newRomanization, input = newInput, mark = newMark)
        }
}

fun Candidate.transformed(standard: CharacterStandard, db: DatabaseHelper): Candidate {
        if (this.type.isNotCantonese()) return this
        return when (standard) {
                CharacterStandard.Traditional -> this
                CharacterStandard.HongKong -> Candidate(text = HongKongVariantConverter.convert(this.text), lexiconText = this.lexiconText, romanization = this.romanization, input = this.input, mark = this.mark)
                CharacterStandard.Taiwan -> Candidate(text = TaiwanVariantConverter.convert(this.text), lexiconText = this.lexiconText, romanization = this.romanization, input = this.input, mark = this.mark)
                CharacterStandard.Simplified -> Candidate(text = Simplifier.convert(this.text, db), lexiconText = this.lexiconText, romanization = this.romanization, input = this.input, mark = this.mark)
        }
}
