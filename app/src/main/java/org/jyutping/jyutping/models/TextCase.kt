package org.jyutping.jyutping.models

enum class TextCase {
        Lowercase,
        Uppercase;
}

fun String.textCased(case: TextCase? = null): String = when (case) {
        TextCase.Uppercase -> this.uppercase()
        TextCase.Lowercase -> this.lowercase()
        else -> this
}
