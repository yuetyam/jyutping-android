package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.feedback.SoundEffect

@Composable
fun HiddenKey(event: HiddenKeyEvent, modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardCase by context.keyboardCase.collectAsState()
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                val letter = event.letter()
                                if (letter == null) {
                                        context.backspace()
                                } else {
                                        val text: String = if (keyboardCase.isLowercased()) letter else letter.uppercase()
                                        context.process(text)
                                }
                        }
                        .fillMaxSize()
        ) {
                Color.Transparent
        }
}

enum class HiddenKeyEvent {
        LetterA,
        LetterL,
        LetterZ,
        Backspace;
        fun letter(): String? = when (this) {
                LetterA -> "a"
                LetterL -> "l"
                LetterZ -> "z"
                Backspace -> null
        }
}
