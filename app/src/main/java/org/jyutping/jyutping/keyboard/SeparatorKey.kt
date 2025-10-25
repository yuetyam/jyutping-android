package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.presets.PresetString
import org.jyutping.jyutping.shapes.BubbleShape
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun SeparatorKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val textColor: Color = if (isDarkMode) Color.White else Color.Black
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val shouldPreviewKey by context.previewKeyText.collectAsState()
        val density = LocalDensity.current
        var baseSize by remember { mutableStateOf(Size.Zero) }
        var isPressing by remember { mutableStateOf(false) }
        val keyShape = RoundedCornerShape(PresetConstant.keyCornerRadius.dp)
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isPressing = true
                                                context.audioFeedback(SoundEffect.Input)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isPressing = false
                                        },
                                        onTap = {
                                                context.process(PresetString.APOSTROPHE)
                                        }
                                )
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
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
                                        color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                        shape = keyShape
                                )
                                .background(
                                        color = separatorKeyBackColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey, isPressing),
                                        shape = keyShape
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Box(
                                modifier = Modifier
                                        .alpha(0.75f)
                                        .padding(bottom = 2.dp)
                                        .fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                        ) {
                                Text(
                                        text = "分隔",
                                        color = textColor,
                                        fontSize = 10.sp
                                )
                        }
                        Text(
                                text = PresetString.APOSTROPHE,
                                color = textColor,
                                fontSize = 24.sp
                        )
                }
                if (shouldPreviewKey && isPressing) {
                        val shape = BubbleShape()
                        val offsetY: Int = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus()
                        val width: Float = baseSize.width / 3F * 5F
                        val height: Float = baseSize.height * 2.5F
                        Popup(
                                alignment = Alignment.Center,
                                offset = IntOffset(x = 0, y = offsetY)
                        ) {
                                Box(
                                        modifier = modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = ToolBox.previewKeyBorderColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .background(
                                                        color = separatorKeyPreviewBackColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = PresetString.APOSTROPHE,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = textColor,
                                                fontSize = 32.sp
                                        )
                                }
                        }
                }
        }
}

private fun separatorKeyBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color = if (isHighContrastPreferred) {
        if (isDarkMode) {
                if (shouldPreviewKey.not() && isPressing) AltPresetColor.shallowDark else AltPresetColor.emphaticDark
        } else {
                if (shouldPreviewKey.not() && isPressing) AltPresetColor.shallowLight else AltPresetColor.emphaticLight
        }
} else {
        if (isDarkMode) {
                if (shouldPreviewKey.not() && isPressing) PresetColor.shallowDark else PresetColor.emphaticDark
        } else {
                if (shouldPreviewKey.not() && isPressing) PresetColor.shallowLight else PresetColor.emphaticLight
        }
}

private fun separatorKeyPreviewBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred) {
        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
} else {
        if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
}
