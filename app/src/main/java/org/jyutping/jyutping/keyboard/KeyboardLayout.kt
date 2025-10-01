package org.jyutping.jyutping.keyboard

/** Cantonese Keyboard Layout */
enum class KeyboardLayout(val identifier: Int) {
        /** 26鍵全鍵盤 */
        Qwerty(1),

        /** 26鍵三拼 */
        TripleStroke(2),

        /** 九宮格 */
        TenKey(3);

        /** 26鍵全鍵盤 */
        val isQwerty: Boolean
                get() = (this == Qwerty)

        /** 26鍵三拼 */
        val isTripleStroke: Boolean
                get() = (this == TripleStroke)

        /** 九宮格 */
        val isTenKey: Boolean
                get() = (this == TenKey)

        companion object {
                fun layoutOf(value: Int): KeyboardLayout = KeyboardLayout.entries.find { it.identifier == value } ?: Qwerty
        }
}
