package org.jyutping.jyutping.models

enum class InputMethodMode(val identifier: Int) {

        Cantonese(1),
        ABC(2);

        val isCantonese: Boolean
                get() = (this == Cantonese)

        val isABC: Boolean
                get() = (this == ABC)

        companion object {
                fun modeOf(value: Int): InputMethodMode = entries.find { it.identifier == value } ?: Cantonese
        }
}
