package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyModel
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.shapes.BubbleShape
import org.jyutping.jyutping.shapes.ExpansiveBubbleShape
import org.jyutping.jyutping.utilities.ToolBox
import kotlin.math.max
import kotlin.math.min

@Composable
fun EnhancedBottomInputKey(
        side: KeySide,
        keyModel: KeyModel,
        modifier: Modifier
) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardInterface by context.keyboardInterface.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val shouldPreviewKey by context.previewKeyText.collectAsState()
        val density = LocalDensity.current
        var baseSize by remember { mutableStateOf(Size.Zero) }
        val keyShape = RoundedCornerShape(PresetConstant.keyCornerRadius.dp)
        var isTouching by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var isSelected by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableIntStateOf(0) }
        var distance by remember { mutableFloatStateOf(0F) }
        Box(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onPress = {
                                                isTouching = true
                                                context.audioFeedback(SoundEffect.Input)
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                tryAwaitRelease()
                                                isTouching = false
                                        },
                                        onTap = {
                                                if (isSelected) {
                                                        isSelected = false
                                                } else {
                                                        context.input(keyModel.primary.text)
                                                }
                                        }
                                )
                        }
                        .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                                isLongPressing = true
                                        },
                                        onDragEnd = {
                                                keyModel.members.getOrNull(selectedIndex)?.let { element ->
                                                        context.input(element.text)
                                                }
                                                selectedIndex = 0
                                                distance = 0F
                                                isLongPressing = false
                                        },
                                        onDragCancel = {
                                                selectedIndex = 0
                                                distance = 0F
                                                isLongPressing = false
                                        },
                                        onDrag = { change, dragAmount ->
                                                change.consume()
                                                val horizontalAmount: Float = if (side.isLeft) dragAmount.x else dragAmount.x.unaryMinus()
                                                distance += horizontalAmount
                                                val baseWidth = baseSize.width * density.density
                                                if (distance < (baseWidth / 2F)) {
                                                        if (selectedIndex != 0) {
                                                                selectedIndex = 0
                                                        }
                                                } else {
                                                        val memberCount = keyModel.members.size
                                                        val maxPoint = baseWidth * memberCount
                                                        val endIndex = memberCount - 1
                                                        val index = memberCount - ((maxPoint - distance) / baseWidth).toInt()
                                                        val newSelectedIndex = min(endIndex, max(0, index))
                                                        if (selectedIndex != newSelectedIndex) {
                                                                selectedIndex = newSelectedIndex
                                                                isSelected = true
                                                        }
                                                }
                                        },
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
                                        color = bottomKeyBackColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey, isTouching),
                                        shape = keyShape
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        keyModel.primary.header?.let { header ->
                                Box(
                                        modifier = Modifier
                                                .alpha(0.75f)
                                                .padding(top = 1.dp, end = 2.dp)
                                                .fillMaxSize(),
                                        contentAlignment = Alignment.TopEnd
                                ) {
                                        Text(
                                                text = header,
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 10.sp
                                        )
                                }
                        }
                        keyModel.primary.footer?.let { footer ->
                                Box(
                                        modifier = Modifier
                                                .alpha(0.75f)
                                                .padding(bottom = 1.dp, end = 2.dp)
                                                .fillMaxSize(),
                                        contentAlignment = Alignment.BottomEnd
                                ) {
                                        Text(
                                                text = footer,
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 10.sp
                                        )
                                }
                        }
                        Text(
                                text = keyModel.primary.text,
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = if (keyModel.primary.isTextSingular) 24.sp else 18.sp
                        )
                }
                if (shouldPreviewKey && isTouching) {
                        val shape: Shape = BubbleShape()
                        val width: Float = baseSize.width / 3F * 5F
                        val height: Float = baseSize.height * 2.5F
                        val offsetY: Int = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus()
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
                                                        color = bottomKeyPreviewBackColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = keyModel.primary.text,
                                                modifier = Modifier.padding(bottom = (baseSize.height * 1.3F).dp),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = if (keyModel.primary.isTextSingular) 32.sp else 22.sp
                                        )
                                }
                        }
                }
                if (isLongPressing) {
                        val expansionCount = keyModel.members.size - 1
                        val shape: Shape = ExpansiveBubbleShape(side = side, expansionCount = expansionCount)
                        val adjustWidth = (baseSize.width * expansionCount / 2F * density.density).toInt()
                        val offsetX: Int = if (side.isLeft) adjustWidth else adjustWidth.unaryMinus()
                        val offsetY: Int = (baseSize.height * 1.5F / 2F * density.density).toInt().unaryMinus()
                        val width: Float = (baseSize.width / 3F * 5F) + (baseSize.width * expansionCount)
                        val height: Float = baseSize.height * 2.5F
                        val memberIndices = if (side.isLeft) keyModel.members.indices else keyModel.members.indices.reversed()
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
                                                        color = bottomKeyPreviewBackColor(isDarkMode, isHighContrastPreferred),
                                                        shape = shape
                                                )
                                                .width(width.dp)
                                                .height(height.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Row(
                                                modifier = Modifier
                                                        .padding(
                                                                bottom = (baseSize.height * 1.3F).dp,
                                                                start = (baseSize.width / 3F).dp,
                                                                end = (baseSize.width / 3F).dp,
                                                        ),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                                for (index in memberIndices) {
                                                        Box(
                                                                modifier = modifier
                                                                        .background(
                                                                                color = if (index == selectedIndex) PresetColor.blue else Color.Transparent,
                                                                                shape = keyShape
                                                                        )
                                                                        .height(baseSize.height.dp)
                                                                        .fillMaxWidth(),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                keyModel.members.getOrNull(index)?.let { element ->
                                                                        element.header?.let { header ->
                                                                                Box(
                                                                                        modifier = Modifier
                                                                                                .alpha(0.75f)
                                                                                                .fillMaxSize(),
                                                                                        contentAlignment = Alignment.TopCenter
                                                                                ) {
                                                                                        Text(
                                                                                                text = header,
                                                                                                color = if (isDarkMode || index == selectedIndex) Color.White else Color.Black,
                                                                                                fontSize = 9.sp
                                                                                        )
                                                                                }
                                                                        }
                                                                        element.footer?.let { footer ->
                                                                                Box(
                                                                                        modifier = Modifier
                                                                                                .alpha(0.75f)
                                                                                                .fillMaxSize(),
                                                                                        contentAlignment = Alignment.BottomCenter
                                                                                ) {
                                                                                        Text(
                                                                                                text = footer,
                                                                                                color = if (isDarkMode || index == selectedIndex) Color.White else Color.Black,
                                                                                                fontSize = 9.sp
                                                                                        )
                                                                                }
                                                                        }
                                                                        Text(
                                                                                text = element.text,
                                                                                color = if (isDarkMode || index == selectedIndex) Color.White else Color.Black,
                                                                                fontSize = if (element.isTextSingular) 24.sp else 20.sp
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

private fun bottomKeyBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, shouldPreviewKey: Boolean, isPressing: Boolean): Color = if (isHighContrastPreferred) {
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

private fun bottomKeyPreviewBackColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean): Color = if (isHighContrastPreferred) {
        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
} else {
        if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
}
