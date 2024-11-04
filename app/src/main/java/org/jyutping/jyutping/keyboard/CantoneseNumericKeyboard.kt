package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun CantoneseNumericKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
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
                        SymbolKey(symbol = "1", modifier = Modifier.weight(1f), position = Alignment.Start)
                        SymbolKey(symbol = "2", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "3", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "4", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "5", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "6", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "7", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "8", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "9", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "0", modifier = Modifier.weight(1f), position = Alignment.End)
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        SymbolKey(symbol = "-", modifier = Modifier.weight(1f), position = Alignment.Start)
                        SymbolKey(symbol = "/", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "：", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "；", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "（", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "）", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "$", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "@", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "「", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "」", modifier = Modifier.weight(1f), position = Alignment.End)
                }
                Row(
                        modifier = Modifier
                                .height(keyHeight)
                                .fillMaxWidth()
                ) {
                        TransformKey(destination = KeyboardForm.Symbolic, modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.22f))
                        SymbolKey(symbol = "。", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "，", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "、", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "？", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = "！", modifier = Modifier.weight(1.16f))
                        SymbolKey(symbol = ".", modifier = Modifier.weight(1.16f))
                        Spacer(modifier = Modifier.weight(0.22f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(transform = KeyboardForm.Alphabetic, height = keyHeight)
        }
}
