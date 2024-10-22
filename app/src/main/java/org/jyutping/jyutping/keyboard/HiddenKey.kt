package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import org.jyutping.jyutping.JyutpingInputMethodService

@Composable
fun HiddenKey(event: HiddenKeyEvent, modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardCase = remember { context.keyboardCase }
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                val letter = event.letter()
                                if (letter == null) {
                                        context.backspace()
                                } else {
                                        val text: String = if (keyboardCase.value.isLowercased()) letter else letter.uppercase()
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
