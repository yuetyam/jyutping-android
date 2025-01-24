package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.presets.PresetString

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
) : Comparable<PinyinLexicon> {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PinyinLexicon) return false
                return this.text == other.text && this.input == other.input
        }
        override fun hashCode(): Int {
                return text.hashCode() * 31 + input.hashCode()
        }
        override fun compareTo(other: PinyinLexicon): Int {
                return this.input.length.compareTo(other.input.length).unaryMinus()
        }
        operator fun plus(another: PinyinLexicon): PinyinLexicon {
                val newText: String = this.text + another.text
                val newPinyin: String = this.pinyin + PresetString.SPACE + another.pinyin
                val newInput: String = this.input + another.input
                val newMark: String = this.mark + PresetString.SPACE + another.mark
                val step: Int = 100_0000
                val newOrder: Int = (this.order * step) + (another.order * step)
                return PinyinLexicon(text = newText, pinyin = newPinyin, input = newInput, mark = newMark, order = newOrder)
        }
}
