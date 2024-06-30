package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.KeyboardHide
import androidx.compose.material.icons.outlined.MultipleStop
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.extensions.keyLightEmphatic

@Composable
fun ToolBar(modifier: Modifier) {
        val itemWidth = 50.dp
        val interactionSource = remember { MutableInteractionSource() }
        val context = LocalContext.current as JyutpingInputMethodService
        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: Settings
                                }
                                .width(itemWidth)
                                .fillMaxHeight()
                                .alpha(0f),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: Emoji
                                }
                                .width(itemWidth)
                                .fillMaxHeight()
                                .alpha(0f),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = Icons.Outlined.EmojiEmotions,
                                contentDescription = null
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                InputMethodModeSwitch()
                Spacer(modifier = Modifier.weight(1f))
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: EditingPanel
                                }
                                .width(itemWidth)
                                .fillMaxHeight()
                                .alpha(0f),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = Icons.Outlined.MultipleStop,
                                contentDescription = null
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) { context.dismissKeyboard() }
                                .width(itemWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = Icons.Outlined.KeyboardHide,
                                contentDescription = null
                        )
                }
        }
}

@Composable
private fun InputMethodModeSwitch() {
        val interactionSource = remember { MutableInteractionSource() }
        val context = LocalContext.current as JyutpingInputMethodService
        Box(
                modifier = Modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                // TODO: InputMethodModeSwitch
                        }
                        .width(80.dp)
                        .fillMaxHeight()
                        .alpha(0f),
                contentAlignment = Alignment.Center
        ) {
                Box (
                        modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.keyLightEmphatic)
                                .width(72.dp)
                                .height(26.dp),
                        contentAlignment = Alignment.Center
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(text = "粵")
                                Text(text = "A")
                        }
                }
        }
}
