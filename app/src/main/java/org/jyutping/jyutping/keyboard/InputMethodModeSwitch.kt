package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun InputMethodModeSwitch() {
        val context = LocalContext.current as JyutpingInputMethodService
        val inputMethodMode by context.inputMethodMode.collectAsState()
        val characterStandard by context.characterStandard.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val partWidth: Dp = 34.dp
        val shapeWidth: Dp = 68.dp
        val shapeHeight: Dp = 25.dp
        val largerFontSize = 17.sp
        val normalFontSize = 13.sp
        Box(
                modifier = Modifier
                        .border(
                                width = 1.dp,
                                color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                shape = CircleShape
                        )
                        .background(
                                color = if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                } else {
                                        if (isDarkMode) PresetColor.emphaticDark else PresetColor.emphaticLight
                                },
                                shape = CircleShape
                        )
                        .width(shapeWidth)
                        .height(shapeHeight),
                contentAlignment = Alignment.Center
        ) {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Box(
                                modifier = Modifier
                                        .border(
                                                width = 1.dp,
                                                color = optionBorderColor(inputMethodMode.isCantonese(), isDarkMode, isHighContrastPreferred),
                                                shape = CircleShape
                                        )
                                        .background(
                                                color = optionBackgroundColor(inputMethodMode.isCantonese(), isDarkMode, isHighContrastPreferred),
                                                shape = CircleShape
                                        )
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = if (characterStandard.isSimplified) "粤" else "粵",
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = if (inputMethodMode.isCantonese()) largerFontSize else normalFontSize
                                )
                        }
                        Box(
                                modifier = Modifier
                                        .border(
                                                width = 1.dp,
                                                color = optionBorderColor(inputMethodMode.isABC(), isDarkMode, isHighContrastPreferred),
                                                shape = CircleShape
                                        )
                                        .background(
                                                color = optionBackgroundColor(inputMethodMode.isABC(), isDarkMode, isHighContrastPreferred),
                                                shape = CircleShape
                                        )
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = "A",
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = if (inputMethodMode.isABC()) largerFontSize else normalFontSize
                                )
                        }
                }
        }
}

private fun optionBorderColor(isSelected: Boolean, isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred && isSelected) {
        if (isDarkMode) Color.White else Color.Black
} else {
        Color.Transparent
}

private fun optionBackgroundColor(isSelected: Boolean, isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isSelected) {
        if (isHighContrastPreferred) {
                if (isDarkMode) AltPresetColor.shallowDark else AltPresetColor.shallowLight
        } else {
                if (isDarkMode) PresetColor.shallowDark else PresetColor.shallowLight
        }
} else {
        Color.Transparent
}
