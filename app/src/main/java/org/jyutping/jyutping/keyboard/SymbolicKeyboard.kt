package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun SymbolicKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val shouldApplyExtraBottomPadding by context.needsExtraBottomPadding.collectAsState()
        val extraBottomPadding: Dp = when {
                shouldApplyExtraBottomPadding -> {
                        val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        val mandatorySystemGesturesBottom = WindowInsets.mandatorySystemGestures.asPaddingValues().calculateBottomPadding()
                        val bottomPadding = max(navigationBarBottom, mandatorySystemGesturesBottom)
                        if (bottomPadding > 0.dp) bottomPadding
                        val systemGesturesBottom = WindowInsets.systemGestures.asPaddingValues().calculateBottomPadding()
                        if (systemGesturesBottom > 0.dp) systemGesturesBottom else PresetConstant.FallbackExtraBottomPadding.dp
                }
                else -> 0.dp
        }
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
                        .padding(bottom = extraBottomPadding)
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
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        SymbolKey(symbol = "[", modifier = Modifier.weight(1f), position = Alignment.Start)
                        SymbolKey(symbol = "]", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "{", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "}", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "#", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "%", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "^", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "*", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "+", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "=", modifier = Modifier.weight(1f), position = Alignment.End)
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        SymbolKey(symbol = "_", modifier = Modifier.weight(1f), position = Alignment.Start)
                        SymbolKey(symbol = "\\", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "|", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "~", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "<", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = ">", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "€", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "£", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "¥", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "•", modifier = Modifier.weight(1f), position = Alignment.End)
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        TransformKey(destination = KeyboardForm.Numeric, modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        SymbolKey(symbol = "…", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "©", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "®", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "℗", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "™", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "℠", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "'", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(transform = KeyboardForm.Alphabetic, height = keyHeight)
        }
}
