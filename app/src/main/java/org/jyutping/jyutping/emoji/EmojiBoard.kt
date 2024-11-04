package org.jyutping.jyutping.emoji

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.EmojiFlags
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material.icons.outlined.EmojiNature
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.EmojiPeople
import androidx.compose.material.icons.outlined.EmojiSymbols
import androidx.compose.material.icons.outlined.EmojiTransportation
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun EmojiBoard(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val edgeIndicatorWeight: Float = 1f
        val indicatorWeight: Float = 1f
        Column(
                modifier = Modifier
                        .background(if (isDarkMode) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground)
                        .systemBarsPadding()
                        .height(height)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
        ) {
                Row(
                        modifier = Modifier
                                .height(20.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = "<#Emoji Category Title#>",
                                color = if (isDarkMode) Color.White else Color.Black,
                                style = MaterialTheme.typography.labelMedium
                        )
                }
                LazyRow(
                        modifier = Modifier
                                .height(height - 60.dp)
                                .fillMaxWidth()
                ) {
                        item {
                                Text(
                                        text = " 未實現 ",
                                        color = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        item {
                                Text(
                                        text = " Not Implemented Yet",
                                        color = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                }
                Row(
                        modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_UP)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.transformTo(KeyboardForm.Alphabetic)
                                },
                                modifier = Modifier.weight(edgeIndicatorWeight)
                        ) {
                                Text(
                                        text = "ABC",
                                        color = if (isDarkMode) Color.White else Color.Black,
                                        style = MaterialTheme.typography.bodyLarge
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.Schedule,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiEmotions,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiNature,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiFoodBeverage,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiPeople,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiTransportation,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiObjects,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiSymbols,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                },
                                modifier = Modifier.weight(indicatorWeight)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.EmojiFlags,
                                        contentDescription = null,
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                        IconButton(
                                onClick = {
                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.backspace()
                                },
                                modifier = Modifier.weight(edgeIndicatorWeight)
                        ) {
                                Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.key_backspace),
                                        contentDescription = null,
                                        modifier = Modifier.height(20.dp),
                                        tint = if (isDarkMode) Color.White else Color.Black
                                )
                        }
                }
        }
}
