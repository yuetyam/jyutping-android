package org.jyutping.jyutping.keyboard

import android.os.Build
import android.view.HapticFeedbackConstants
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun BackspaceKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var longPressJob: Job? by remember { mutableStateOf(null) }
        val longPressCoroutineScope = rememberCoroutineScope()
        var isDraggable by remember { mutableStateOf(true) }
        var isDragging by remember { mutableStateOf(false) }
        val keyShape = RoundedCornerShape(PresetConstant.keyCornerRadius.dp)
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onLongPress = {
                                                context.audioFeedback(SoundEffect.Delete)
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                isLongPressing = true
                                                longPressJob = longPressCoroutineScope.launch {
                                                        while (isActive && isLongPressing) {
                                                                delay(100)
                                                                context.audioFeedback(SoundEffect.Delete)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.backspace()
                                                        }
                                                }
                                        },
                                        onPress = {
                                                context.audioFeedback(SoundEffect.Delete)
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
                                                                context.audioFeedback(SoundEffect.Delete)
                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                                                } else {
                                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
                                                                }
                                                                context.clearBuffer()
                                                                isDraggable = false
                                                        }
                                                }
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
                                color = ToolBox.actionKeyBackColor(isDarkMode, isHighContrastPreferred, (isPressing || isDragging)),
                                shape = keyShape
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
