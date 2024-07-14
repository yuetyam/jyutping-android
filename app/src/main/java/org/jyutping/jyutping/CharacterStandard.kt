package org.jyutping.jyutping

enum class CharacterStandard {

        Traditional,

        /// Traditional, Hong Kong
        HongKong,

        /// Traditional, Taiwan
        Taiwan,

        Simplified;

        fun idValue(): Int = when (this) {
                Traditional -> 1
                HongKong -> 2
                Taiwan -> 3
                Simplified -> 4
        }

        fun isSimplified(): Boolean = (this == Simplified)
        fun isNotSimplified(): Boolean = (this != Simplified)
}
