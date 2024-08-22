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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.keyLight
import org.jyutping.jyutping.extensions.keyLightEmphatic
import org.jyutping.jyutping.utilities.ShapeKeyMap

@Composable
fun StrokeKey(letter: Char, modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardCase = remember { context.keyboardCase }
        val keyLetter: String = if (keyboardCase.value.isLowercased()) letter.lowercase() else letter.uppercase()
        val keyStroke: Char? = ShapeKeyMap.strokeCode(letter)
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.process(keyLetter)
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPressed.value) Color.keyLightEmphatic else Color.keyLight)
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        if (keyStroke != null) {
                                Box (
                                        modifier = modifier
                                                .padding(2.dp)
                                                .fillMaxSize(),
                                        contentAlignment = Alignment.TopEnd
                                ) {
                                        Text(
                                                text = keyLetter,
                                                fontSize = 12.sp
                                        )
                                }
                                Text(
                                        text = keyStroke.toString(),
                                        fontSize = 16.sp
                                )
                        } else {
                                Text(
                                        text = keyLetter,
                                        color = Color.Gray,
                                        fontSize = 18.sp
                                )
                        }
                }
        }
}
