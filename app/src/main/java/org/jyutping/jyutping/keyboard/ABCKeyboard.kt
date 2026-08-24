package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.models.KeyElement
import org.jyutping.jyutping.models.KeyModel
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun ABCKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val useDedicatedNumberPad by context.useDedicatedNumberPad.collectAsState()
        val inputKeyStyle by context.inputKeyStyle.collectAsState()
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
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        if (inputKeyStyle.isClear) {
                                LetterKey(virtual = VirtualInputKey.letterQ, modifier = Modifier.weight(1f), position = Alignment.Start)
                                LetterKey(virtual = VirtualInputKey.letterW, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterE, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterR, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterT, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterY, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterU, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterI, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterO, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterP, modifier = Modifier.weight(1f), position = Alignment.End)
                        } else {
                                FirstEnhancedInputKeyRow()
                        }
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        HiddenKey(hidden = HiddenVirtualKey.LetterA, modifier = Modifier.weight(0.5f))
                        if (inputKeyStyle.isNumbersAndSymbols) {
                                SecondEnhancedInputKeyRow()
                        } else {
                                LetterKey(virtual = VirtualInputKey.letterA, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterS, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterD, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterF, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterG, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterH, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterJ, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterK, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterL, modifier = Modifier.weight(1f))
                        }
                        HiddenKey(hidden = HiddenVirtualKey.LetterL, modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.4f))
                        HiddenKey(hidden = HiddenVirtualKey.LetterZ, modifier = Modifier.weight(0.1f))
                        if (inputKeyStyle.isNumbersAndSymbols) {
                                ThirdEnhancedInputKeyRow()
                        } else {
                                LetterKey(virtual = VirtualInputKey.letterZ, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterX, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterC, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterV, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterB, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterN, modifier = Modifier.weight(1f))
                                LetterKey(virtual = VirtualInputKey.letterM, modifier = Modifier.weight(1f))
                        }
                        HiddenKey(hidden = HiddenVirtualKey.Backspace, modifier = Modifier.weight(0.1f))
                        BackspaceKey(modifier = Modifier.weight(1.4f))
                }
                ABCBottomKeyRow(
                        transform = if (useDedicatedNumberPad) KeyboardForm.DedicatedNumbers else KeyboardForm.Numeric,
                        height = keyHeight
                )
        }
}

