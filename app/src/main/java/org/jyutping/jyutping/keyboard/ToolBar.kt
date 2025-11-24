package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm

@Composable
fun ToolBar() {
        val inputMethodModeSwitchWidth = 64.dp
        val buttonSize = 48.dp
        val iconSize = 24.dp
        val editingIconSize = 28.dp
        val interactionSource = remember { MutableInteractionSource() }
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.Settings)
                        },
                        modifier = Modifier.size(buttonSize)
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_settings),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.EmojiBoard)
                        },
                        modifier = Modifier.size(buttonSize)
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_emoji),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
                Box (
                        modifier = Modifier
                                .clickable(interactionSource = interactionSource, indication = null) {
                                        context.audioFeedback(SoundEffect.Click)
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
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.EditingPanel)
                        },
                        modifier = Modifier.size(buttonSize)
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_editing),
                                contentDescription = null,
                                modifier = Modifier.size(editingIconSize),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.dismissKeyboard()
                        },
                        modifier = Modifier.size(buttonSize)
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_dismiss_keyboard),
                                contentDescription = null,
                                modifier = Modifier.size(iconSize),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
        }
}
