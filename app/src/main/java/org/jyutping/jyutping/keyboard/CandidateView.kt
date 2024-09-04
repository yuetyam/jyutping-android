package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CandidateView(candidate: Candidate, isDarkMode: Boolean, modifier: Modifier) {
        Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy((-2).dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                if (candidate.type.isCantonese()) {
                        Text(
                                text = candidate.romanization,
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 12.sp
                        )
                }
                Text(
                        text = candidate.text,
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 20.sp
                )
        }
}
