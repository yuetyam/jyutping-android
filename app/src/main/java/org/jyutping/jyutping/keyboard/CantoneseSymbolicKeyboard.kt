package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun CantoneseSymbolicKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        Column(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.darkBackground else AltPresetColor.lightBackground
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.paddingValue().dp)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
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
                        SymbolKey(symbol = "［", modifier = Modifier.weight(1f), position = Alignment.Start)
                        SymbolKey(symbol = "］", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｛", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｝", modifier = Modifier.weight(1f))
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
                        SymbolKey(symbol = "—", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "\\", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "｜", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "～", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "《", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "》", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "¥", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "&", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "·", modifier = Modifier.weight(1f), position = Alignment.End)
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
