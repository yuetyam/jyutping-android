package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CandidateView(candidate: Candidate, commentStyle: CommentStyle, isDarkMode: Boolean, modifier: Modifier) {
        val isCantonese: Boolean = candidate.type.isCantonese()
        val textColor: Color = if (isDarkMode) Color.White else Color.Black
        Column(
                modifier = modifier,
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
