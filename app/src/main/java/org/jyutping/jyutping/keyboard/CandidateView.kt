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
fun CandidateView(
        candidateState: Int,
        candidate: Candidate,
        commentStyle: CommentStyle,
        isDarkMode: Boolean,
        selection: () -> Unit,
        deletion: () -> Unit,
        numberLabel: String? = null
) {
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
                
                // Number label in top-left corner (absolute positioning)
                if (numberLabel != null) {
                        Text(
                                text = numberLabel,
                                color = textColor.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(top = 2.dp)
                        )
                }
                
                // Main content: vertically centered Column with romanization above text
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // Romanization (Jyutping) above the candidate text
                        if (isCantonese && !commentStyle.isNone()) {
                                if (commentStyle.isAbove()) {
                                        Text(
                                                text = candidate.romanization,
                                                color = textColor,
                                                fontSize = 12.sp,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1
                                        )
                                }
                        }
                        
                        // Candidate text
                        Text(
                                text = candidate.text,
                                color = textColor,
                                fontSize = 20.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                        )
                        
                        // Romanization below (if needed)
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
