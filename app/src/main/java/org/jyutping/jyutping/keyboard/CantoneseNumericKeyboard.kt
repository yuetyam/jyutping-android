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
fun CantoneseNumericKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val needsNumberRow by context.needsNumberRow.collectAsState()
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
                        .padding(bottom = extraBottomPadding.applyingValue.dp)
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
                if (needsNumberRow) {
                        CantoneseNumberRow(height = keyHeight)
                }
                CantoneseNumberRow(height = keyHeight)
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
                        TransformKey(destination = KeyboardForm.Symbolic, modifier = Modifier.weight(1.4f))
                        Spacer(modifier = Modifier.weight(0.1f))
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
                        Spacer(modifier = Modifier.weight(0.1f))
                        BackspaceKey(modifier = Modifier.weight(1.4f))
                }
                CantoneseAltBottomKeyRow(transform = KeyboardForm.Primary, height = keyHeight)
        }
}
