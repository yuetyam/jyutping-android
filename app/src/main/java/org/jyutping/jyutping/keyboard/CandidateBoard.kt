package org.jyutping.jyutping.keyboard

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor
import splitties.systemservices.windowManager

private fun Candidate.width(): Dp = when {
        this.type.isCantonese() -> (this.text.length * 20 + 32).dp
        (this.text.length == 1) -> 64.dp
        else -> (this.text.length * 18).dp
}

private class CandidateRow(val identifier: Int, val candidates: List<Candidate>, val width: Dp = candidates.map { it.width() }.fold(0.dp) { acc, w -> acc + w })

@Composable
fun CandidateBoard(height: Dp) {
        val collapseWidth: Dp = 44.dp
        val collapseHeight: Dp = 44.dp
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val commentStyle by context.commentStyle.collectAsState()
        val rowVerticalAlignment: Alignment.Vertical = if (commentStyle.isBelow()) Alignment.Top else Alignment.Bottom
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val screenWidth: Dp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val windowMetrics = context.windowManager.currentWindowMetrics
                (windowMetrics.bounds.width() / windowMetrics.density).dp
        } else {
                LocalConfiguration.current.screenWidthDp.dp
        }
        val state = rememberLazyListState()
        val candidates by context.candidates.collectAsState()
        val candidateState by context.candidateState.collectAsState()
        LaunchedEffect(candidateState) {
                state.scrollToItem(index = 0, scrollOffset = 0)
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
                                if (isDarkMode) {
                                        if (isHighContrastPreferred) Color.Black else PresetColor.keyboardDarkBackground
                                } else {
                                        if (isHighContrastPreferred) Color.White else PresetColor.keyboardLightBackground
                                }
                        )
                        .systemBarsPadding()
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
                                                CandidateView(
                                                        candidate = it,
                                                        commentStyle = commentStyle,
                                                        isDarkMode = isDarkMode,
                                                        modifier = Modifier
                                                                .clickable(interactionSource = interactionSource, indication = null) {
                                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                        context.selectCandidate(it)
                                                                }
                                                                .padding(2.dp)
                                                                .weight(it.width() / row.width)
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
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else PresetColor.keyLightEmphatic
                                        }
                                )
                        }
                }
                IconButton(
                        onClick = {
                                view.playSoundEffect(SoundEffectConstants.NAVIGATION_UP)
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
                                        shape = RoundedCornerShape(4.dp)
                                )
                                .background(
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.Black else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isHighContrastPreferred) Color.White else PresetColor.keyLightEmphatic
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                )
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.chevron_up),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
        }
}
