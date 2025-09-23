package org.jyutping.jyutping.presets

import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.graphics.Color

object PresetColor {

        fun attach(context: Context) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val canBlur: Boolean = context.getSystemService(WindowManager::class.java)?.isCrossWindowBlurEnabled ?: false
                        lightBackground = if (canBlur) semiLight else fullLight
                        darkBackground = if (canBlur) semiDark else fullDark
                }
        }

        val supportsBlur: Boolean = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

        private val semiLight = Color(0xCCD0D4D8)
        private val fullLight = Color(0xFFD0D4D8)
        var lightBackground: Color = if (supportsBlur) semiLight else fullLight
                private set

        private val semiDark = Color(0xCC222222)
        private val fullDark = Color(0xFF222222)
        var darkBackground: Color = if (supportsBlur) semiDark else fullDark
                private set

        val shallowLight      : Color = Color.White.copy(0.95f)
        val emphaticLight     : Color = Color(0x40686E80)
        val solidShallowLight : Color = Color.White
        val solidEmphaticLight: Color = Color(0xFFAAAFBA)

        val shallowDark       : Color = Color.White.copy(alpha = 0.3f)
        val emphaticDark      : Color = Color.White.copy(alpha = 0.15f)
        val solidShallowDark  : Color = Color(0xFF666666)
        val solidEmphaticDark : Color = Color(0xFF444444)

        val red                    : Color = Color(0xFFF44336)
        val green                  : Color = Color(0xFF4CAF50)
        val blue                   : Color = Color(0xFF2196F3)
        val orange                 : Color = Color(0xFFFF9800)
}

object AltPresetColor {
        val lightBackground: Color = Color.White
        val darkBackground : Color = Color.Black
        val shallowLight : Color = Color.White
        val emphaticLight: Color = Color(0xFFBBBBBB)
        val shallowDark  : Color = Color(0xFF666666)
        val emphaticDark : Color = Color(0xFF444444)
}
