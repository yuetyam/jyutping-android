package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import org.jyutping.jyutping.JyutpingInputMethodService

@Composable
fun KeyboardScreen(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering = remember { context.isBuffering }
        Column(
                modifier = Modifier
                        .background(Color(0xFFD6D8DD))
                        .fillMaxWidth()
        ) {
                if (isBuffering.value) {
                        CandidateScrollBar()
                } else {
                        ToolBar()
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        LetterKey(letter = "q", modifier = Modifier.weight(1f))
                        LetterKey(letter = "w", modifier = Modifier.weight(1f))
                        LetterKey(letter = "e", modifier = Modifier.weight(1f))
                        LetterKey(letter = "r", modifier = Modifier.weight(1f))
                        LetterKey(letter = "t", modifier = Modifier.weight(1f))
                        LetterKey(letter = "y", modifier = Modifier.weight(1f))
                        LetterKey(letter = "u", modifier = Modifier.weight(1f))
                        LetterKey(letter = "i", modifier = Modifier.weight(1f))
                        LetterKey(letter = "o", modifier = Modifier.weight(1f))
                        LetterKey(letter = "p", modifier = Modifier.weight(1f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        LetterKey(letter = "a", modifier = Modifier.weight(1f))
                        LetterKey(letter = "s", modifier = Modifier.weight(1f))
                        LetterKey(letter = "d", modifier = Modifier.weight(1f))
                        LetterKey(letter = "f", modifier = Modifier.weight(1f))
                        LetterKey(letter = "g", modifier = Modifier.weight(1f))
                        LetterKey(letter = "h", modifier = Modifier.weight(1f))
                        LetterKey(letter = "j", modifier = Modifier.weight(1f))
                        LetterKey(letter = "k", modifier = Modifier.weight(1f))
                        LetterKey(letter = "l", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.5f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        ShiftKey(modifier = Modifier.weight(1.3f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        LetterKey(letter = "z", modifier = Modifier.weight(1f))
                        LetterKey(letter = "x", modifier = Modifier.weight(1f))
                        LetterKey(letter = "c", modifier = Modifier.weight(1f))
                        LetterKey(letter = "v", modifier = Modifier.weight(1f))
                        LetterKey(letter = "b", modifier = Modifier.weight(1f))
                        LetterKey(letter = "n", modifier = Modifier.weight(1f))
                        LetterKey(letter = "m", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.weight(0.2f))
                        BackspaceKey(modifier = Modifier.weight(1.3f))
                }
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(keyHeight)
                ) {
                        TransformKey(modifier = Modifier.weight(2f))
                        LeftKey(modifier = Modifier.weight(1f))
                        SpaceKey(modifier = Modifier.weight(4f))
                        RightKey(modifier = Modifier.weight(1f))
                        ReturnKey(modifier = Modifier.weight(2f))
                }
        }
}
