package org.jyutping.jyutping.presets

import androidx.compose.ui.graphics.Color

object PresetColor {

        fun attach(canBlur: Boolean) {
                lightBackground = if (canBlur) semiLight else fullLight
                darkBackground  = if (canBlur) semiDark  else fullDark
        }

        private val semiLight = Color(0xAAD0D4D8)
        private val fullLight = Color(0xFFD0D4D8)
        var lightBackground: Color = fullLight
                private set

        private val semiDark = Color(0xAA222222)
        private val fullDark = Color(0xFF222222)
        var darkBackground: Color = fullDark
                private set

        val shallowLight       = Color.White.copy(0.95f)
        val emphaticLight      = Color(0x40686E80)
        val solidShallowLight  = Color.White
        val solidEmphaticLight = Color(0xFFAAAFBA)

        val shallowDark        = Color.White.copy(alpha = 0.3f)
        val emphaticDark       = Color.White.copy(alpha = 0.15f)
        val solidShallowDark   = Color(0xFF666666)
        val solidEmphaticDark  = Color(0xFF444444)

        val red    = Color(0xFFF44336)
        val green  = Color(0xFF4CAF50)
        val blue   = Color(0xFF2196F3)
        val orange = Color(0xFFFF9800)
}

object AltPresetColor {
        val lightBackground = Color.White
        val darkBackground  = Color.Black
        val shallowLight    = Color.White
        val emphaticLight   = Color(0xFFBBBBBB)
        val shallowDark     = Color(0xFF666666)
        val emphaticDark    = Color(0xFF444444)
}

object AppleColor {
        val blue   = Color(0xFF0088FF)
        val green  = Color(0xFF34C759)
        val indigo = Color(0xFF6155F5)
        val orange = Color(0xFFFF8D28)
        val purple = Color(0xFFCB30E0)
        val red    = Color(0xFFFF3814)
        val teal   = Color(0xFF00C3D0)
}
