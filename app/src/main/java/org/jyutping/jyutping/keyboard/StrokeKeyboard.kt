package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun StrokeKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering = remember { context.isBuffering }
        val isDarkMode = remember { context.isDarkMode }
        val needsInputModeSwitchKey = remember { context.needsInputModeSwitchKey }
        Column(
                modifier = Modifier
                        .background(if (isDarkMode.value) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
                                .height(PresetConstant.ToolBarHeight.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        if (isDarkMode.value) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground
                        if (isBuffering.value) {
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
                        StrokeKey(letter = 'q', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'w', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'e', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'r', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 't', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'y', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'u', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'i', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'o', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'p', modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        StrokeKey(letter = 'a', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 's', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'd', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'f', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'g', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'h', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'j', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'k', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'l', modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        StrokeKey(letter = 'z', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'x', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'c', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'v', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'b', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'n', modifier = Modifier.weight(1f))
                        StrokeKey(letter = 'm', modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        TransformKey(destination = KeyboardForm.Numeric, modifier = Modifier.weight(2f))
                        if (needsInputModeSwitchKey.value) {
                                GlobeKey(modifier = Modifier.weight(1f))
                        } else {
                                LeftKey(modifier = Modifier.weight(1f))
                        }
                        SpaceKey(modifier = Modifier.weight(4f))
                        RightKey(modifier = Modifier.weight(1f))
                        ReturnKey(modifier = Modifier.weight(2f))
                }
        }
}
