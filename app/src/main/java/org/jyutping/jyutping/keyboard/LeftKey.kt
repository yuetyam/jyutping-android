package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.shapes.BubbleShape

@Composable
fun LeftKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val inputMethodMode = remember { context.inputMethodMode }
        val isBuffering = remember { context.isBuffering }
        val isDarkMode = remember { context.isDarkMode }
        val keyForm: LeftKeyForm = when (inputMethodMode.value) {
                InputMethodMode.Cantonese -> if (isBuffering.value) LeftKeyForm.Buffering else LeftKeyForm.Cantonese
                InputMethodMode.ABC -> LeftKeyForm.ABC
        }
        var isPressing by remember { mutableStateOf(false) }
        val shouldPreviewKey = remember { context.previewKeyText }
        val density = LocalDensity.current
        var baseSize by remember { mutableStateOf(Size.Zero) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.leftKey()
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .onGloballyPositioned { layoutCoordinates ->
                                        val originalSize = layoutCoordinates.size
                                        val width = originalSize.width.div(density.density)
                                        val height = originalSize.height.div(density.density)
                                        baseSize = Size(width = width, height = height)
                                }
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        color = responsiveKeyColor(isDarkMode.value, shouldPreviewKey.value, isPressing)
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
                if (shouldPreviewKey.value && isPressing) {
                        val shape = BubbleShape()
                        val offsetX: Int = 0
                        val offsetY: Int = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus()
                        val width: Float = baseSize.width / 3F * 5F
                        val height: Float = baseSize.height * 2.5F
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = offsetX, y = offsetY)
                        ) {
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = if (isDarkMode.value) Color.DarkGray else Color.LightGray,
                                                        shape = shape
                                                )
                                                .background(
                                                        color = if (isDarkMode.value) PresetColor.keyDarkEmphatic else PresetColor.keyLightEmphatic,
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = keyForm.keyText(),
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode.value) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}

private enum class LeftKeyForm {
        Cantonese,
        Buffering,
        ABC
}
private fun LeftKeyForm.isBuffering(): Boolean = (this == LeftKeyForm.Buffering)
private fun LeftKeyForm.keyText(): String = when (this) {
        LeftKeyForm.Cantonese -> "，"
        LeftKeyForm.Buffering -> PresetString.SEPARATOR
        LeftKeyForm.ABC -> ","
}

private fun responsiveKeyColor(isDarkMode: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color {
        return if (isDarkMode) {
                if (shouldPreviewKey.not() && isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
        } else {
                if (shouldPreviewKey.not() && isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
        }
}
