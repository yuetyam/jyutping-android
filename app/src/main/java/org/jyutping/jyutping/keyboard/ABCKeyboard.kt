package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
        val useNineKeyNumberPad by context.useNineKeyNumberPad.collectAsState()
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
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        HiddenKey(hidden = HiddenVirtualKey.LetterA, modifier = Modifier.weight(0.5f))
                        LetterKey(virtual = VirtualInputKey.letterA, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterS, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterD, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterF, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterG, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterH, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterJ, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterK, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterL, modifier = Modifier.weight(1f))
                        HiddenKey(hidden = HiddenVirtualKey.LetterL, modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        HiddenKey(hidden = HiddenVirtualKey.LetterZ, modifier = Modifier.weight(0.2f))
                        LetterKey(virtual = VirtualInputKey.letterZ, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterX, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterC, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterV, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterB, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterN, modifier = Modifier.weight(1f))
                        LetterKey(virtual = VirtualInputKey.letterM, modifier = Modifier.weight(1f))
                        HiddenKey(hidden = HiddenVirtualKey.Backspace, modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                ABCBottomKeyRow(
                        transform = if (useNineKeyNumberPad) KeyboardForm.NineKeyNumeric else KeyboardForm.Numeric,
                        height = keyHeight
                )
        }
}
