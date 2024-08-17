package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import org.jyutping.jyutping.extensions.keyLightEmphatic
import org.jyutping.jyutping.extensions.keyboardLightBackground

@Composable
fun CandidateBoard(height: Dp) {
        val collapseWidth: Dp = 44.dp
        val collapseHeight: Dp = 44.dp
        val minimumCellSize: Dp = 50.dp
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val state = rememberLazyGridState()
        LaunchedEffect(context.candidateState.intValue) {
                state.scrollToItem(index = 0, scrollOffset = 0)
        }
        Box(
                modifier = Modifier
                        .background(Color.keyboardLightBackground)
                        .height(height)
                        .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
        ) {
                LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = minimumCellSize),
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        verticalArrangement = Arrangement.Top,
                        horizontalArrangement = Arrangement.Start
                ) {
                        items(context.candidates.value) { candidate ->
                                CandidateView(
                                        candidate = candidate,
                                        modifier = Modifier
                                                .clickable(interactionSource = interactionSource, indication = null) {
                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                        context.select(candidate)
                                                }
                                                .padding(2.dp)
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
                                .background(color = Color.keyLightEmphatic, shape = RoundedCornerShape(4.dp))
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.chevron_up),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                        )
                }
        }
}
