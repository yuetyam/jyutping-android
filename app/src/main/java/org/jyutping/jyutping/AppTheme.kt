package org.jyutping.jyutping

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(content: @Composable () -> Unit) {
        MaterialExpressiveTheme(
                colorScheme = if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current),
                content = content
        )
}

@Composable
fun CompactTheme(content: @Composable () -> Unit) {
        MaterialExpressiveTheme(
                colorScheme = if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current),
                typography = Typography(
                        bodyLarge = MaterialTheme.typography.bodyMedium
                ),
                content = content
        )
}
