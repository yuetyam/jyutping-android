package org.jyutping.jyutping

enum class CharacterStandard {

        Traditional,

        /// Traditional, Hong Kong
        HongKong,

        /// Traditional, Taiwan
        Taiwan,

        Simplified;

        fun isSimplified(): Boolean = (this == Simplified)
        fun isNotSimplified(): Boolean = (this != Simplified)

        fun identifier(): Int = when (this) {
                Traditional -> 1
                HongKong -> 2
                Taiwan -> 3
                Simplified -> 4
        }
        companion object {
                fun standardOf(value: Int): CharacterStandard {
                        return when (value) {
                                Traditional.identifier() -> Traditional
                                HongKong.identifier() -> HongKong
                                Taiwan.identifier() -> Taiwan
                                Simplified.identifier() -> Simplified
                                else -> Traditional
                        }
                }
        }
}
