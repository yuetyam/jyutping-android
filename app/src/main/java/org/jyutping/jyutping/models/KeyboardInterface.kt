package org.jyutping.jyutping.models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class KeyboardInterface {
        PhonePortrait,
        PhoneLandscape,
        PadPortrait,
        PadLandscape;

        val isPhonePortrait: Boolean
                get() = (this == PhonePortrait)

        val isPhoneLandscape: Boolean
                get() = (this == PhoneLandscape)

        val keyHorizontalPadding: Dp
                get() = when (this) {
                        PhoneLandscape -> 6.dp
                        else -> 3.dp
                }
        val keyVerticalPadding: Dp
                get() = when (this) {
                        PhoneLandscape -> 3.dp
                        else -> 6.dp
                }
}
