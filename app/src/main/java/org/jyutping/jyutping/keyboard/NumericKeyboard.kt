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
import org.jyutping.jyutping.extensions.toCharText
import org.jyutping.jyutping.models.InputKeyEvent
import org.jyutping.jyutping.models.KeyElement
import org.jyutping.jyutping.models.KeyModel
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun NumericKeyboard(keyHeight: Dp) {
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
                        NumberKey(InputKeyEvent.number1, modifier = Modifier.weight(1f), position = Alignment.Start)
                        NumberKey(InputKeyEvent.number2, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number3, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number4, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number5, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number6, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number7, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number8, modifier = Modifier.weight(1f))
                        NumberKey(InputKeyEvent.number9, modifier = Modifier.weight(1f))
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number0,
                                keyModel = KeyModel(
                                        primary = KeyElement("0"),
                                        members = listOf(
                                                KeyElement("0"),
                                                KeyElement("°"),
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
                        EdgeEnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("-"),
                                        members = listOf(
                                                KeyElement("-"),
                                                KeyElement("–", footer = "2013"),
                                                KeyElement("—", footer = "2014"),
                                                KeyElement("•", footer = "2022"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("/"),
                                        members = listOf(
                                                KeyElement("/"),
                                                KeyElement("\\"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = ":", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = ";", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "(", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = ")", modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("$"),
                                        members = listOf(
                                                KeyElement("$"),
                                                KeyElement("€"),
                                                KeyElement("£"),
                                                KeyElement("¥"),
                                                KeyElement("₩"),
                                                KeyElement("₽"),
                                                KeyElement("¢"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("&"),
                                        members = listOf(
                                                KeyElement("&"),
                                                KeyElement("§"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = "@", modifier = Modifier.weight(1f))
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "0022".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "0022".toCharText(), footer = "0022"),
                                                KeyElement(text = "201D".toCharText(), footer = "201D"),
                                                KeyElement(text = "201C".toCharText(), footer = "201C"),
                                                KeyElement(text = "201E".toCharText(), footer = "201E"),
                                                KeyElement(text = "00BB".toCharText(), footer = "00BB"),
                                                KeyElement(text = "00AB".toCharText(), footer = "00AB"),
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
                        TransformKey(destination = KeyboardForm.Symbolic, modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("."),
                                        members = listOf(
                                                KeyElement("."),
                                                KeyElement("…"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = ",", modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("?"),
                                        members = listOf(
                                                KeyElement("?"),
                                                KeyElement("¿"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("!"),
                                        members = listOf(
                                                KeyElement("!"),
                                                KeyElement("¡"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("%"),
                                        members = listOf(
                                                KeyElement("%"),
                                                KeyElement("‰"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = "*", modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "0027".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "0027".toCharText(), footer = "0027"),
                                                KeyElement(text = "2019".toCharText(), footer = "2019"),
                                                KeyElement(text = "2018".toCharText(), footer = "2018"),
                                                KeyElement(text = "0060".toCharText(), footer = "0060"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(transform = KeyboardForm.Alphabetic, height = keyHeight)
        }
}
