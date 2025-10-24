package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm

@Composable
fun CandidateScrollBar() {
        val expanderWidth: Dp = 44.dp
        val dividerHeight: Dp = 24.dp
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val commentStyle by context.commentStyle.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val state = rememberLazyListState()
        val candidates by context.candidates.collectAsState()
        val candidateState by context.candidateState.collectAsState()
        LaunchedEffect(candidateState) {
                state.animateScrollToItem(index = 0, scrollOffset = 0)
        }
        Row(
                modifier = Modifier.fillMaxSize()
        ) {
                LazyRow(
                        modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                        state = state,
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        itemsIndexed(candidates) { index, candidate ->
                                CandidateView(
                                        candidateState = candidateState,
                                        candidate = candidate,
                                        commentStyle = commentStyle,
                                        isDarkMode = isDarkMode,
                                        selection = {
                                                context.audioFeedback(SoundEffect.Click)
                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                context.selectCandidate(index = index)
                                        },
                                        deletion = {
                                                context.audioFeedback(SoundEffect.Delete)
                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                context.forgetCandidate(index = index)
                                        }
                                )
                        }
                }
                Box(
                        modifier = Modifier
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
                                VerticalDivider(color = Color.Gray)
                        }
                        IconButton(
                                onClick = {
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.transformTo(KeyboardForm.CandidateBoard)
                                },
                                modifier = Modifier.fillMaxSize()
                        ) {
                                Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.chevron_down),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                }
        }
}
