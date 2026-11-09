package org.jyutping.jyutping.ninekey

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.negative
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.keyboard.CangjieVariant
import org.jyutping.jyutping.models.KeySide
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.shapes.ExpansiveLargeBubbleShape
import org.jyutping.jyutping.utilities.ToolBox
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds

/** Advertised reverse-lookup triggers in display order. */
private val triggers: List<VirtualInputKey> = listOf(VirtualInputKey.letterR, VirtualInputKey.letterV, VirtualInputKey.letterX, VirtualInputKey.letterQ)

/**
 * Returns the reverse-lookup method label displayed beneath the trigger in the expanded selector.
 * @param cangjieVariant Determines whether V is labeled as Cangjie or Quick.
 */
private fun VirtualInputKey.lookupFootnote(cangjieVariant: CangjieVariant): String = when (this) {
        VirtualInputKey.letterR -> "普拼"
        VirtualInputKey.letterV -> when (cangjieVariant) {
                CangjieVariant.Cangjie5, CangjieVariant.Cangjie3 -> "倉頡"
                CangjieVariant.Quick5, CangjieVariant.Quick3 -> "速成"
        }
        VirtualInputKey.letterX -> "筆畫"
        VirtualInputKey.letterQ -> "拆字"
        else -> " "
}

/**
 * A nine-key reverse-lookup key with a long-press method selector.
 *
 * An ordinary release dispatches the default reverse-lookup trigger. Holding expands the method selector;
 * sliding horizontally changes the selection and releasing dispatches it.
 * While buffering, expansion is disabled and release routes through the apostrophe handler.
 */
