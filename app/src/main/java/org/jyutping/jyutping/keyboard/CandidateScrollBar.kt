package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService

@Composable
fun CandidateScrollBar() {
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val state = rememberLazyListState()
        LaunchedEffect(context.candidateState.intValue) {
                state.scrollToItem(index = 0, scrollOffset = 0)
        }
        LazyRow(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                state = state,
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.Bottom
        ) {
                items(context.candidates.value) {
                        CandidateView(
                                candidate = it,
                                modifier = Modifier
                                        .clickable(interactionSource = interactionSource, indication = null) {
                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                context.select(it)
                                        }
                                        .padding(horizontal = 6.dp)
                                        .padding(bottom = 8.dp)
                        )
                }
        }
}
