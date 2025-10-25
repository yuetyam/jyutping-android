package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun AdvancedIconButton(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        iconSize: Dp = 20.dp,
        size: Dp = 44.dp,
        action: () -> Unit
) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        IconButton(
                onClick = action,
                modifier = modifier
                        .size(size)
                        .border(
                                width = 1.dp,
                                color = if (isHighContrastPreferred) {
                                        if (isDarkMode) Color.White else Color.Black
                                } else {
                                        Color.Transparent
                                },
                                shape = CircleShape
                        ),
                colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isHighContrastPreferred) {
                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                        } else {
                                if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
                        },
                        contentColor = if (isDarkMode) Color.White else Color.Black
                ),
                shape = CircleShape
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                )
        }
}
