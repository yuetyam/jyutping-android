package org.jyutping.jyutping.ninekey

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun NineKeySpecialKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        val keyShape = RoundedCornerShape(PresetConstant.largeKeyCornerRadius.dp)
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                context.audioFeedback(SoundEffect.Input)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                if (isBuffering.negative) {
                                                        context.nineKeyProcess(Combo.Special)
                                                }
                                                try {
                                                        tryAwaitRelease()
                                                } finally {
                                                        isPressing = false
                                                }
                                        }
                                )
                        }
                        .padding(3.dp)
                        .border(
                                width = 1.dp,
                                color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                shape = keyShape
                        )
                        .background(
                                color = ToolBox.inputKeyBackColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey = false, isPressing = isPressing),
                                shape = keyShape
                        )
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(

                        modifier = Modifier
                                .alpha(if (isBuffering) 0f else 0.35f)
                                .padding(bottom = 2.dp)
                                .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                ) {
                        Text(
                                text = "反查",
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 10.sp,
                        )
                }
                Text(
                        text = Combo.Special.text,
                        modifier = Modifier.alpha(if (isBuffering) 0f else 1f),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 16.sp,
                )
        }
}
