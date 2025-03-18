package org.jyutping.jyutping.tenkey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.TenKeyGlobeKey
import org.jyutping.jyutping.keyboard.ToolBar
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun TenKeyNumericKeyboard(height: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val needsInputModeSwitchKey by context.needsInputModeSwitchKey.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val keyHeight: Dp = (height - PresetConstant.ToolBarHeight.dp) / 4
        val sidebarUnitHeight: Dp = keyHeight * 3f / 4f
        Column(
                modifier = Modifier
                        .background(
                                if (isDarkMode) {
                                        if (isHighContrastPreferred) Color.Black else PresetColor.keyboardDarkBackground
                                } else {
                                        if (isHighContrastPreferred) Color.White else PresetColor.keyboardLightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.paddingValue().dp)
                        .height(height)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
                                .background(
                                        if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.Black else PresetColor.keyboardDarkBackground
                                        } else {
                                                if (isHighContrastPreferred) Color.White else PresetColor.keyboardLightBackground
                                        }
                                )
                                .height(PresetConstant.ToolBarHeight.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        ToolBar()
                }
                Row(
                        modifier = Modifier
                                .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                        Column(
                                modifier = Modifier
                                        .weight(0.2f)
                                        .fillMaxHeight()
                        ) {
                                TenKeySymbolSidebar(
                                        unitHeight = sidebarUnitHeight,
                                        modifier = Modifier
                                                .weight(0.75f)
                                                .fillMaxWidth()
                                )
                                TenKeyTransformKey(
                                        destination = KeyboardForm.Alphabetic,
                                        modifier = Modifier
                                                .weight(0.25f)
                                                .fillMaxWidth()
                                )
                        }
                        Column(
                                modifier = Modifier
                                        .weight(0.6f)
                                        .fillMaxHeight()
                        ) {
                                Row(
                                        modifier = Modifier
                                                .weight(0.25f)
                                ) {
                                        TenKeyNumericInputKey(keyText = "1", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "2", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "3", modifier = Modifier.weight(1f / 3f))
                                }
                                Row(
                                        modifier = Modifier
                                                .weight(0.25f)
                                ) {
                                        TenKeyNumericInputKey(keyText = "4", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "5", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "6", modifier = Modifier.weight(1f / 3f))
                                }
                                Row(
                                        modifier = Modifier
                                                .weight(0.25f)
                                ) {
                                        TenKeyNumericInputKey(keyText = "7", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "8", modifier = Modifier.weight(1f / 3f))
                                        TenKeyNumericInputKey(keyText = "9", modifier = Modifier.weight(1f / 3f))
                                }
                                if (needsInputModeSwitchKey) {
                                        Row(
                                                modifier = Modifier
                                                        .weight(0.25f)
                                        ) {
                                                TenKeyGlobeKey(modifier = Modifier.weight(1f / 3f))
                                                TenKeyNumericInputKey(keyText = "0", modifier = Modifier.weight(1f / 3f))
                                                TenKeyNumericInputKey(keyText = ".", modifier = Modifier.weight(1f / 3f))
                                        }
                                } else {
                                        Row(
                                                modifier = Modifier
                                                        .weight(0.25f)
                                        ) {
                                                TenKeyNumericInputKey(keyText = ".", modifier = Modifier.weight(1f / 3f))
                                                TenKeyNumericInputKey(keyText = "0", modifier = Modifier.weight(1f / 3f))
                                                TenKeySpaceKey(modifier = Modifier.weight(1f / 3f))
                                        }
                                }
                        }
                        Column(
                                modifier = Modifier
                                        .weight(0.2f)
                                        .fillMaxHeight()
                        ) {
                                TenKeyBackspaceKey(
                                        modifier = Modifier
                                                .weight(0.25f)
                                )
                                TenKeyTransformKey(
                                        destination = KeyboardForm.Numeric,
                                        modifier = Modifier
                                                .weight(0.25f)
                                )
                                TenKeyReturnKey(
                                        modifier = Modifier
                                                .weight(0.5f)
                                )
                        }
                }
        }
}
