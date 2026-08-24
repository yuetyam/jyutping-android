package org.jyutping.jyutping.memory

import org.jyutping.jyutping.extensions.isBasicDigit
import org.jyutping.jyutping.extensions.isLowercaseBasicLatinLetter
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.models.decimalOverflowed
import org.jyutping.jyutping.models.keypadCode
import org.jyutping.jyutping.models.serialCode
import org.jyutping.jyutping.presets.PresetCharacter
import org.jyutping.jyutping.presets.PresetString

data class MemoryLexicon(
        val word: String,
        val romanization: String,
        val frequency: Long,
        val latest: Long,
        val charCount: Int,
        val letterCount: Int,
        val complexity: Long,
        val anchors: Long,
        val spell: Long,
        val nineKeyAnchors: Long,
        val nineKeySpell: Long
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
                        val phones = romanization.filter { it.isBasicDigit.negative }.split(PresetCharacter.SPACE)
                        val complexity: Long = phones.map { it.length }.decimalOverflowed()
                        val anchorText: String = phones.mapNotNull { it.firstOrNull() }.joinToString(separator = PresetString.EMPTY)
                        val letters: String = romanization.filter { it.isLowercaseBasicLatinLetter }
                        val timestamp: Long = latest ?: System.currentTimeMillis()
                        return MemoryLexicon(
                                word = word,
                                romanization = romanization,
                                frequency = frequency,
                                latest = timestamp,
                                charCount = word.length,
                                letterCount = letters.length,
                                complexity = complexity,
                                anchors = anchorText.serialCode,
                                spell = letters.serialCode,
                                nineKeyAnchors = anchorText.keypadCode,
                                nineKeySpell = letters.keypadCode
                        )
                }
        }
}
