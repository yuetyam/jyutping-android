package org.jyutping.jyutping.keyboard

import android.os.Build
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun ReturnKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                                } else {
                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                }
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.performReturn()
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
                                                if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        if (isBuffering) {
                                Text(
                                        text = context.returnKeyForm.value.text() ?: "return",
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = 15.sp
                                )
                        } else {
                                Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.key_return),
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                }
        }
}