@Composable
fun SpecialKey(modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val cangjieVariant by context.cangjieVariant.collectAsState()
        val density = LocalDensity.current
        val keyShape = RoundedCornerShape(PresetConstant.largeKeyCornerRadius.dp)
        val shapeInset = 3.dp
        val extraHeight = 4.dp
        val gap = 3.dp
        var baseSize by remember { mutableStateOf(Size.Zero) }
        var isTouching by remember { mutableStateOf(false) }
        var isLongPressing by remember { mutableStateOf(false) }
        var selectedIndex by remember { mutableIntStateOf(0) }
        var distance by remember { mutableFloatStateOf(0F) }
        LaunchedEffect(isTouching) {
                if (isTouching && isBuffering.negative) {
                        delay(300L.milliseconds)
                        if (isTouching && isBuffering.negative) {
                                isLongPressing = true
                        }
                }
        }
        Box(
                modifier = modifier
                        .zIndex(if (isLongPressing) 1F else 0F)
                        .pointerInput(Unit) {
                                awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        isTouching = true
                                        distance = 0F
                                        context.audioFeedback(SoundEffect.Input)
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        try {
                                                while (true) {
                                                        val event = awaitPointerEvent()
                                                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                                                        if (change.changedToUp()) {
                                                                if (isLongPressing) {
                                                                        context.audioFeedback(SoundEffect.Input)
                                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                                        val virtual = triggers.getOrNull(selectedIndex)
                                                                        context.nineKeyProcess(virtual)
                                                                } else if (isBuffering) {
                                                                        context.nineKeyProcess(VirtualInputKey.apostrophe)
                                                                } else {
                                                                        context.nineKeyProcess()
                                                                }
                                                                break
                                                        }
                                                        val dragAmount: Offset = change.positionChange()
                                                        if (dragAmount != Offset.Zero) {
                                                                change.consume()
                                                                if (isLongPressing) {
                                                                        distance += dragAmount.x
                                                                        val baseWidth: Float = baseSize.width
                                                                        val memberCount: Int = triggers.size
                                                                        if (memberCount > 1 && baseWidth > 0F) {
                                                                                if (distance < (baseWidth / 2F)) {
                                                                                        if (selectedIndex != 0) {
                                                                                                selectedIndex = 0
                                                                                        }
                                                                                } else {
                                                                                        val maxPoint: Float = baseWidth * memberCount
                                                                                        val endIndex: Int = memberCount - 1
                                                                                        val index: Int = memberCount - ((maxPoint - distance) / baseWidth).toInt()
                                                                                        val newSelectedIndex: Int = min(endIndex, max(0, index))
                                                                                        if (selectedIndex != newSelectedIndex) {
                                                                                                selectedIndex = newSelectedIndex
                                                                                        }
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                }
                                        } finally {
                                                selectedIndex = 0
                                                distance = 0F
                                                isLongPressing = false
                                                isTouching = false
                                        }
                                }
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(shapeInset)
                                .onGloballyPositioned { layoutCoordinates ->
                                        val size = layoutCoordinates.size
                                        baseSize = Size(width = size.width.toFloat(), height = size.height.toFloat())
                                }
                )
                if (isLongPressing) {
                        val memberCount: Int = triggers.size
                        val expansionCount: Int = memberCount - 1
                        val shape: Shape = ExpansiveLargeBubbleShape(side = KeySide.Left, expansionCount = expansionCount)
                        val extraHeightPx: Float = with(density) { extraHeight.toPx() }
                        val offsetX: Int = (baseSize.width * expansionCount / 2F).toInt()
                        val offsetY: Int = ((baseSize.height + extraHeightPx) / 2F).toInt().unaryMinus()
                        val width = with(density) { (baseSize.width * memberCount).toDp() }
                        val height = with(density) { (baseSize.height * 2F + extraHeightPx).toDp() }
                        val rowHeight = with(density) { baseSize.height.toDp() }
                        Box(
                                modifier = Modifier
                                        .offset { IntOffset(offsetX, offsetY) }
                                        .requiredWidth(width)
                                        .requiredHeight(height)
                                        .border(
                                                width = 1.dp,
                                                color = ToolBox.previewKeyBorderColor(isDarkMode, isHighContrastPreferred),
                                                shape = shape
                                        )
                                        .background(
                                                color = ToolBox.previewInputKeyBackColor(isDarkMode, isHighContrastPreferred),
                                                shape = shape
                                        ),
                                contentAlignment = Alignment.TopCenter
                        ) {
                                Row(
                                        modifier = Modifier
                                                .requiredWidth(width)
                                                .requiredHeight(rowHeight),
                                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                        triggers.forEachIndexed { index, element ->
                                                val isSelected: Boolean = (index == selectedIndex)
                                                val textColor: Color = if (isDarkMode || isSelected) Color.White else Color.Black
                                                Box(
                                                        modifier = Modifier
                                                                .weight(1F)
                                                                .fillMaxHeight()
                                                                .padding(gap)
                                                                .background(
                                                                        color = if (isSelected) PresetColor.blue else Color.Transparent,
                                                                        shape = RoundedCornerShape(PresetConstant.largeKeyCornerRadius.dp - gap)
                                                                ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Column(
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                                Text(
                                                                        text = element.text.uppercase(),
                                                                        color = textColor,
                                                                        fontSize = 22.sp
                                                                )
                                                                Text(
                                                                        text = element.lookupFootnote(cangjieVariant),
                                                                        modifier = Modifier.alpha(0.75f),
                                                                        color = textColor,
                                                                        fontSize = 10.sp
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                } else {
                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(if (isTouching) 1.dp else shapeInset)
                                        .border(
                                                width = 1.dp,
                                                color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                                shape = keyShape
                                        )
                                        .background(
                                                color = ToolBox.inputKeyBackColor(isDarkMode, isHighContrastPreferred, shouldPreviewKey = false, isPressing = isTouching),
                                                shape = keyShape
                                        ),
                                contentAlignment = Alignment.Center
                        ) {
                                Box(
                                        modifier = Modifier
                                                .alpha(if (isBuffering) 0f else 0.35f)
                                                .padding(bottom = 2.dp)
                                                .fillMaxSize(),
                                        contentAlignment = Alignment.BottomCenter
                                ) {
                                        Text(
                                                text = "反查",
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 10.sp,
                                        )
                                }
                                Row(
                                        modifier = Modifier.alpha(if (isBuffering) 0f else 1f),
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                        Text(
                                                text = "R",
                                                modifier = Modifier.alignByBaseline(),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 16.sp,
                                        )
                                        Text(
                                                text = "VXQ",
                                                modifier = Modifier.alignByBaseline(),
                                                color = if (isDarkMode) Color.White else Color.Black,
                                                fontSize = 10.sp,
                                        )
                                }
                        }
                }
        }
}
