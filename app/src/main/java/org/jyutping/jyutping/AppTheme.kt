package org.jyutping.jyutping

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(content: @Composable () -> Unit) {
        MaterialExpressiveTheme(
                colorScheme = when {
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
                        isSystemInDarkTheme() -> darkColorScheme()
                        else -> lightColorScheme()
                },
                content = content
        )
}

@Composable
fun CompactTheme(content: @Composable () -> Unit) {
        MaterialExpressiveTheme(
                colorScheme = when {
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
                        isSystemInDarkTheme() -> darkColorScheme()
                        else -> lightColorScheme()
                },
                typography = Typography(
                        bodyLarge = MaterialTheme.typography.bodyMedium
                ),
                content = content
        )
}
