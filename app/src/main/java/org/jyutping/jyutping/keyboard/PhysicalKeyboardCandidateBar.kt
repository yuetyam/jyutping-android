package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

/**
 * Horizontal scrolling candidate bar for physical keyboard collapsed mode.
 * Uses the same spacing strategy as CandidateScrollBar (individual candidates with padding),
 * not the expanded CandidateBoard (row-wrapping with weights).
 */
@Composable
fun PhysicalKeyboardCandidateBar(height: Dp) {
        val buttonAreaWidth: Dp = 44.dp * 3 + 8.dp // Space for 3 buttons (expand, mode switch, keyboard) + padding
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val commentStyle by context.commentStyle.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val state = rememberLazyListState()
        val candidates by context.candidates.collectAsState()
        val candidateState by context.candidateState.collectAsState()
        val candidateOffset by context.candidateOffset.collectAsState()
        
        // Reset scroll position when candidate state changes (new candidates)
        LaunchedEffect(candidateState) {
                state.animateScrollToItem(index = 0, scrollOffset = 0)
        }
        
        // Auto-scroll when candidate offset changes (Tab/Shift+Tab navigation)
        LaunchedEffect(candidateOffset) {
                if (candidateOffset < candidates.size) {
                        // Scroll to show the first candidate of the current group
                        state.animateScrollToItem(index = candidateOffset, scrollOffset = 0)
                }
        }
        
        Box(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.darkBackground else AltPresetColor.lightBackground
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.paddingValue().dp)
                        .height(height)
                        .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
        ) {
                Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Horizontal scrolling candidate list (like CandidateScrollBar)
                        LazyRow(
                                modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
                                state = state,
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                itemsIndexed(candidates) { index, candidate ->
                                        // Show number labels 7-9 for the current group of 3 candidates
                                        val numberLabel = when {
                                                index == candidateOffset -> "7"
                                                index == candidateOffset + 1 -> "8"
                                                index == candidateOffset + 2 -> "9"
                                                else -> null
                                        }
                                        
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
                                                },
                                                numberLabel = numberLabel
                                        )
                                }
                        }
                        // Reserve space for buttons on the right
                        Box(
                                modifier = Modifier
                                        .width(buttonAreaWidth)
                                        .fillMaxHeight()
                        )
                }
                // Divider at the bottom
                HorizontalDivider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        thickness = 1.dp,
                        color = if (isHighContrastPreferred) {
                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                        } else {
                                if (isDarkMode) PresetColor.emphaticDark else PresetColor.emphaticLight
                        }
                )
                // Physical keyboard buttons (expand, mode switch, keyboard)
                CandidateBoardPhysicalButtons(
                        collapseWidth = 44.dp,
                        collapseHeight = 44.dp,
                        isDarkMode = isDarkMode,
                        isHighContrastPreferred = isHighContrastPreferred
                )
        }
}
