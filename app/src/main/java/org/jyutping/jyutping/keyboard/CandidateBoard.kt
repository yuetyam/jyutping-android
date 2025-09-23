package org.jyutping.jyutping.keyboard

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import splitties.systemservices.windowManager

private fun Candidate.width(): Dp = when (this.type) {
        CandidateType.Cantonese -> (this.text.length * 20 + 32).dp
        CandidateType.Emoji -> 64.dp
        CandidateType.Symbol -> if (this.text.length == 1) 64.dp else (this.text.length * 18).dp
        else -> if (this.text.length == 1) 64.dp else (this.text.length * 18).dp
}

private class CandidateRow(val identifier: Int, val candidates: List<Candidate>, val width: Dp = candidates.map { it.width() }.fold(0.dp) { acc, w -> acc + w })

@Composable
fun CandidateBoard(height: Dp) {
        val collapseWidth: Dp = 44.dp
        val collapseHeight: Dp = 44.dp
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val commentStyle by context.commentStyle.collectAsState()
        val rowVerticalAlignment: Alignment.Vertical = when (commentStyle) {
                CommentStyle.AboveCandidates -> Alignment.Bottom
                CommentStyle.BelowCandidates -> Alignment.Top
                CommentStyle.NoComments -> Alignment.CenterVertically
        }
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val screenWidth: Dp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // TODO: Consider SafeArea?
                val windowMetrics = context.windowManager.currentWindowMetrics
                (windowMetrics.bounds.width() / windowMetrics.density).dp
        } else {
                (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density).dp
        }
        val state = rememberLazyListState()
        val candidates by context.candidates.collectAsState()
        val candidateState by context.candidateState.collectAsState()
        LaunchedEffect(candidateState) {
                state.animateScrollToItem(index = 0, scrollOffset = 0)
        }
        val minRowIdentifier: Int = 1000
        val candidateRows: List<CandidateRow> by lazy {
                val rows: MutableList<CandidateRow> = mutableListOf()
                var cache: MutableList<Candidate> = mutableListOf()
                var rowID: Int = minRowIdentifier
                var rowWidth: Dp = 0.dp
                for (index in candidates.indices) {
                        val candidate = candidates[index]
                        val maxWidth: Dp = if (rows.isEmpty()) (screenWidth - collapseWidth) else screenWidth
                        val length: Dp = candidate.width()
                        if (rowWidth < (maxWidth - length)) {
                                cache.add(candidate)
                                rowWidth += length
                        } else {
                                val row = CandidateRow(identifier = rowID, candidates = cache)
                                rows.add(row)
                                cache = mutableListOf(candidate)
                                rowID += 1
                                rowWidth = length
                        }
                }
                val lastRow = CandidateRow(identifier = rowID, candidates = cache)
                rows.add(lastRow)
                rows
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
                contentAlignment = Alignment.TopEnd
        ) {
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                ) {
                        items(candidateRows) { row ->
                                Row(
                                        modifier = Modifier
                                                .defaultMinSize(minHeight = collapseHeight)
                                                .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                                        verticalAlignment = rowVerticalAlignment
                                ) {
                                        row.candidates.map {
                                                AltCandidateView(
                                                        modifier = Modifier
                                                                .padding(2.dp)
                                                                .weight(it.width() / row.width),
                                                        candidateState = candidateState,
                                                        candidate = it,
                                                        commentStyle = commentStyle,
                                                        isDarkMode = isDarkMode,
                                                        selection = {
                                                                context.audioFeedback(SoundEffect.Click)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.selectCandidate(it)
                                                        },
                                                        deletion = {
                                                                context.audioFeedback(SoundEffect.Delete)
                                                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                                                context.forgetCandidate(it)
                                                        }
                                                )
                                        }
                                        if (row.identifier == minRowIdentifier) {
                                                Spacer(modifier = Modifier
                                                        .width(collapseWidth)
                                                        .weight(collapseWidth / screenWidth))
                                        }
                                }
                                HorizontalDivider(
                                        thickness = 1.dp,
                                        color = if (isHighContrastPreferred) {
                                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                        } else {
                                                if (isDarkMode) PresetColor.emphaticDark else PresetColor.emphaticLight
                                        }
                                )
                        }
                }
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Back)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.Alphabetic)
                        },
                        modifier = Modifier
                                .width(collapseWidth)
                                .height(collapseHeight)
                                .border(
                                        width = 1.dp,
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else Color.Transparent
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else Color.Transparent
                                        },
                                        shape = CircleShape
                                )
                                .background(
                                        color = if (isHighContrastPreferred) {
                                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                        } else {
                                                if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
                                        },
                                        shape = CircleShape
                                )
                                .padding(top = 4.dp, end = 4.dp)
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.chevron_up),
                                contentDescription = null,
                                modifier = Modifier
                                        .padding(bottom = 4.dp, start = 4.dp)
                                        .size(20.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
        }
}
