package org.jyutping.preparing

object PresetCharacter {
        const val SPACE: Char = ' '

        /** Quote mark; delimiter; apostrophe */
        const val SEPARATOR: Char = '\''

        val reverseLookupTriggers: Set<Char> = setOf('r', 'v', 'x', 'q')
        val cantoneseTones: Set<Char> = setOf('1', '2', '3', '4', '5', '6')
}
