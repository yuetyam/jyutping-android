package org.jyutping.preparing

object PresetCharacter {
        const val SPACE: Char = ' '

        /** Quote mark; delimiter; apostrophe */
        const val SEPARATOR: Char = '\''

        val reverseLookupTriggers: HashSet<Char> = hashSetOf('r', 'v', 'x', 'q')
        val cantoneseTones: HashSet<Char> = hashSetOf('1', '2', '3', '4', '5', '6')
}
