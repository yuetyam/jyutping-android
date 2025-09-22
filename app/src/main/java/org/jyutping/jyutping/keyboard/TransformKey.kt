package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.utilities.ToolBox

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
        val keyShape = RoundedCornerShape(PresetConstant.keyCornerRadius.dp)
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                context.audioFeedback(SoundEffect.Click)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.transformTo(destination)
                                        }
                                )
                        }
                        .padding(horizontal = keyboardInterface.keyHorizontalPadding(), vertical = keyboardInterface.keyVerticalPadding())
                        .border(
                                width = 1.dp,
                                color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                shape = keyShape
                        )
                        .background(
                                color = ToolBox.actionKeyBackColor(isDarkMode, isHighContrastPreferred, isPressing),
                                shape = keyShape
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
