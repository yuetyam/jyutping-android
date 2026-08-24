package org.jyutping.jyutping.keyboard

/** Letter input key style */
enum class InputKeyStyle(val identifier: Int) {

        /** Letters only */
        Clear(1),

        /** Letters with extra digits (number row) */
        Numbers(2),

        /** Letters with extra digits (number row) and symbols */
        NumbersAndSymbols(3);

        val isClear: Boolean
                get() = (this == Clear)

        val isNumbers: Boolean
                get() = (this == Numbers)

        val isNumbersAndSymbols: Boolean
                get() = (this == NumbersAndSymbols)

        companion object {
                fun styleOf(value: Int): InputKeyStyle = entries.find { it.identifier == value } ?: Clear
        }
}
