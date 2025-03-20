package org.jyutping.jyutping.tenkey

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun TenKeySymbolSidebar(unitHeight: Dp, modifier: Modifier) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        var isPressing by remember { mutableStateOf(false) }
        var pressingIndex by remember { mutableIntStateOf(-1) }
        var pressingState by remember { mutableIntStateOf(0) }
        val state = rememberLazyListState()
        LaunchedEffect(pressingState) {
                state.animateScrollToItem(index = 0, scrollOffset = 0)
        }
        val symbols: List<String> = listOf("+", "-", "*", "/", "%", "=", ":", "@", "#", "~", "≈")
        LazyColumn(
                modifier = modifier
                        .padding(3.dp)
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
                        .fillMaxSize(),
                state = state
        ) {
                itemsIndexed(symbols) { index, symbol ->
                        Box(
                                modifier = Modifier
                                        .pointerInput(Unit) {
                                                detectTapGestures(
                                                        onPress = {
                                                                isPressing = true
                                                                pressingIndex = index
                                                                context.audioFeedback(SoundEffect.Input)
                                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                                tryAwaitRelease()
                                                                isPressing = false
                                                                pressingIndex = -1
                                                        },
                                                        onTap = {
                                                                context.input(symbol)
                                                                pressingState += 1
                                                        }
                                                )
                                        }
                                        .background(
                                                color = backgroundColor(isDarkMode, isHighContrastPreferred, isPressing && pressingIndex == index)
                                        )
                                        .height(unitHeight)
                                        .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = symbol,
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        fontSize = 18.sp
                                )
                        }
                        HorizontalDivider(
                                thickness = 1.dp,
                                color = Color.Gray
                        )
                }
        }
}

private fun backgroundColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, isPressing: Boolean) =
        if (isDarkMode) {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyDark else AltPresetColor.keyDarkEmphatic
                } else {
                        if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                }
        } else {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyLight else AltPresetColor.keyLightEmphatic
                } else {
                        if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                }
        }
