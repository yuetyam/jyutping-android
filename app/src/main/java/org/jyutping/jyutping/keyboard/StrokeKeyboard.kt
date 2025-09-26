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
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun StrokeKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val useTenKeyNumberPad by context.useTenKeyNumberPad.collectAsState()
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
                        StrokeKey(event = InputKeyEvent.letterQ, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterW, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterE, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterR, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterT, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterY, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterU, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterI, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterO, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterP, modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        StrokeKey(event = InputKeyEvent.letterA, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterS, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterD, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterF, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterG, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterH, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterJ, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterK, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterL, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        StrokeKey(event = InputKeyEvent.letterZ, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterX, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterC, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterV, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterB, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterN, modifier = Modifier.weight(1f))
                        StrokeKey(event = InputKeyEvent.letterM, modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                BottomKeyRow(
                        transform = if (useTenKeyNumberPad) KeyboardForm.TenKeyNumeric else KeyboardForm.Numeric,
                        height = keyHeight
                )
        }
}
