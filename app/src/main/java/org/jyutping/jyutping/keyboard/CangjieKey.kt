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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.shapes.BubbleShape
import org.jyutping.jyutping.shapes.LeftHalfBubbleShape
import org.jyutping.jyutping.shapes.RightHalfBubbleShape
import org.jyutping.jyutping.utilities.ShapeKeyMap

@Composable
fun CangjieKey(letter: Char, modifier: Modifier, position: Alignment.Horizontal = Alignment.CenterHorizontally) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val showLowercaseKeys by context.showLowercaseKeys.collectAsState()
        val keyboardCase by context.keyboardCase.collectAsState()
        val displayKeyLetter: String = if (showLowercaseKeys && keyboardCase.isLowercased()) letter.lowercase() else letter.uppercase()
        val keyRadical: Char = ShapeKeyMap.cangjieCode(letter) ?: letter
        val shouldPreviewKey by context.previewKeyText.collectAsState()
        val density = LocalDensity.current
        var baseSize by remember { mutableStateOf(Size.Zero) }
        var isPressing by remember { mutableStateOf(false) }
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
                                                val text: String = if (keyboardCase.isLowercased()) letter.lowercase() else letter.uppercase()
                                                context.process(text)
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = keyboardInterface.keyHorizontalPadding(), vertical = keyboardInterface.keyVerticalPadding())
                                .onGloballyPositioned { layoutCoordinates ->
                                        val originalSize = layoutCoordinates.size
                                        val width = originalSize.width.div(density.density)
                                        val height = originalSize.height.div(density.density)
                                        baseSize = Size(width = width, height = height)
                                }
                                .border(
                                        width = 1.dp,
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else Color.Transparent
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else Color.Transparent
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        color = keyBackgroundColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey, isPressing)
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
                                        text = displayKeyLetter,
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = 12.sp
                                )
                        }
                        Text(
                                text = keyRadical.toString(),
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 16.sp
                        )
                }
                if (shouldPreviewKey && isPressing) {
                        val shape: Shape = when (position) {
                                Alignment.Start -> RightHalfBubbleShape()
                                Alignment.End -> LeftHalfBubbleShape()
                                else -> BubbleShape()
                        }
                        val offsetX: Int = when (position) {
                                Alignment.Start -> (baseSize.width / 4F * density.density).toInt()
                                Alignment.End -> (baseSize.width / 4F * density.density).toInt().unaryMinus()
                                else -> 0
                        }
                        val offsetY: Int = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus()
                        val width: Float = when (position) {
                                Alignment.Start -> baseSize.width / 2F * 3F
                                Alignment.End -> baseSize.width / 2F * 3F
                                else -> baseSize.width / 3F * 5F
                        }
                        val height: Float = baseSize.height * 2.5F
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = offsetX, y = offsetY)
                        ) {
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = if (isDarkMode) {
                                                                if (isHighContrastPreferred) Color.White else Color.DarkGray
                                                        } else {
                                                                if (isHighContrastPreferred) Color.Black else Color.LightGray
                                                        },
                                                        shape = shape
                                                )
                                                .background(
                                                        color = if (isDarkMode) {
                                                                if (isHighContrastPreferred) AltPresetColor.keyDark else PresetColor.keyDark
                                                        } else {
                                                                if (isHighContrastPreferred) AltPresetColor.keyLight else PresetColor.keyLight
                                                        },
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = keyRadical.toString(),
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}

private fun keyBackgroundColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color =
        if (isDarkMode) {
                if (isHighContrastPreferred) {
                        if (shouldPreviewKey.not() && isPressing) AltPresetColor.keyDarkEmphatic else AltPresetColor.keyDark
                } else {
                        if (shouldPreviewKey.not() && isPressing) PresetColor.keyDarkEmphatic else PresetColor.keyDark
                }
        } else {
                if (isHighContrastPreferred) {
                        if (shouldPreviewKey.not() && isPressing) AltPresetColor.keyLightEmphatic else AltPresetColor.keyLight
                } else {
                        if (shouldPreviewKey.not() && isPressing) PresetColor.keyLightEmphatic else PresetColor.keyLight
                }
        }
