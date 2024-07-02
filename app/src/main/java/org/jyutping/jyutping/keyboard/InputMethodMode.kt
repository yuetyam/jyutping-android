package org.jyutping.jyutping.keyboard

enum class InputMethodMode {
        Cantonese,
        ABC
}

fun InputMethodMode.isCantonese(): Boolean = (this == InputMethodMode.Cantonese)
fun InputMethodMode.isABC(): Boolean = (this == InputMethodMode.ABC)
