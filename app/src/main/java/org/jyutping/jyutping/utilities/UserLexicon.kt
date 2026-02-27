package org.jyutping.jyutping.utilities

import org.jyutping.jyutping.models.Lexicon
import org.jyutping.jyutping.presets.PresetString

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
                fun convert(lexicon: Lexicon): UserLexicon {
                        val idValue: Int = (lexicon.text + lexicon.romanization).hashCode()
                        val shortcutValue: Int = lexicon.romanization
                                .split(PresetString.SPACE)
                                .mapNotNull { it.firstOrNull() }
                                .joinToString(separator = PresetString.EMPTY)
                                .hashCode()
                        val pingValue: Int = lexicon.romanization.filter { it.isLetter() }.hashCode()
                        val time: Long = System.currentTimeMillis()
                        return UserLexicon(
                                id = idValue,
                                word = lexicon.text,
                                romanization = lexicon.romanization,
                                shortcut = shortcutValue,
                                ping = pingValue,
                                frequency = 1,
                                latest = time
                        )
                }
                fun join(lexicons: List<Lexicon>): UserLexicon {
                        val newText: String = lexicons.joinToString(separator = PresetString.EMPTY) { it.text }
                        val newRomanization: String = lexicons.joinToString(separator = PresetString.SPACE) { it.romanization }
                        val idValue: Int = (newText + newRomanization).hashCode()
                        val shortcutValue: Int = newRomanization
                                .split(PresetString.SPACE)
                                .mapNotNull { it.firstOrNull() }
                                .joinToString(separator = PresetString.EMPTY)
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
