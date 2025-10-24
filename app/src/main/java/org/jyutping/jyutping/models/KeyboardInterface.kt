package org.jyutping.jyutping.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class KeyboardInterface {
        PhonePortrait,
        PhoneLandscape,
        PadPortrait,
        PadLandscape;

        val isCompact: Boolean
                get() = when (this) {
                        PhonePortrait, PhoneLandscape -> true
                        else -> false
                }

        val isPhonePortrait: Boolean
                get() = (this == PhonePortrait)

        val isPhoneLandscape: Boolean
                get() = (this == PhoneLandscape)

        fun keyHorizontalPadding(): Dp = when (this) {
                PhoneLandscape -> 6.dp
                else -> 3.dp
        }
        fun keyVerticalPadding(): Dp = when (this) {
                PhoneLandscape -> 3.dp
                else -> 6.dp
        }
}
