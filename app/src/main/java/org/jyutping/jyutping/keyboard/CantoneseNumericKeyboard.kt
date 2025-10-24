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
import org.jyutping.jyutping.models.InputKeyEvent
import org.jyutping.jyutping.models.KeyElement
import org.jyutping.jyutping.models.KeyModel
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString

@Composable
fun CantoneseNumericKeyboard(keyHeight: Dp) {
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
                        EdgeEnhancedInputKey(
                                side = KeySide.Left,
                                event = InputKeyEvent.number1,
                                keyModel = KeyModel(
                                        primary = KeyElement("1"),
                                        members = listOf(
                                                KeyElement("1"),
                                                KeyElement(text = "１", header = PresetString.FULL_WIDTH),
                                                KeyElement("壹"),
                                                KeyElement(text = "¹", header = "上標"),
                                                KeyElement(text = "₁", header = "下標"),
                                                KeyElement("①")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                event = InputKeyEvent.number2,
                                keyModel = KeyModel(
                                        primary = KeyElement("2"),
                                        members = listOf(
                                                KeyElement("2"),
                                                KeyElement(text = "２", header = PresetString.FULL_WIDTH),
                                                KeyElement("貳"),
                                                KeyElement(text = "²", header = "上標"),
                                                KeyElement(text = "₂", header = "下標"),
                                                KeyElement("②")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = "3", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "4", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "5", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "6", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "7", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "8", modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number9,
                                keyModel = KeyModel(
                                        primary = KeyElement("9"),
                                        members = listOf(
                                                KeyElement("9"),
                                                KeyElement(text = "９", header = PresetString.FULL_WIDTH),
                                                KeyElement("玖"),
                                                KeyElement(text = "⁰", header = "上標"),
                                                KeyElement(text = "₀", header = "下標"),
                                                KeyElement("⑨")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number0,
                                keyModel = KeyModel(
                                        primary = KeyElement("0"),
                                        members = listOf(
                                                KeyElement("0"),
                                                KeyElement(text = "０", header = PresetString.FULL_WIDTH),
                                                KeyElement("零"),
                                                KeyElement(text = "⁰", header = "上標"),
                                                KeyElement(text = "₀", header = "下標"),
                                                KeyElement("⓪"),
                                                KeyElement("拾"),
                                                KeyElement(text = "°", header = "度")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
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
                        Spacer(modifier = Modifier.weight(0.2f))
                        SymbolKey(symbol = "。", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "，", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "、", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "？", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "！", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = ".", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "\"", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(transform = KeyboardForm.Alphabetic, height = keyHeight)
        }
}
