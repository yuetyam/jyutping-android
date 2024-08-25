package org.jyutping.jyutping.keyboard

enum class InputMethodMode {
        Cantonese,
        ABC;
        fun isCantonese(): Boolean = (this == Cantonese)
        fun isABC(): Boolean = (this == ABC)
}
