package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
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
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.VirtualInputKey

@Composable
fun HiddenKey(hidden: HiddenVirtualKey, modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                val virtualInputKey = hidden.virtualInputKey
                                if (virtualInputKey == null) {
                                        context.audioFeedback(SoundEffect.Delete)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.backspace()
                                } else {
                                        context.audioFeedback(SoundEffect.Input)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.handle(virtualInputKey)
                                }
                        }
                        .fillMaxSize()
        ) {
                Color.Transparent
        }
}

enum class HiddenVirtualKey {
        LetterA,
        LetterL,
        LetterZ,
        Backspace;

        val virtualInputKey: VirtualInputKey?
                get() = when (this) {
                        LetterA -> VirtualInputKey.letterA
                        LetterL -> VirtualInputKey.letterL
                        LetterZ -> VirtualInputKey.letterZ
                        else -> null
                }
}
