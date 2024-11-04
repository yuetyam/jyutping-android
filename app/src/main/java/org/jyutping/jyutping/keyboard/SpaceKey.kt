package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun SpaceKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val keyForm by context.spaceKeyForm.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var isDragging by remember { mutableStateOf(false) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
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
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
                                                isDragging = true
                                        },
                                        onDragEnd = {
                                                isDragging = false
                                        },
                                        onDragCancel = {
                                                isDragging = false
                                        },
                                        onDrag = { change, dragAmount ->
                                                change.consume()
                                                val moveX = dragAmount.x
                                                if (moveX < -10f) {
                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.moveBackward()
                                                } else if (moveX > 10f) {
                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.moveForward()
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
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        if (isDarkMode) {
                                                if (isPressing || isDragging) PresetColor.keyDarkEmphatic else PresetColor.keyDark
                                        } else {
                                                if (isPressing || isDragging) PresetColor.keyLightEmphatic else PresetColor.keyLight
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = if (isDragging) PresetConstant.SpaceKeyLongPressHint else keyForm.text(),
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 15.sp
                        )
                }
        }
}
