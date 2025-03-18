package org.jyutping.jyutping.keyboard

/**
 * Numeric keyboard for the Qwerty KeyboardLayout.
 */
enum class NumericLayout(var identifier: Int) {

        /**
         * Normal Numeric Keyboard
         */
        Default(1),

        /**
         * 10-Key KeyPad
         */
        NumberKeyPad(2);
}
