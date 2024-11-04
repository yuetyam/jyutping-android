package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun BackspaceKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var longPressJob: Job? by remember { mutableStateOf(null) }
        val longPressCoroutineScope = rememberCoroutineScope()
        var isDraggable by remember { mutableStateOf(true) }
        var isDragging by remember { mutableStateOf(false) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onLongPress = {
                                                isLongPressing = true
                                                longPressJob = longPressCoroutineScope.launch {
                                                        while (isLongPressing) {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.backspace()
                                                                delay(100)
                                                        }
                                                }
                                        },
                                        onPress = {
                                                longPressJob?.cancel()
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                                                tryAwaitRelease()
                                                isPressing = false
                                                isLongPressing = false
                                                longPressJob?.cancel()
                                        },
                                        onTap = {
                                                context.backspace()
                                        }
                                )
                        }
                        .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                        onDragStart = {
                                                isDraggable = true
                                                isDragging = true
                                        },
                                        onDragEnd = {
                                                isDraggable = true
                                                isDragging = false
                                        },
                                        onDragCancel = {
                                                isDraggable = true
                                                isDragging = false
                                        },
                                        onHorizontalDrag = { change, dragAmount ->
                                                change.consume()
                                                if (isDraggable) {
                                                        val offsetX = change.position.x - change.previousPosition.x
                                                        if (offsetX < -20f) {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
                                                                context.clearBuffer()
                                                                isDraggable = false
                                                        }
                                                }
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
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
                        Icon(
                                imageVector = ImageVector.vectorResource(id = if (isPressing || isDragging) R.drawable.key_backspacing else R.drawable.key_backspace),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
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
