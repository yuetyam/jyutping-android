package org.jyutping.jyutping.editingpanel

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun EditingPanelSpaceKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var isDragging by remember { mutableStateOf(false) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                context.audioFeedback(SoundEffect.Space)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.space()
                                        }
                                )
                        }
                        .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                                context.audioFeedback(SoundEffect.Click)
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                isDragging = true
                                        },
                                        onDragEnd = {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                                }
                                                isDragging = false
                                        },
                                        onDragCancel = {
                                                isDragging = false
                                        },
                                        onDrag = { change, dragAmount ->
                                                change.consume()
                                                val moveX = dragAmount.x
                                                if (moveX < -10f) {
                                                        context.audioFeedback(SoundEffect.Click)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.moveBackward()
                                                } else if (moveX > 10f) {
                                                        context.audioFeedback(SoundEffect.Click)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.moveForward()
                                                }
                                        }
                                )
                        }
                        .padding(4.dp)
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
                                color = backgroundColor(isDarkMode, isHighContrastPreferred, (isPressing || isDragging))
                        )
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = if (isDragging) PresetConstant.SpaceKeyLongPressHint else stringResource(id = R.string.editing_panel_key_space),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 15.sp,
                )
        }
}

private fun backgroundColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, isPressing: Boolean) =
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
