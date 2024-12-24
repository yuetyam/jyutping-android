package org.jyutping.jyutping.keyboard

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class KeyboardInterface {
        PhonePortrait,
        PhoneLandscape,
        PadPortrait,
        PadLandscape;

        fun isCompact(): Boolean = when (this) {
                PhonePortrait, PhoneLandscape -> true
                else -> false
        }
        fun isPhonePortrait(): Boolean = (this == PhonePortrait)
        fun isPhoneLandscape(): Boolean = (this == PhoneLandscape)

        fun keyHorizontalPadding(): Dp = when (this) {
                PhoneLandscape -> 6.dp
                else -> 3.dp
        }
        fun keyVerticalPadding(): Dp = when (this) {
                PhoneLandscape -> 3.dp
                else -> 6.dp
        }
}
