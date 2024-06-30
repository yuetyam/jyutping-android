package org.jyutping.jyutping.keyboard

enum class KeyboardCase {
        Lowercased,
        Uppercased,
        CapsLocked
}

fun KeyboardCase.isLowercased(): Boolean = this == KeyboardCase.Lowercased
fun KeyboardCase.isUppercased(): Boolean = this == KeyboardCase.Uppercased
fun KeyboardCase.isCapsLocked(): Boolean = this == KeyboardCase.CapsLocked
