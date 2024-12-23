package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun AlphabeticKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
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
                        if (isBuffering) {
                                CandidateScrollBar()
                        } else {
                                ToolBar()
                        }
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        LetterKey(letter = "q", modifier = Modifier.weight(1f), position = Alignment.Start)
                        LetterKey(letter = "w", modifier = Modifier.weight(1f))
                        LetterKey(letter = "e", modifier = Modifier.weight(1f))
                        LetterKey(letter = "r", modifier = Modifier.weight(1f))
                        LetterKey(letter = "t", modifier = Modifier.weight(1f))
                        LetterKey(letter = "y", modifier = Modifier.weight(1f))
                        LetterKey(letter = "u", modifier = Modifier.weight(1f))
                        LetterKey(letter = "i", modifier = Modifier.weight(1f))
                        LetterKey(letter = "o", modifier = Modifier.weight(1f))
                        LetterKey(letter = "p", modifier = Modifier.weight(1f), position = Alignment.End)
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        HiddenKey(event = HiddenKeyEvent.LetterA, modifier = Modifier.weight(0.5f))
                        LetterKey(letter = "a", modifier = Modifier.weight(1f))
                        LetterKey(letter = "s", modifier = Modifier.weight(1f))
                        LetterKey(letter = "d", modifier = Modifier.weight(1f))
                        LetterKey(letter = "f", modifier = Modifier.weight(1f))
                        LetterKey(letter = "g", modifier = Modifier.weight(1f))
                        LetterKey(letter = "h", modifier = Modifier.weight(1f))
                        LetterKey(letter = "j", modifier = Modifier.weight(1f))
                        LetterKey(letter = "k", modifier = Modifier.weight(1f))
                        LetterKey(letter = "l", modifier = Modifier.weight(1f))
                        HiddenKey(event = HiddenKeyEvent.LetterL, modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        HiddenKey(event = HiddenKeyEvent.LetterZ, modifier = Modifier.weight(0.2f))
                        LetterKey(letter = "z", modifier = Modifier.weight(1f))
                        LetterKey(letter = "x", modifier = Modifier.weight(1f))
                        LetterKey(letter = "c", modifier = Modifier.weight(1f))
                        LetterKey(letter = "v", modifier = Modifier.weight(1f))
                        LetterKey(letter = "b", modifier = Modifier.weight(1f))
                        LetterKey(letter = "n", modifier = Modifier.weight(1f))
                        LetterKey(letter = "m", modifier = Modifier.weight(1f))
                        HiddenKey(event = HiddenKeyEvent.Backspace, modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(transform = KeyboardForm.Numeric, height = keyHeight)
        }
}
