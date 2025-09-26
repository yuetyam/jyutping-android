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
import org.jyutping.jyutping.models.InputKeyEvent

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
                                val inputEvent = event.inputEvent
                                if (inputEvent == null) {
                                        context.backspace()
                                } else {
                                        context.handle(inputEvent)
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

        val inputEvent: InputKeyEvent?
                get() = when (this) {
                        LetterA -> InputKeyEvent.letterA
                        LetterL -> InputKeyEvent.letterL
                        LetterZ -> InputKeyEvent.letterZ
                        else -> null
                }
}
