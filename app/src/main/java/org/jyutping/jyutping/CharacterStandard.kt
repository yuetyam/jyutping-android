package org.jyutping.jyutping

/**
 * Character Set. 字符集／字形標準
 */
enum class CharacterStandard(val identifier: Int) {

        Traditional(1),

        /// Traditional, Hong Kong
        HongKong(2),

        /// Traditional, Taiwan
        Taiwan(3),

        Simplified(4);

        fun isSimplified(): Boolean = (this == Simplified)
        // fun isNotSimplified(): Boolean = (this != Simplified)

        companion object {
                fun standardOf(value: Int): CharacterStandard = CharacterStandard.entries.find { it.identifier == value } ?: Traditional
        }
}
