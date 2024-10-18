package org.jyutping.jyutping.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun JyutpingTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        dynamicColor: Boolean = true, // Dynamic color is available on Android 12+ (API 31+)
        content: @Composable () -> Unit
) {
        val colorScheme = when {
                dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> if (darkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
                darkTheme -> darkColorScheme()
                else -> lightColorScheme()
        }
        /*
        val view = LocalView.current
        if (!view.isInEditMode) {
                val window = (view.context as? Activity)?.window ?: return
                SideEffect {
                        window.statusBarColor = Color.Transparent.toArgb()
                        window.navigationBarColor = Color.Transparent.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
                        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = darkTheme
                }
        }
        */
        MaterialTheme(
                colorScheme = colorScheme,
                content = content
        )
}
