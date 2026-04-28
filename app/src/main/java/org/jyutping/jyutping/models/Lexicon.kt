package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

/** Internal candidate lexicon */
data class Lexicon(
        /** Lexicon type */
        val type: LexiconType = LexiconType.Cantonese,

        /** Lexicon text */
        val text: String,

        /** Jyutping */
        val romanization: String,

        /** User input */
        val input: String,

        /** Character count of the `input` */
        val inputCount: Int = input.length,

        /** Formatted user input for pre-edit display */
        val mark: String = input,

        /** Rank, order. (Smaller is preferred) */
        val number: Int = 0,

        /** Extra text to attach to this lexicon */
        val attached: String? = null
) : Comparable<Lexicon> {

        val isCantonese: Boolean
                get() = type.isCantonese

        val isNotCantonese: Boolean
                get() = type.isNotCantonese

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Lexicon) return false
                if (type != other.type) return false
                return if (this.isCantonese && other.isCantonese) {
                        (text == other.text) && (romanization == other.romanization)
                } else {
                        text == other.text
                }
        }
        override fun hashCode(): Int = when (type) {
                LexiconType.Cantonese -> (text.hashCode() * 31 + romanization.hashCode())
                else -> text.hashCode()
        }
        override fun compareTo(other: Lexicon): Int {
                return inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: number.compareTo(other.number)
        }
        operator fun plus(another: Lexicon): Lexicon? {
                if (isNotCantonese || another.isNotCantonese) return null
                val newText = text + another.text
                val newRomanization = romanization + PresetString.SPACE + another.romanization
                val newInput = input + another.input
                val newMark = mark + PresetString.SPACE + another.mark
                val step: Int = 1_000_000
                val newNumber: Int = (number + step) + (another.number + step)
                return Lexicon(text = newText, romanization = newRomanization, input = newInput, mark = newMark, number = newNumber)
        }

        /** isConcatenated */
        val isCompound: Boolean
                get() = (number > 1_000_000)

        val isInputMemory: Boolean
                get() = (number < 0)

        val isIdealInputMemory: Boolean
                get() = (number == -1)

        val isNotIdealInputMemory: Boolean
                get() = (number == -2)

        val isEmojiOrSymbol: Boolean
                get() = when (type) {
                        LexiconType.Emoji,
                        LexiconType.Symbol -> true
                        else -> false
                }

        fun replacedInput(newInput: String): Lexicon = Lexicon(type = type, text = text, romanization = romanization, input = newInput, mark = mark, number = number, attached = attached)

        companion object {
                fun concatenate(lexicons: List<Lexicon>): Lexicon? {
                        val isNotAllCantonese: Boolean = lexicons.any { it.isNotCantonese }
                        if (isNotAllCantonese) return null
                        if (lexicons.size == 1) { return lexicons.first() }
                        val newText: String = lexicons.joinToString(separator = PresetString.EMPTY) { it.text }
                        val newRomanization: String = lexicons.joinToString(separator = PresetString.SPACE) { it.romanization }
                        val newInput: String = lexicons.joinToString(separator = PresetString.EMPTY) { it.input }
                        val newMark: String = lexicons.joinToString(separator = PresetString.SPACE) { it.mark }
                        val step: Int = 1_000_000
                        val newNumber: Int = lexicons.map { it.number }.fold(0) { acc, i -> acc + i + step }
                        return Lexicon(text = newText, romanization = newRomanization, input = newInput, mark = newMark, number = newNumber)
                }
        }
}
