package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService

@Composable
fun CandidateScrollBar() {
        val interactionSource = remember { MutableInteractionSource() }
        val context = LocalContext.current as JyutpingInputMethodService
        val candidates = remember { context.candidates }
        Box(
                modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                contentAlignment = Alignment.Center
        ) {
                Color.Transparent
                LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        items(candidates.value) {
                                CandidateView(
                                        candidate = it,
                                        modifier = Modifier
                                                .padding(bottom = 8.dp)
                                                .clickable(interactionSource = interactionSource, indication = null) {
                                                        context.select(it)
                                                }
                                )
                        }
                }
        }
}
