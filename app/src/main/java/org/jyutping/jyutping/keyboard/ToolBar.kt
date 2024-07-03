package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R

@Composable
fun ToolBar() {
        val inputMethodModeSwitchWidth = 80.dp
        val buttonWidth = 50.dp
        val iconSize = 24.dp
        val editingIconSize = 28.dp
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
                IconButton(
                        onClick = {
                                view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.Settings)
                        },
                        modifier = Modifier
                                .width(buttonWidth)
                                .fillMaxHeight()
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_settings),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                        )
                }
                IconButton(
                        onClick = {
                                view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.EmojiBoard)
                        },
                        modifier = Modifier
                                .width(buttonWidth)
                                .fillMaxHeight()
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_emoji),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                        )
                }
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.toggleInputMethodMode()
                                }
                                .width(inputMethodModeSwitchWidth)
                                .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                ) {
                        InputMethodModeSwitch()
                }
                IconButton(
                        onClick = {
                                view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.EditingPanel)
                        },
                        modifier = Modifier
                                .width(buttonWidth)
                                .fillMaxHeight()
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_editing),
                                contentDescription = null,
                                modifier = Modifier.size(editingIconSize)
                        )
                }
                IconButton(
                        onClick = {
                                view.playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.dismissKeyboard()
                        },
                        modifier = Modifier
                                .width(buttonWidth)
                                .fillMaxHeight()
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_dismiss_keyboard),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize)
                        )
                }
        }
}
