package org.jyutping.jyutping.editingpanel

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun EditingPanelMoveForwardKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var longPressJob: Job? by remember { mutableStateOf(null) }
        val longPressCoroutineScope = rememberCoroutineScope()
        Column(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onLongPress = {
                                                context.audioFeedback(SoundEffect.Click)
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                isLongPressing = true
                                                longPressJob = longPressCoroutineScope.launch {
                                                        while (isActive && isLongPressing) {
                                                                delay(100) // 0.1s
                                                                context.audioFeedback(SoundEffect.Click)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.moveForward()
                                                        }
                                                }
                                        },
                                        onPress = {
                                                context.audioFeedback(SoundEffect.Click)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                                                longPressJob?.cancel()
                                                isLongPressing = false
                                                isPressing = true
                                                tryAwaitRelease()
                                                isPressing = false
                                                isLongPressing = false
                                                longPressJob?.cancel()
                                        },
                                        onTap = {
                                                context.moveForward()
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
                                shape = RoundedCornerShape(PresetConstant.largeKeyCornerRadius.dp)
                        )
                        .shadow(
                                elevation = 0.5.dp,
                                shape = RoundedCornerShape(PresetConstant.largeKeyCornerRadius.dp)
                        )
                        .background(
                                color = backgroundColor(isDarkMode, isHighContrastPreferred, isPressing)
                        )
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isDarkMode) Color.White else Color.Black
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
