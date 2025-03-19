package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun TransformKey(destination: KeyboardForm, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        val keyText: String = when (destination) {
                KeyboardForm.Alphabetic -> "ABC"
                KeyboardForm.TenKeyNumeric -> "123"
                KeyboardForm.Numeric -> "123"
                KeyboardForm.Symbolic -> "#+="
                else -> "???"
        }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.transformTo(destination)
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = modifier
                                .padding(horizontal = keyboardInterface.keyHorizontalPadding(), vertical = keyboardInterface.keyVerticalPadding())
                                .border(
                                        width = 1.dp,
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else Color.Transparent
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else Color.Transparent
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        color = backgroundColor(isDarkMode, isHighContrastPreferred, isPressing)
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = keyText,
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 18.sp,
                        )
                }
        }
}

private fun backgroundColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, isPressing: Boolean): Color =
        if (isDarkMode) {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyDark else AltPresetColor.keyDarkEmphatic
                } else {
                        if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                }
        } else {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyLight else AltPresetColor.keyLightEmphatic
                } else {
                        if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                }
        }