@Composable
private fun RowScope.FirstEnhancedInputKeyRow() {
        EdgeEnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterQ,
                keyModel = KeyModel(
                        primary = KeyElement(text = "q", header = "1"),
                        members = listOf(
                                KeyElement(text = "q"),
                                KeyElement(text = "1")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterW,
                keyModel = KeyModel(
                        primary = KeyElement(text = "w", header = "2"),
                        members = listOf(
                                KeyElement(text = "w"),
                                KeyElement(text = "2")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterE,
                keyModel = KeyModel(
                        primary = KeyElement(text = "e", header = "3"),
                        members = listOf(
                                KeyElement(text = "e"),
                                KeyElement(text = "3"),
                                KeyElement(text = "ē"),
                                KeyElement(text = "é"),
                                KeyElement(text = "ě"),
                                KeyElement(text = "è"),
                                KeyElement(text = "ë")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterR,
                keyModel = KeyModel(
                        primary = KeyElement(text = "r", header = "4"),
                        members = listOf(
                                KeyElement(text = "r"),
                                KeyElement(text = "4")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterT,
                keyModel = KeyModel(
                        primary = KeyElement(text = "t", header = "5"),
                        members = listOf(
                                KeyElement(text = "t"),
                                KeyElement(text = "5")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterY,
                keyModel = KeyModel(
                        primary = KeyElement(text = "y", header = "6"),
                        members = listOf(
                                KeyElement(text = "y"),
                                KeyElement(text = "6")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterU,
                keyModel = KeyModel(
                        primary = KeyElement(text = "u", header = "7"),
                        members = listOf(
                                KeyElement(text = "u"),
                                KeyElement(text = "7"),
                                KeyElement(text = "ū"),
                                KeyElement(text = "ú"),
                                KeyElement(text = "ǔ"),
                                KeyElement(text = "ù"),
                                KeyElement(text = "ü")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterI,
                keyModel = KeyModel(
                        primary = KeyElement(text = "i", header = "8"),
                        members = listOf(
                                KeyElement(text = "i"),
                                KeyElement(text = "8"),
                                KeyElement(text = "ī"),
                                KeyElement(text = "í"),
                                KeyElement(text = "ǐ"),
                                KeyElement(text = "ì"),
                                KeyElement(text = "ï")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterO,
                keyModel = KeyModel(
                        primary = KeyElement(text = "o", header = "9"),
                        members = listOf(
                                KeyElement(text = "o"),
                                KeyElement(text = "9"),
                                KeyElement(text = "ō"),
                                KeyElement(text = "ó"),
                                KeyElement(text = "ǒ"),
                                KeyElement(text = "ò"),
                                KeyElement(text = "ö")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EdgeEnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterP,
                keyModel = KeyModel(
                        primary = KeyElement(text = "p", header = "0"),
                        members = listOf(
                                KeyElement(text = "p"),
                                KeyElement(text = "0")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
}

@Composable
private fun RowScope.SecondEnhancedInputKeyRow() {
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterA,
                keyModel = KeyModel(
                        primary = KeyElement(text = "a", header = "@"),
                        members = listOf(
                                KeyElement(text = "a"),
                                KeyElement(text = "@")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterS,
                keyModel = KeyModel(
                        primary = KeyElement(text = "s", header = "#"),
                        members = listOf(
                                KeyElement(text = "s"),
                                KeyElement(text = "#")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterD,
                keyModel = KeyModel(
                        primary = KeyElement(text = "d", header = "$"),
                        members = listOf(
                                KeyElement(text = "d"),
                                KeyElement(text = "$")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterF,
                keyModel = KeyModel(
                        primary = KeyElement(text = "f", header = "&"),
                        members = listOf(
                                KeyElement(text = "f"),
                                KeyElement(text = "&")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterG,
                keyModel = KeyModel(
                        primary = KeyElement(text = "g", header = "*"),
                        members = listOf(
                                KeyElement(text = "g"),
                                KeyElement(text = "*")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterH,
                keyModel = KeyModel(
                        primary = KeyElement(text = "h", header = "("),
                        members = listOf(
                                KeyElement(text = "h"),
                                KeyElement(text = "(")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterJ,
                keyModel = KeyModel(
                        primary = KeyElement(text = "j", header = ")"),
                        members = listOf(
                                KeyElement(text = "j"),
                                KeyElement(text = ")")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterK,
                keyModel = KeyModel(
                        primary = KeyElement(text = "k", header = "'"),
                        members = listOf(
                                KeyElement(text = "k"),
                                KeyElement(text = "'", footer = "0027"),
                                KeyElement(text = "’", footer = "2019"),
                                KeyElement(text = "‘", footer = "2018")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterL,
                keyModel = KeyModel(
                        primary = KeyElement(text = "l", header = "\""),
                        members = listOf(
                                KeyElement(text = "l"),
                                KeyElement(text = "\"", footer = "0022"),
                                KeyElement(text = "”", footer = "201D"),
                                KeyElement(text = "“", footer = "201C")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
}

@Composable
private fun RowScope.ThirdEnhancedInputKeyRow() {
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterZ,
                keyModel = KeyModel(
                        primary = KeyElement(text = "z", header = "%"),
                        members = listOf(
                                KeyElement(text = "z"),
                                KeyElement(text = "%")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterX,
                keyModel = KeyModel(
                        primary = KeyElement(text = "x", header = "-"),
                        members = listOf(
                                KeyElement(text = "x"),
                                KeyElement(text = "-")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterC,
                keyModel = KeyModel(
                        primary = KeyElement(text = "c", header = "+"),
                        members = listOf(
                                KeyElement(text = "c"),
                                KeyElement(text = "+")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterV,
                keyModel = KeyModel(
                        primary = KeyElement(text = "v", header = "="),
                        members = listOf(
                                KeyElement(text = "v"),
                                KeyElement(text = "=")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Left,
                virtual = VirtualInputKey.letterB,
                keyModel = KeyModel(
                        primary = KeyElement(text = "b", header = "/"),
                        members = listOf(
                                KeyElement(text = "b"),
                                KeyElement(text = "/")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterN,
                keyModel = KeyModel(
                        primary = KeyElement(text = "n", header = ";"),
                        members = listOf(
                                KeyElement(text = "n"),
                                KeyElement(text = ";")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
        EnhancedInputKey(
                side = KeySide.Right,
                virtual = VirtualInputKey.letterM,
                keyModel = KeyModel(
                        primary = KeyElement(text = "m", header = ":"),
                        members = listOf(
                                KeyElement(text = "m"),
                                KeyElement(text = ":")
                        )
                ),
                modifier = Modifier.weight(1f)
        )
}
