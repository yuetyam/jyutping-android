package org.jyutping.jyutping.presets

object PresetCharacter {
        const val SPACE: Char = ' '
        const val SEPARATOR: Char = '\''

        val reverseLookupTriggers: HashSet<Char> = hashSetOf('r', 'v', 'x', 'q')
        val cantoneseTones: HashSet<Char> = hashSetOf('1', '2', '3', '4', '5', '6')
}
