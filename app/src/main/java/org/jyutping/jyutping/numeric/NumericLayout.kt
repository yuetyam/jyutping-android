package org.jyutping.jyutping.numeric

/**
 * Numeric keyboard for the Qwerty KeyboardLayout.
 */
enum class NumericLayout(var identifier: Int) {

        /** Normal numeric keyboard */
        Default(1),

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other keys */
        Dedicated(2);

        /** 10-key keypad-style digit keyboard with a symbol sidebar and some other keys */
        val isDedicated: Boolean
                get() = (this == Dedicated)
}
