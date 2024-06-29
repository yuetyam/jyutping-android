package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService

@Composable
fun LeftKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val context = LocalContext.current as JyutpingInputMethodService
        val lightEmphatic = Color(0xFFACB1B9)
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) { context.leftKey() }
                        .fillMaxWidth()
                        .fillMaxHeight(),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isPressed.value) lightEmphatic else Color.White)
                                .fillMaxWidth()
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Text(
                                text = "，",
                                fontSize = 20.sp
                        )
                }
        }
}
