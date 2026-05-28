package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
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
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun AdvancedIconButton(
        modifier: Modifier = Modifier,
        icon: ImageVector,
        iconSize: Dp = 26.dp,
        size: Dp = 44.dp,
        alternative: Boolean = false,
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
                                color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                shape = CircleShape
                        )
                        .padding(1.dp),
                colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isHighContrastPreferred) {
                                if (isDarkMode) AltPresetColor.emphaticDark else if (alternative) AltPresetColor.emphaticLight else AltPresetColor.shallowLight
                        } else {
                                if (isDarkMode) PresetColor.solidShallowDark else PresetColor.solidShallowLight
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
