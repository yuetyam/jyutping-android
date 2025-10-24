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
                        EnhancedInputKey(
                                side = KeySide.Left,
                                event = InputKeyEvent.number3,
                                keyModel = KeyModel(
                                        primary = KeyElement("3"),
                                        members = listOf(
                                                KeyElement("3"),
                                                KeyElement(text = "３", header = PresetString.FULL_WIDTH),
                                                KeyElement("叁"),
                                                KeyElement(text = "³", header = "上標"),
                                                KeyElement(text = "₃", header = "下標"),
                                                KeyElement("③")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                event = InputKeyEvent.number4,
                                keyModel = KeyModel(
                                        primary = KeyElement("4"),
                                        members = listOf(
                                                KeyElement("4"),
                                                KeyElement(text = "４", header = PresetString.FULL_WIDTH),
                                                KeyElement("肆"),
                                                KeyElement(text = "⁴", header = "上標"),
                                                KeyElement(text = "₄", header = "下標"),
                                                KeyElement("④")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                event = InputKeyEvent.number5,
                                keyModel = KeyModel(
                                        primary = KeyElement("5"),
                                        members = listOf(
                                                KeyElement("5"),
                                                KeyElement(text = "５", header = PresetString.FULL_WIDTH),
                                                KeyElement("伍"),
                                                KeyElement(text = "⁵", header = "上標"),
                                                KeyElement(text = "₅", header = "下標"),
                                                KeyElement("⑤")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number6,
                                keyModel = KeyModel(
                                        primary = KeyElement("6"),
                                        members = listOf(
                                                KeyElement("6"),
                                                KeyElement(text = "６", header = PresetString.FULL_WIDTH),
                                                KeyElement("陸"),
                                                KeyElement(text = "⁶", header = "上標"),
                                                KeyElement(text = "₆", header = "下標"),
                                                KeyElement("⑥")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number7,
                                keyModel = KeyModel(
                                        primary = KeyElement("7"),
                                        members = listOf(
                                                KeyElement("7"),
                                                KeyElement(text = "７", header = PresetString.FULL_WIDTH),
                                                KeyElement("柒"),
                                                KeyElement(text = "⁷", header = "上標"),
                                                KeyElement(text = "₇", header = "下標"),
                                                KeyElement("⑦")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number8,
                                keyModel = KeyModel(
                                        primary = KeyElement("8"),
                                        members = listOf(
                                                KeyElement("8"),
                                                KeyElement(text = "８", header = PresetString.FULL_WIDTH),
                                                KeyElement("捌"),
                                                KeyElement(text = "⁸", header = "上標"),
                                                KeyElement(text = "₈", header = "下標"),
                                                KeyElement("⑧")
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                event = InputKeyEvent.number9,
                                keyModel = KeyModel(
                                        primary = KeyElement("9"),
                                        members = listOf(
                                                KeyElement("9"),
                                                KeyElement(text = "９", header = PresetString.FULL_WIDTH),
                                                KeyElement("玖"),
                                                KeyElement(text = "⁹", header = "上標"),
                                                KeyElement(text = "₉", header = "下標"),
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
                        EdgeEnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("-"),
                                        members = listOf(
                                                KeyElement("-"),
                                                KeyElement("－", header = PresetString.FULL_WIDTH, footer = "FF0D"),
                                                KeyElement("—", footer = "2014"),
                                                KeyElement("–", footer = "2013"),
                                                KeyElement("•", footer = "2022")
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
                                                KeyElement("／", header = PresetString.FULL_WIDTH),
                                                KeyElement("\\"),
                                                KeyElement("÷"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("："),
                                        members = listOf(
                                                KeyElement("："),
                                                KeyElement(":", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("；"),
                                        members = listOf(
                                                KeyElement("；"),
                                                KeyElement(";", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("（"),
                                        members = listOf(
                                                KeyElement("（"),
                                                KeyElement("(", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("）"),
                                        members = listOf(
                                                KeyElement("）"),
                                                KeyElement(")", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
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
                                        primary = KeyElement("@"),
                                        members = listOf(
                                                KeyElement("@"),
                                                KeyElement("＠", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("「"),
                                        members = listOf(
                                                KeyElement("「"),
                                                KeyElement("『"),
                                                KeyElement(text = "201C".toCharText()),
                                                KeyElement(text = "2018".toCharText()),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("」"),
                                        members = listOf(
                                                KeyElement("」"),
                                                KeyElement("』"),
                                                KeyElement(text = "201D".toCharText()),
                                                KeyElement(text = "2019".toCharText()),
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
                                        primary = KeyElement("。"),
                                        members = listOf(
                                                KeyElement("。"),
                                                KeyElement("｡", header = PresetString.HALF_WIDTH),
                                                KeyElement(text = "2026".toCharText(), footer = "2026"),
                                                KeyElement(text = "22EF".toCharText(), footer = "22EF"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("，"),
                                        members = listOf(
                                                KeyElement("，"),
                                                KeyElement(",", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("、"),
                                        members = listOf(
                                                KeyElement("、"),
                                                KeyElement("､", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("？"),
                                        members = listOf(
                                                KeyElement("？"),
                                                KeyElement("?", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("！"),
                                        members = listOf(
                                                KeyElement("！"),
                                                KeyElement("!", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("."),
                                        members = listOf(
                                                KeyElement("."),
                                                KeyElement(text = "．", header = PresetString.FULL_WIDTH, footer = "FF0E"),
                                                KeyElement(text = "…", footer = "2026"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "0022".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "0022".toCharText(), footer = "0022"),
                                                KeyElement(text = "FF02".toCharText(), header = PresetString.FULL_WIDTH, footer = "FF02"),
                                                KeyElement(text = "201D".toCharText(), header = "右", footer = "201D"),
                                                KeyElement(text = "201C".toCharText(), header = "左", footer = "201C"),
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
