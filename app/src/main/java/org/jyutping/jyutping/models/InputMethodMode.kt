package org.jyutping.jyutping.models

enum class InputMethodMode {
        Cantonese,
        ABC;

        val isCantonese: Boolean
                get() = (this == Cantonese)

        val isABC: Boolean
                get() = (this == ABC)
}
