package org.jyutping.jyutping.keyboard

import org.jyutping.jyutping.models.TextCase

enum class KeyboardCase {
        Lowercased,
        Uppercased,
        CapsLocked;

        // TODO: Refactor this code
        fun isLowercased(): Boolean = (this == Lowercased)
        fun isUppercased(): Boolean = (this == Uppercased)
        fun isCapsLocked(): Boolean = (this == CapsLocked)

        val textCase: TextCase
                get() = when (this) {
                        Lowercased -> TextCase.Lowercase
                        Uppercased -> TextCase.Uppercase
                        CapsLocked -> TextCase.Uppercase
                }
}
