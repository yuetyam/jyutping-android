package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun CandidateScrollBar() {
        val expanderWidth: Dp = 44.dp
        val dividerHeight: Dp = 24.dp
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val commentStyle by context.commentStyle.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val state = rememberLazyListState()
        val candidates by context.candidates.collectAsState()
        val candidateState by context.candidateState.collectAsState()
        LaunchedEffect(candidateState) {
                state.scrollToItem(index = 0, scrollOffset = 0)
        }
        Box(
                contentAlignment = Alignment.CenterEnd
        ) {
                LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        items(candidates) {
                                CandidateView(
                                        candidate = it,
                                        commentStyle = commentStyle,
                                        isDarkMode = isDarkMode,
                                        selection = { context.selectCandidate(it) },
                                        deletion = { context.forgetCandidate(it) }
                                )
                        }
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
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                }
        }
}
