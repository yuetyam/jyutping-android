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
import org.jyutping.jyutping.models.InputKeyEvent
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.shapes.BubbleShape
import org.jyutping.jyutping.utilities.ShapeKeyMap
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun StrokeKey(event: InputKeyEvent, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val keyboardCase by context.keyboardCase.collectAsState()
        val showLowercaseKeys by context.showLowercaseKeys.collectAsState()
        val shouldPreviewKey by context.previewKeyText.collectAsState()
        val displayKeyLetter: String = if (showLowercaseKeys && keyboardCase.isLowercased) event.text else event.text.uppercase()
        val keyStroke: String? = ShapeKeyMap.keyStroke(event.text)
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
                                                context.handle(event)
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
                                        color = ToolBox.inputKeyBackColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey, isPressing),
                                        shape = keyShape
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        if (keyStroke != null) {
                                Box(
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
                                        text = keyStroke,
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = 16.sp
                                )
                        } else {
                                Text(
                                        text = displayKeyLetter,
                                        modifier = Modifier.alpha(0.66f),
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = 18.sp
                                )
                        }
                }
                if ((keyStroke != null) && shouldPreviewKey && isPressing) {
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
                                                        color = ToolBox.previewKeyBorderColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .background(
                                                        color = ToolBox.previewInputKeyBackColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = keyStroke,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                style = MaterialTheme.typography.headlineLarge
                                        )
                                }
                        }
                }
        }
}
