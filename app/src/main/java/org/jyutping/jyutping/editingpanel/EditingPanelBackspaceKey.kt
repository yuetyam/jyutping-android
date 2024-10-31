package org.jyutping.jyutping.editingpanel

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun EditingPanelBackspaceKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        var isPressing by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var longPressJob: Job? by remember { mutableStateOf(null) }
        val longPressCoroutineScope = rememberCoroutineScope()
        Column(
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
                        .padding(4.dp)
                        .shadow(
                                elevation = 0.5.dp,
                                shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                                if (isDarkMode.value) {
                                        if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                } else {
                                        if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                }
                        )
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Icon(
                        imageVector = ImageVector.vectorResource(id = if (isPressing) R.drawable.key_backspacing else R.drawable.key_backspace),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isDarkMode.value) Color.White else Color.Black
                )
                Text(
                        text = stringResource(id = R.string.editing_panel_key_backspace),
                        color = if (isDarkMode.value) Color.White else Color.Black,
                        fontSize = 11.sp,
                )
        }
}
