package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.keyLight
import org.jyutping.jyutping.extensions.keyLightEmphatic

@Composable
fun BackspaceKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        var isLongPressed by remember { mutableStateOf(false) }
        var taskJob: Job? by remember { mutableStateOf(null) }
        val scope = rememberCoroutineScope()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        var offsetX by remember { mutableFloatStateOf(0f) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                coroutineScope {
                                        launch {
                                                detectTapGestures(
                                                        onLongPress = {
                                                                isLongPressed = true
                                                                taskJob = scope.launch {
                                                                        while (isLongPressed) {
                                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                                context.backspace()
                                                                                delay(100)
                                                                        }
                                                                }
                                                        },
                                                        onPress = {
                                                                tryAwaitRelease()
                                                                isLongPressed = false
                                                                taskJob?.cancel()
                                                        },
                                                        onTap = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.backspace()
                                                        }
                                                )
                                        }
                                        launch {
                                                detectHorizontalDragGestures { change, dragAmount ->
                                                        change.consume()
                                                        offsetX += dragAmount
                                                        if (offsetX < -44f) {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.clearBuffer()
                                                        }
                                                }
                                        }
                                }
                        }
                        .fillMaxWidth()
                        .fillMaxHeight(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPressed.value) Color.keyLight else Color.keyLightEmphatic)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = if (isPressed.value) R.drawable.key_backspacing else R.drawable.key_backspace),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                        )
                }
        }
}
