package org.jyutping.jyutping.models

/** Cantonese Keyboard Layout */
enum class KeyboardLayout(val identifier: Int) {
        /** 26 鍵全鍵盤 */
        Qwerty(1),

        /** 26 鍵三拼 */
        TripleStroke(2),

        /** 九宮格（9 鍵） */
        NineKey(3),

        /** 14 鍵 */
        FourteenKey(4),

        /** 18 鍵 */
        EighteenKey(5);

        /** 26 鍵全鍵盤 */
        val isQwerty: Boolean
                get() = (this == Qwerty)

        /** 26 鍵三拼 */
        val isTripleStroke: Boolean
                get() = (this == TripleStroke)

        /** 九宮格（9 鍵） */
        val isNineKey: Boolean
                get() = (this == NineKey)

        /** 14 鍵 */
        val isFourteenKey: Boolean
                get() = (this == FourteenKey)

        /** 18 鍵 */
        val isEighteenKey: Boolean
                get() = (this == EighteenKey)

        companion object {
                fun layoutOf(value: Int): KeyboardLayout = entries.find { it.identifier == value } ?: Qwerty
        }
}
