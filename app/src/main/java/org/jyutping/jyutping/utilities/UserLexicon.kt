package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.extensions.empty
import org.jyutping.jyutping.extensions.space
import org.jyutping.jyutping.keyboard.Candidate

data class UserLexicon(
        val id: Int,
        val word: String,
        val romanization: String,
        val shortcut: Int,
        val ping: Int,
        val frequency: Int,
        val latest: Long
) {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is UserLexicon) return false
                return this.id == other.id
        }
        override fun hashCode(): Int {
                return id
        }
        companion object {
                fun convert(candidate: Candidate): UserLexicon {
                        val idValue: Int = (candidate.lexiconText + candidate.romanization).hashCode()
                        val shortcutValue: Int = candidate.romanization
                                .split(String.space)
                                .mapNotNull { it.firstOrNull() }
                                .joinToString(separator = String.empty)
                                .hashCode()
                        val pingValue: Int = candidate.romanization.filter { it.isLetter() }.hashCode()
                        val time: Long = System.currentTimeMillis()
                        return UserLexicon(
                                id = idValue,
                                word = candidate.lexiconText,
                                romanization = candidate.romanization,
                                shortcut = shortcutValue,
                                ping = pingValue,
                                frequency = 1,
                                latest = time
                        )
                }
                fun join(candidates: List<Candidate>): UserLexicon {
                        val newText: String = candidates.joinToString(separator = String.empty) { it.lexiconText }
                        val newRomanization: String = candidates.joinToString(separator = String.space) { it.romanization }
                        val idValue: Int = (newText + newRomanization).hashCode()
                        val shortcutValue: Int = newRomanization
                                .split(String.space)
                                .mapNotNull { it.firstOrNull() }
                                .joinToString(separator = String.empty)
                                .hashCode()
                        val pingValue: Int = newRomanization.filter { it.isLetter() }.hashCode()
                        val time: Long = System.currentTimeMillis()
                        return UserLexicon(
                                id = idValue,
                                word = newText,
                                romanization = newRomanization,
                                shortcut = shortcutValue,
                                ping = pingValue,
                                frequency = 1,
                                latest = time
                        )
                }
        }
}
