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
import org.jyutping.jyutping.models.KeyElement
import org.jyutping.jyutping.models.KeyModel
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString

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
                        EdgeEnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("［"),
                                        members = listOf(
                                                KeyElement("［"),
                                                KeyElement(text = "[", header = PresetString.HALF_WIDTH),
                                                KeyElement("【"),
                                                KeyElement("〖"),
                                                KeyElement("〔"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("］"),
                                        members = listOf(
                                                KeyElement("］"),
                                                KeyElement("]", header = PresetString.HALF_WIDTH),
                                                KeyElement("】"),
                                                KeyElement("〗"),
                                                KeyElement("〕"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("｛"),
                                        members = listOf(
                                                KeyElement("｛"),
                                                KeyElement("{", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("｝"),
                                        members = listOf(
                                                KeyElement("｝"),
                                                KeyElement("}", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("#"),
                                        members = listOf(
                                                KeyElement("#"),
                                                KeyElement("＃", header = PresetString.FULL_WIDTH),
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
                                                KeyElement("％", header = PresetString.FULL_WIDTH),
                                                KeyElement("‰"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("^"),
                                        members = listOf(
                                                KeyElement("^"),
                                                KeyElement("＾", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("*"),
                                        members = listOf(
                                                KeyElement("*"),
                                                KeyElement("＊", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("+"),
                                        members = listOf(
                                                KeyElement("+"),
                                                KeyElement("＋", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("="),
                                        members = listOf(
                                                KeyElement("="),
                                                KeyElement(text = "＝", header = PresetString.FULL_WIDTH),
                                                KeyElement("≠"),
                                                KeyElement("≈"),
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
                                        primary = KeyElement("_"),
                                        members = listOf(
                                                KeyElement("_"),
                                                KeyElement(text = "＿", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = "2014".toCharText(), modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("\\"),
                                        members = listOf(
                                                KeyElement("\\"),
                                                KeyElement(text = "＼", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("｜"),
                                        members = listOf(
                                                KeyElement("｜"),
                                                KeyElement(text = "|", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement("～"),
                                        members = listOf(
                                                KeyElement("～"),
                                                KeyElement(text = "~", header = PresetString.HALF_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("《"),
                                        members = listOf(
                                                KeyElement("《"),
                                                KeyElement("〈"),
                                                KeyElement(text = "<", header = PresetString.HALF_WIDTH),
                                                KeyElement(text = "＜", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("》"),
                                        members = listOf(
                                                KeyElement("》"),
                                                KeyElement("〉"),
                                                KeyElement(text = ">", header = PresetString.HALF_WIDTH),
                                                KeyElement(text = "＞", header = PresetString.FULL_WIDTH),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement("¥"),
                                        members = listOf(
                                                KeyElement("¥"),
                                                KeyElement(text = "￥", header = PresetString.FULL_WIDTH),
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
                                                KeyElement(text = "＆", header = PresetString.FULL_WIDTH),
                                                KeyElement("§"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        EdgeEnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "00B7".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "00B7".toCharText(), header = "間隔號", footer = "00B7"),
                                                KeyElement(text = "2022".toCharText(), header = "項目符號", footer = "2022"),
                                                KeyElement(text = "00B0".toCharText(), header = "度"),
                                                KeyElement(text = "2027".toCharText(), header = "連字點", footer = "2027"),
                                                KeyElement(text = "FF65".toCharText(), header = "半寬中點", footer = "FF65"),
                                                KeyElement(text = "30FB".toCharText(), header = "全寬中點", footer = "30FB"),
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
                        TransformKey(destination = KeyboardForm.Numeric, modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        EnhancedInputKey(
                                side = KeySide.Left,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "2026".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "2026".toCharText(), footer = "2026"),
                                                KeyElement(text = "22EF".toCharText(), footer = "22EF"),
                                        )
                                ),
                                modifier = Modifier.weight(1f)
                        )
                        SymbolKey(symbol = "©", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "®", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "℗", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "™", modifier = Modifier.weight(1f))
                        SymbolKey(symbol = "℠", modifier = Modifier.weight(1f))
                        EnhancedInputKey(
                                side = KeySide.Right,
                                keyModel = KeyModel(
                                        primary = KeyElement(text = "0027".toCharText()),
                                        members = listOf(
                                                KeyElement(text = "0027".toCharText(), footer = "0027"),
                                                KeyElement(text = "FF07".toCharText(), header = PresetString.FULL_WIDTH, footer = "FF07"),
                                                KeyElement(text = "2019".toCharText(), header = "右", footer = "2019"),
                                                KeyElement(text = "2018".toCharText(), header = "左", footer = "2018"),
                                                KeyElement(text = "0060".toCharText(), header = "重音符", footer = "0060"),
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
