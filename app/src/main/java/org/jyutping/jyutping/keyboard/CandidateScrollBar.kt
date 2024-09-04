package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun CandidateScrollBar() {
        val expanderWidth: Dp = 44.dp
        val dividerHeight: Dp = 24.dp
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val state = rememberLazyListState()
        LaunchedEffect(context.candidateState.intValue) {
                state.scrollToItem(index = 0, scrollOffset = 0)
        }
        Box(
                contentAlignment = Alignment.CenterEnd
        ) {
                LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.Bottom
                ) {
                        items(context.candidates.value) {
                                CandidateView(
                                        candidate = it,
                                        isDarkMode = isDarkMode.value,
                                        modifier = Modifier
                                                .clickable(interactionSource = interactionSource, indication = null) {
                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.select(it)
                                                }
                                                .padding(horizontal = if (it.type.isCantonese()) 6.dp else 10.dp)
                                                .padding(bottom = 12.dp)
                                )
                        }
                }
                Box(
                        modifier = Modifier
                                .background(if (isDarkMode.value) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground)
                                .width(expanderWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Box(
                                modifier = Modifier
                                        .height(dividerHeight)
                                        .fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                        ) {
                                VerticalDivider(
                                        modifier = Modifier.alpha(0.3f),
                                        thickness = 1.dp,
                                        color = Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.transformTo(KeyboardForm.CandidateBoard)
                                },
                                modifier = Modifier.fillMaxSize()
                        ) {
                                Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.chevron_down),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isDarkMode.value) Color.White else Color.Black
                                )
                        }
                }
        }
}
