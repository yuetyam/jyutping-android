package org.jyutping.jyutping.keyboard

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun ShiftKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val keyboardCase = remember { context.keyboardCase }
        val drawableId: Int = when (keyboardCase.value) {
                KeyboardCase.Lowercased -> R.drawable.key_shift
                KeyboardCase.Uppercased -> R.drawable.key_shifting
                KeyboardCase.CapsLocked -> R.drawable.key_capslock
        }
        var isPressed by remember { mutableStateOf(false) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                coroutineScope {
                                        launch {
                                                awaitPointerEventScope {
                                                        while (true) {
                                                                val event = awaitPointerEvent()
                                                                when (event.type) {
                                                                        PointerEventType.Press -> {
                                                                                if (isPressed.not()) {
                                                                                        isPressed = true
                                                                                }
                                                                        }

                                                                        PointerEventType.Release -> {
                                                                                if (isPressed) {
                                                                                        isPressed = false
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                        launch {
                                                detectTapGestures(
                                                        onDoubleTap = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                                context.doubleShift()
                                                        },
                                                        onTap = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                                context.shift()
                                                        }
                                                )
                                        }
                                }
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                        if (isDarkMode.value) {
                                                if (isPressed) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressed) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = drawableId),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isDarkMode.value) Color.White else Color.Black
                        )
                }
        }
}
