package org.jyutping.jyutping

enum class CharacterStandard {

        Traditional,

        /// Traditional, Hong Kong
        HongKong,

        /// Traditional, Taiwan
        Taiwan,

        Simplified
}

fun CharacterStandard.idValue(): Int = when (this) {
        CharacterStandard.Traditional -> 1
        CharacterStandard.HongKong -> 2
        CharacterStandard.Taiwan -> 3
        CharacterStandard.Simplified -> 4
}

fun CharacterStandard.isSimplified(): Boolean = (this == CharacterStandard.Simplified)
