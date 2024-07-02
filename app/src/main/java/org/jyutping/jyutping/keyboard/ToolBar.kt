package org.jyutping.jyutping.keyboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R

@Composable
fun ToolBar() {
        val itemWidth = 50.dp
        val interactionSource = remember { MutableInteractionSource() }
        val context = LocalContext.current as JyutpingInputMethodService
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
                Box(
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: Settings
                                }
                                .width(itemWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_settings),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: Emoji
                                }
                                .width(itemWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_emoji),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) { context.toggleInputMethodMode() }
                                .width(80.dp)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        InputMethodModeSwitch()
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        // TODO: EditingPanel
                                }
                                .width(itemWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_editing),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                        )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) { context.dismissKeyboard() }
                                .width(itemWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_dismiss_keyboard),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                        )
                }
        }
}
