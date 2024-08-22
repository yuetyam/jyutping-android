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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.keyboardLightBackground
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun CangjieKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering = remember { context.isBuffering }
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.keyboardLightBackground)
        ) {
                Box(
                        modifier = Modifier
                                .height(PresetConstant.ToolBarHeight.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        Color.keyboardLightBackground
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
                        CangjieKey(letter = 'q', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'w', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'e', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'r', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 't', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'y', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'u', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'i', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'o', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'p', modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        CangjieKey(letter = 'a', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 's', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'd', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'f', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'g', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'h', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'j', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'k', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'l', modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        CangjieKey(letter = 'z', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'x', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'c', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'v', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'b', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'n', modifier = Modifier.weight(1f))
                        CangjieKey(letter = 'm', modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        TransformKey(destination = KeyboardForm.Numeric, modifier = Modifier.weight(2f))
                        LeftKey(modifier = Modifier.weight(1f))
                        SpaceKey(modifier = Modifier.weight(4f))
                        RightKey(modifier = Modifier.weight(1f))
                        ReturnKey(modifier = Modifier.weight(2f))
                }
        }
}
