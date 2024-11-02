package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import org.jyutping.jyutping.shapes.BubbleShape
import org.jyutping.jyutping.shapes.LeftHalfBubbleShape
import org.jyutping.jyutping.shapes.RightHalfBubbleShape

@Composable
fun LetterKey(letter: String, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val showLowercaseKeys = remember { context.showLowercaseKeys }
        val keyboardCase = remember { context.keyboardCase }
        val displayText: String = if (showLowercaseKeys.value && keyboardCase.value.isLowercased()) letter else letter.uppercase()
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
                                                val text: String = if (keyboardCase.value.isLowercased()) letter else letter.uppercase()
                                                context.process(text)
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
                        Text(
                                text = displayText,
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 22.sp
                        )
                }
                if (shouldPreviewKey.value && isPressing) {
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = 0, y = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus())
                        ) {
                                val shape = BubbleShape()
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = if (isDarkMode.value) Color.DarkGray else Color.LightGray,
                                                        shape = shape
                                                )
                                                .background(
                                                        color = if (isDarkMode.value) PresetColor.keyDark else PresetColor.keyLight,
                                                        shape = shape
                                                )
                                                .width((baseSize.width / 3F * 5F).dp)
                                                .height((baseSize.height * 2.5F).dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = displayText,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode.value) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}

private fun responsiveKeyColor(isDarkMode: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color {
        return if (isDarkMode) {
                if (shouldPreviewKey.not() && isPressing) PresetColor.keyDarkEmphatic else PresetColor.keyDark
        } else {
                if (shouldPreviewKey.not() && isPressing) PresetColor.keyLightEmphatic else PresetColor.keyLight
        }
}

@Composable
fun LetterLeadingEdgeKey(letter: String, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val showLowercaseKeys = remember { context.showLowercaseKeys }
        val keyboardCase = remember { context.keyboardCase }
        val displayText: String = if (showLowercaseKeys.value && keyboardCase.value.isLowercased()) letter else letter.uppercase()
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
                                                val text: String = if (keyboardCase.value.isLowercased()) letter else letter.uppercase()
                                                context.process(text)
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
                        Text(
                                text = displayText,
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 22.sp
                        )
                }
                if (shouldPreviewKey.value && isPressing) {
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = (baseSize.width / 4F * density.density).toInt(), y = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus())
                        ) {
                                val shape = RightHalfBubbleShape()
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = if (isDarkMode.value) Color.DarkGray else Color.LightGray,
                                                        shape = shape
                                                )
                                                .background(
                                                        color = if (isDarkMode.value) PresetColor.keyDark else PresetColor.keyLight,
                                                        shape = shape
                                                )
                                                .width((baseSize.width / 2F * 3F).dp)
                                                .height((baseSize.height * 2.5F).dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = displayText,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode.value) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}

@Composable
fun LetterTrailingEdgeKey(letter: String, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val showLowercaseKeys = remember { context.showLowercaseKeys }
        val keyboardCase = remember { context.keyboardCase }
        val displayText: String = if (showLowercaseKeys.value && keyboardCase.value.isLowercased()) letter else letter.uppercase()
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
                                                val text: String = if (keyboardCase.value.isLowercased()) letter else letter.uppercase()
                                                context.process(text)
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
                        Text(
                                text = displayText,
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 22.sp
                        )
                }
                if (shouldPreviewKey.value && isPressing) {
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = (baseSize.width / 4F * density.density).toInt().unaryMinus(), y = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus())
                        ) {
                                val shape = LeftHalfBubbleShape()
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = if (isDarkMode.value) Color.DarkGray else Color.LightGray,
                                                        shape = shape
                                                )
                                                .background(
                                                        color = if (isDarkMode.value) PresetColor.keyDark else PresetColor.keyLight,
                                                        shape = shape
                                                )
                                                .width((baseSize.width / 2F * 3F).dp)
                                                .height((baseSize.height * 2.5F).dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = displayText,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode.value) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}
