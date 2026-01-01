package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.utilities.DatabaseHelper
import org.jyutping.jyutping.utilities.HongKongVariantConverter
import org.jyutping.jyutping.utilities.Simplifier
import org.jyutping.jyutping.utilities.TaiwanVariantConverter

data class Candidate(

        /** Candidate Type. */
        val type: CandidateType = CandidateType.Cantonese,

        /** Candidate text for display. */
        val text: String,

        /** Candidate text (the real one). */
        val lexiconText: String = text,

        /** Jyutping. */
        val romanization: String,

        /** User input. */
        val input: String,

        /** Length of the input text. */
        val inputCount: Int = input.length,

        /** Formatted user input for pre-edit display. */
        val mark: String = input,

        /** Rank. Smaller is preferred.. */
        val order: Int = 0
) : Comparable<Candidate>
{
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Candidate) return false
                if (this.type != other.type) return false
                return if (this.type.isCantonese() && other.type.isCantonese()) {
                        (this.text == other.text) && (this.romanization == other.romanization)
                } else {
                        this.text == other.text
                }
        }
        override fun hashCode(): Int = when (type) {
                CandidateType.Cantonese -> (text.hashCode() * 31 + romanization.hashCode())
                else -> text.hashCode()
        }
        operator fun plus(another: Candidate): Candidate {
                val newText = this.text + another.text
                val newRomanization = this.romanization + PresetString.SPACE + another.romanization
                val newInput = this.input + another.input
                val newMark = this.mark + PresetString.SPACE + another.mark
                val step = 1_000_000
                val newOrder = (this.order + step) + (another.order + step)
                return Candidate(text = newText, romanization = newRomanization, input = newInput, mark = newMark, order = newOrder)
        }
        override fun compareTo(other: Candidate): Int {
                return this.inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: this.order.compareTo(other.order)
        }

        /** isConcatenated. order > 1_000_000 */
        val isCompound: Boolean
                get() = (order > 1_000_000)
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
