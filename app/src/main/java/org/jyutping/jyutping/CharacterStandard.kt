package org.jyutping.jyutping

enum class CharacterStandard(val identifier: Int) {

        Traditional(1),

        /// Traditional, Hong Kong
        HongKong(2),

        /// Traditional, Taiwan
        Taiwan(3),

        Simplified(4);

        fun isSimplified(): Boolean = (this == Simplified)
        fun isNotSimplified(): Boolean = (this != Simplified)

        companion object {
                fun standardOf(value: Int): CharacterStandard = when (value) {
                        Traditional.identifier -> Traditional
                        HongKong.identifier -> HongKong
                        Taiwan.identifier -> Taiwan
                        Simplified.identifier -> Simplified
                        else -> Traditional
                }
        }
}
