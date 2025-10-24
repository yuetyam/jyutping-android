package org.jyutping.jyutping.presets

object PresetCharacter {
        /** U+0020 */
        const val SPACE: Char = ' '

        /** U+0027. Separator; Delimiter; Quote */
        const val APOSTROPHE: Char = '\''

        /** r, v, x, q */
        val reverseLookupTriggers: HashSet<Char> = hashSetOf('r', 'v', 'x', 'q')
}
