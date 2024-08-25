package org.jyutping.jyutping.presets

import androidx.compose.ui.graphics.Color

object PresetColor {
        val keyboardLightBackground: Color = Color(0xFFD0D3D9)
        val keyboardDarkBackground : Color = Color(0xFF333333)

        val keyLight               : Color = Color.White
        val keyLightEmphatic       : Color = Color(0xFFACB1B9)

        val keyDark                : Color = Color.White.copy(alpha = 0.35f)
        val keyDarkEmphatic        : Color = Color.White.copy(alpha = 0.15f)
        val keyDarkOpacity         : Color = Color(0xFF686868)
        val keyDarkEmphaticOpacity : Color = Color(0xFF464646)
}
