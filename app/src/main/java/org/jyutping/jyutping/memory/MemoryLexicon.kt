package org.jyutping.jyutping.memory

import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.models.nineKeyCharCode
import org.jyutping.jyutping.presets.PresetString

data class MemoryLexicon(
        val word: String,
        val romanization: String,
        val frequency: Long,
        val latest: Long,
        val shortcut: Int,
        val spell: Int,
        val nineKeyAnchors: Long,
        val nineKeyCode: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is MemoryLexicon) return false
                return (word == other.word) && (romanization == other.romanization)
        }
        override fun hashCode(): Int {
                return word.hashCode() * 31 + romanization.hashCode()
        }
        companion object {
                fun new(word: String, romanization: String, frequency: Long = 1L, latest: Long? = null): MemoryLexicon {
                        val anchorText = romanization.split(PresetString.SPACE).mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                        val letterText = romanization.filter { it.isLowercaseBasicLatinLetter }
                        val shortcut: Int = anchorText.hashCode()
                        val spell: Int = letterText.hashCode()
                        val nineKeyAnchors: Long = anchorText.nineKeyCharCode ?: 0L
                        val nineKeyCode: Long = letterText.nineKeyCharCode ?: 0L
                        val timestamp: Long = latest ?: System.currentTimeMillis()
                        return MemoryLexicon(word = word, romanization = romanization, frequency = frequency, latest = timestamp, shortcut = shortcut, spell = spell, nineKeyAnchors = nineKeyAnchors, nineKeyCode = nineKeyCode)
                }
        }
}
