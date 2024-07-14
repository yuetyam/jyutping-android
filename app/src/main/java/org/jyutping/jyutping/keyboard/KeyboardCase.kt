package org.jyutping.jyutping.keyboard

enum class KeyboardCase {
        Lowercased,
        Uppercased,
        CapsLocked;
        fun isLowercased(): Boolean = (this == Lowercased)
        fun isUppercased(): Boolean = (this == Uppercased)
        fun isCapsLocked(): Boolean = (this == CapsLocked)
}
