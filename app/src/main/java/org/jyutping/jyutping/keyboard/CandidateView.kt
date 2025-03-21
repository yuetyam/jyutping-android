package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.presets.PresetString

// For CandidateScrollBar
@Composable
fun CandidateView(candidateState: Int, candidate: Candidate, commentStyle: CommentStyle, isDarkMode: Boolean, selection: () -> Unit, deletion: () -> Unit) {
        val isCantonese: Boolean = candidate.type.isCantonese()
        val textColor: Color = if (isDarkMode) Color.White else Color.Black
        Box(
                modifier = Modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onLongPress = {
                                                if (candidateState > 0) {
                                                        deletion()
                                                }
                                        },
                                        onTap = { selection() }
                                )
                        }
                        .padding(horizontal = if (isCantonese) 8.dp else 10.dp)
                        .fillMaxHeight(),
                contentAlignment = Alignment.Center
        ) {
                Color.Transparent
                Box(
                        modifier = Modifier
                                .alpha(if (commentStyle.isNone()) 0f else 1f)
                                .fillMaxHeight(),
                        contentAlignment = if (commentStyle.isBelow()) Alignment.BottomCenter else Alignment.TopCenter
                ) {
                        Color.Transparent
                        Text(
                                text = if (isCantonese) candidate.romanization else PresetString.SPACE,
                                modifier = Modifier
                                        .padding(vertical = 2.dp)
                                        .height(20.dp),
                                color = textColor,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                        )
                }
                Text(
                        text = candidate.text,
                        modifier = Modifier
                                .padding(bottom = if (commentStyle.isBelow()) 16.dp else 0.dp),
                        color = textColor,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                )
        }
}

// For CandidateBoard
@Composable
fun AltCandidateView(modifier: Modifier, candidateState: Int, candidate: Candidate, commentStyle: CommentStyle, isDarkMode: Boolean, selection: () -> Unit, deletion: () -> Unit) {
        val isCantonese: Boolean = candidate.type.isCantonese()
        val textColor: Color = if (isDarkMode) Color.White else Color.Black
        Column(
                modifier = modifier
                        .pointerInput(Unit) {
                                detectTapGestures(
                                        onLongPress = {
                                                if (candidateState > 0) {
                                                        deletion()
                                                }
                                        },
                                        onTap = { selection() }
                                )
                        },
                verticalArrangement = Arrangement.spacedBy((-2).dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                if (isCantonese && commentStyle.isAbove()) {
                        Text(
                                text = candidate.romanization,
                                color = textColor,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                        )
                }
                Text(
                        text = candidate.text,
                        color = textColor,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                )
                if (isCantonese && commentStyle.isBelow()) {
                        Text(
                                text = candidate.romanization,
                                color = textColor,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                        )
                }
        }
}
