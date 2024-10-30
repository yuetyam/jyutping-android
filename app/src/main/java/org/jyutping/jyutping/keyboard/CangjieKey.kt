package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.utilities.ShapeKeyMap

@Composable
fun CangjieKey(letter: Char, modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val keyboardCase = remember { context.keyboardCase }
        val keyLetter: String = if (keyboardCase.value.isLowercased()) letter.lowercase() else letter.uppercase()
        val keyRadical: Char = ShapeKeyMap.cangjieCode(letter) ?: letter
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                context.process(keyLetter)
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        if (isDarkMode.value) {
                                                if (isPressed.value) PresetColor.keyDarkEmphatic else PresetColor.keyDark
                                        } else {
                                                if (isPressed.value) PresetColor.keyLightEmphatic else PresetColor.keyLight
                                        }
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Box (
                                modifier = modifier
                                        .padding(2.dp)
                                        .fillMaxSize(),
                                contentAlignment = Alignment.TopEnd
                        ) {
                                Text(
                                        text = keyLetter,
                                        color = if (isDarkMode.value) Color.White else Color.Black,
                                        fontSize = 12.sp
                                )
                        }
                        Text(
                                text = keyRadical.toString(),
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 16.sp
                        )
                }
        }
}
