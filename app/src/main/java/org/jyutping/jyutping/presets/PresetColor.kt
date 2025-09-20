package org.jyutping.jyutping.presets

import android.os.Build
import androidx.compose.ui.graphics.Color

object PresetColor {

        /**
         * Should blur keyboard background
         *
         * True if API Level 31+, that is, Android 12+
         */
        val isBlurPreferred: Boolean = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

        val keyLight               : Color = Color.White
        val keyLightEmphatic       : Color = Color(0xFFAAAFBA)
        val keyboardLightBackground: Color = if (isBlurPreferred) Color(0xCCD0D4D8) else Color(0xFFD0D4D8)

        val keyDark                : Color = Color(0xFF666666)
        val keyDarkEmphatic        : Color = Color(0xFF444444)
        val keyboardDarkBackground : Color = if (isBlurPreferred) Color(0xCC222222) else Color(0xFF222222)

        // Key shadow
        val shadowGray: Color = Color(0x88888888)

        val red                    : Color = Color(0xFFF44336)
        val green                  : Color = Color(0xFF4CAF50)
        val blue                   : Color = Color(0xFF2196F3)
        val orange                 : Color = Color(0xFFFF9800)
}

object AltPresetColor {
        val keyLight               : Color = Color.White
        val keyLightEmphatic       : Color = Color(0xFFBBBBBB)
        val keyboardLightBackground: Color = Color(0xFFDDDDDD)

        val keyDark                : Color = Color(0xFF666666)
        val keyDarkEmphatic        : Color = Color(0xFF444444)
        val keyboardDarkBackground : Color = Color(0xFF222222)
}

/*
object ShallowPresetColor {
        val keyLight               : Color = Color.White
        val keyLightEmphatic       : Color = Color(0xFFB8BCC4)
        val keyboardLightBackground: Color = Color(0xFFDDDFE4)

        val keyDark                : Color = Color(0xFF666666)
        val keyDarkEmphatic        : Color = Color(0xFF444444)
        val keyboardDarkBackground : Color = Color(0xFF222222)
}

object IOSPresetColor {
        val keyLight               : Color = Color.White
        val keyLightEmphatic       : Color = Color(0xFFACB1B9)
        val keyboardLightBackground: Color = Color(0xFFD0D4D8)

        val keyDark                : Color = Color.White.copy(alpha = 0.35f)
        val keyDarkEmphatic        : Color = Color.White.copy(alpha = 0.15f)
        val keyDarkOpacity         : Color = Color(0xFF686868)
        val keyDarkEmphaticOpacity : Color = Color(0xFF464646)
        val keyboardDarkBackground : Color = Color(0xFF333333)
}
*/
