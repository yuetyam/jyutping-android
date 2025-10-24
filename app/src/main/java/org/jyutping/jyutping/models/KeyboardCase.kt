package org.jyutping.jyutping.models

enum class KeyboardCase {
        Lowercased,
        Uppercased,
        CapsLocked;

        val isLowercased: Boolean
                get() = (this == Lowercased)

        val isUppercased: Boolean
                get() = (this == Uppercased)

        val isCapsLocked: Boolean
                get() = (this == CapsLocked)

        /** Is not lowercased */
        val isCapitalized: Boolean
                get() = (this != Lowercased)

        val textCase: TextCase
                get() = when (this) {
                        Lowercased -> TextCase.Lowercase
                        Uppercased -> TextCase.Uppercase
                        CapsLocked -> TextCase.Uppercase
                }
}
