package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetString

@Composable
fun RightKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val inputMethodMode = remember { context.inputMethodMode }
        val isBuffering = remember { context.isBuffering }
        val isDarkMode = remember { context.isDarkMode }
        val keyForm: RightKeyForm = when (inputMethodMode.value) {
                InputMethodMode.Cantonese -> if (isBuffering.value) RightKeyForm.Buffering else RightKeyForm.Cantonese
                InputMethodMode.ABC -> RightKeyForm.ABC
        }
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                context.rightKey()
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
                                                if (isPressed.value) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressed.value) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        if (keyForm.isBuffering()) {
                                Column(
                                        verticalArrangement = Arrangement.Bottom,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                                text = "分隔",
                                                modifier = Modifier
                                                        .alpha(0.85f)
                                                        .padding(bottom = 2.dp),
                                                color = if (isDarkMode.value) Color.White else Color.Black,
                                                fontSize = 10.sp
                                        )
                                }
                        }
                        Text(
                                text = keyForm.keyText(),
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 20.sp
                        )
                }
        }
}

private enum class RightKeyForm {
        Cantonese,
        Buffering,
        ABC
}
private fun RightKeyForm.isBuffering(): Boolean = (this == RightKeyForm.Buffering)
private fun RightKeyForm.keyText(): String = when (this) {
        RightKeyForm.Cantonese -> "。"
        RightKeyForm.Buffering -> PresetString.SEPARATOR
        RightKeyForm.ABC -> "."
}
