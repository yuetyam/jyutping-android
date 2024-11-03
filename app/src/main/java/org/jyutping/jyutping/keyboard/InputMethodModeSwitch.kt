package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun InputMethodModeSwitch() {
        val context = LocalContext.current as JyutpingInputMethodService
        val inputMethodMode = remember { context.inputMethodMode }
        val characterStandard = remember { context.characterStandard }
        val isDarkMode = remember { context.isDarkMode }
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val shapeWidth: Dp = 72.dp
        val shapeHeight: Dp = 25.dp
        val partWidth: Dp = 36.dp
        val cornerRadius: Dp = 5.dp
        val largerFontSize = 17.sp
        val normalFontSize = 13.sp
        val shape = RoundedCornerShape(cornerRadius)
        Box(
                modifier = Modifier
                        .border(
                                width = 1.dp,
                                color = if (isDarkMode.value) {
                                        if (isHighContrastPreferred) Color.White else Color.Transparent
                                } else {
                                        if (isHighContrastPreferred) Color.Black else Color.Transparent
                                },
                                shape = shape
                        )
                        .background(
                                color = if (isDarkMode.value) {
                                        if (isHighContrastPreferred) Color.Black else PresetColor.keyDarkEmphatic
                                } else {
                                        if (isHighContrastPreferred) Color.White else PresetColor.keyLightEmphatic
                                },
                                shape = shape
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
                                                color = optionBorderColor(inputMethodMode.value.isCantonese(), isDarkMode.value, isHighContrastPreferred),
                                                shape = shape
                                        )
                                        .background(
                                                color = optionBackgroundColor(inputMethodMode.value.isCantonese(), isDarkMode.value, isHighContrastPreferred),
                                                shape = shape
                                        )
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = if (characterStandard.value.isSimplified()) "粤" else "粵",
                                        color = if (isDarkMode.value) Color.White else Color.Black,
                                        fontSize = if (inputMethodMode.value.isCantonese()) largerFontSize else normalFontSize
                                )
                        }
                        Box(
                                modifier = Modifier
                                        .border(
                                                width = 1.dp,
                                                color = optionBorderColor(inputMethodMode.value.isABC(), isDarkMode.value, isHighContrastPreferred),
                                                shape = shape
                                        )
                                        .background(
                                                color = optionBackgroundColor(inputMethodMode.value.isABC(), isDarkMode.value, isHighContrastPreferred),
                                                shape = shape
                                        )
                                        .width(partWidth)
                                        .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = "A",
                                        color = if (isDarkMode.value) Color.White else Color.Black,
                                        fontSize = if (inputMethodMode.value.isABC()) largerFontSize else normalFontSize
                                )
                        }
                }
        }
}

private fun optionBorderColor(isSelected: Boolean, isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color =
        if (isSelected) {
                if (isDarkMode) {
                        if (isHighContrastPreferred) Color.White else Color.Transparent
                } else {
                        if (isHighContrastPreferred) Color.Black else Color.Transparent
                }
        } else {
                Color.Transparent
        }

private fun optionBackgroundColor(isSelected: Boolean, isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color =
        if (isSelected) {
                if (isDarkMode) {
                        if (isHighContrastPreferred) Color.Black else PresetColor.keyDark
                } else {
                        if (isHighContrastPreferred) Color.White else PresetColor.keyLight
                }
        } else {
                Color.Transparent
        }
