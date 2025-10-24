package org.jyutping.jyutping.emoji

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun EmojiBoard(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val headerHeight: Dp = 20.dp
        val footerHeight: Dp = 40.dp
        val gridHeight: Dp = height - headerHeight - footerHeight
        val cellHeight: Dp = gridHeight.div(5)
        val edgeIndicatorWeight: Float = 1f
        val indicatorWeight: Float = 1f
        val emojis by context.emojiBoardEmojis.collectAsState()
        val categoryStartIndexMap by context.categoryStartIndexMap.collectAsState()
        var headerTitleId by remember { mutableIntStateOf(R.string.emoji_board_header_frequently_used) }
        val gridState = rememberLazyGridState()
        val coroutineScope = rememberCoroutineScope()
        Column(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.darkBackground else AltPresetColor.lightBackground
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.paddingValue().dp)
                        .height(height)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
        ) {
                Row(
                        modifier = Modifier
                                .height(headerHeight)
                                .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = context.getString(headerTitleId),
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 13.sp
                        )
                }
                LazyHorizontalGrid(
                        rows = GridCells.Fixed(5),
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight),
                        state = gridState,
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                        items(emojis) { emoji ->
                                Box(
                                        modifier = Modifier
                                                .size(cellHeight)
                                                .clickable {
                                                        context.audioFeedback(SoundEffect.Input)
                                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                                        context.input(emoji.text)
                                                        context.updateEmojiFrequent(emoji)
                                                },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = emoji.text,
                                                fontSize = 28.sp
                                        )
                                }
                        }
                }
                Row(
                        modifier = Modifier
                                .height(footerHeight)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(
                                onClick = {
                                        context.audioFeedback(SoundEffect.Back)
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_frequently_used
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.Frequent] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_smileys_and_people
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.SmileysAndPeople] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_animals_and_nature
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.AnimalsAndNature] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_food_and_drink
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.FoodAndDrink] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_activity
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.Activity] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_travel_and_places
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.TravelAndPlaces] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_objects
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.Objects] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_symbols
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.Symbols] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Click)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        headerTitleId = R.string.emoji_board_header_flags
                                        val targetIndex = categoryStartIndexMap[EmojiCategory.Flags] ?: 0
                                        coroutineScope.launch {
                                                gridState.scrollToItem(targetIndex)
                                        }
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
                                        context.audioFeedback(SoundEffect.Delete)
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
