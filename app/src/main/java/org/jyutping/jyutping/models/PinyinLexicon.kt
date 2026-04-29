package org.jyutping.jyutping.models

import org.jyutping.jyutping.presets.PresetString

data class PinyinLexicon(

        /** Cantonese Chinese word. */
        val text: String,

        /** Pinyin romanization for word text. */
        val pinyin: String,

        /** User input. */
        val input: String,

        /** Length of the input text. */
        val inputCount: Int = input.length,

        /** Formatted user input for pre-edit display.  */
        val mark: String,

        /** Rank, order. (Smaller is preferred) */
        val number: Int
) : Comparable<PinyinLexicon> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinLexicon) return false
                return (text == other.text) && (input == other.input)
        }
        override fun hashCode(): Int {
                return text.hashCode() * 31 + input.hashCode()
        }
        override fun compareTo(other: PinyinLexicon): Int {
                return inputCount.compareTo(other.inputCount).unaryMinus()
                        .takeIf { it != 0 } ?: number.compareTo(other.number)
        }
        operator fun plus(another: PinyinLexicon): PinyinLexicon {
                val newText: String = this.text + another.text
                val newPinyin: String = this.pinyin + PresetString.SPACE + another.pinyin
                val newInput: String = this.input + another.input
                val newMark: String = this.mark + PresetString.SPACE + another.mark
                val step = 1_000_000
                val newOrder: Int = (this.number * step) + (another.number * step)
                return PinyinLexicon(text = newText, pinyin = newPinyin, input = newInput, mark = newMark, number = newOrder)
        }
}
