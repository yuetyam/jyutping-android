package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun SettingsScreen(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        val characterStandard = remember { context.characterStandard }
        val isAudioFeedbackOn = remember { context.isAudioFeedbackOn }
        val isHapticFeedbackOn = remember { context.isHapticFeedbackOn }
        val showLowercaseKeys = remember { context.showLowercaseKeys }
        val needsInputModeSwitchKey = remember { context.needsInputModeSwitchKey }
        val needsLeftKey = remember { context.needsLeftKey }
        val needsRightKey = remember { context.needsRightKey }
        val commentStyle = remember { context.commentStyle }
        val isEmojiSuggestionsOn = remember { context.isEmojiSuggestionsOn }
        val cangjieVariant = remember { context.cangjieVariant }
        val isInputMemoryOn = remember { context.isInputMemoryOn }
        val tintColor: Color = if (isDarkMode.value) Color.White else Color.Black
        val backColor: Color = if (isDarkMode.value) Color.Black else Color.White
        val buttonColors: ButtonColors = if (isDarkMode.value) {
                ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        } else {
                ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        }
        val destructiveButtonColors: ButtonColors = if (isDarkMode.value) {
                ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.Red)
        } else {
                ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red)
        }
        val switchColors: SwitchColors = if (isDarkMode.value) {
                SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PresetColor.accentGreen, uncheckedThumbColor = Color.LightGray, uncheckedTrackColor = Color.DarkGray, uncheckedBorderColor = Color.Gray)
        } else {
                SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PresetColor.accentGreen, uncheckedThumbColor = Color.DarkGray, uncheckedTrackColor = Color.LightGray, uncheckedBorderColor = Color.Gray)
        }
        val version: String by lazy { BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" }
        Column(
                modifier = Modifier
                        .background(if (isDarkMode.value) PresetColor.keyboardDarkBackground else PresetColor.keyboardLightBackground)
                        .systemBarsPadding()
                        .height(height)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Row(
                        modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        IconButton(
                                onClick = {
                                        context.transformTo(KeyboardForm.Alphabetic)
                                }
                        ) {
                               Icon(
                                       imageVector = Icons.Outlined.ArrowUpward,
                                       contentDescription = null,
                                       tint = tintColor
                               )
                        }
                        Text(
                                text = stringResource(id = R.string.keyboard_settings_navigation_hint),
                                color = tintColor
                        )
                        IconButton(
                                onClick = { /* TODO: Expansion */ },
                                modifier = Modifier.alpha(0f)
                        ) {
                                Icon(
                                        imageVector = Icons.Outlined.ArrowUpward,
                                        contentDescription = null,
                                        tint = tintColor
                                )
                        }
                }
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_character_standard_header),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = tintColor,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                        .fillMaxWidth()
                                        ) {
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Traditional)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_traditional))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.Traditional) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.HongKong)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_hongkong))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.HongKong) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Taiwan)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_taiwan))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.Taiwan) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Simplified)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_simplified))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value.isSimplified()) 1f else 0f)
                                                        )
                                                }
                                        }
                                }
                        }
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_keyboard_feedback_header),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = tintColor,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                        .fillMaxWidth()
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_keyboard_feedback_audio_switch_title),
                                                                color = tintColor
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Switch(
                                                                checked = isAudioFeedbackOn.value,
                                                                onCheckedChange = {
                                                                        isAudioFeedbackOn.value = it
                                                                        context.updateAudioFeedback(it)
                                                                },
                                                                colors = switchColors
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_keyboard_feedback_haptic_switch_title),
                                                                color = tintColor
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Switch(
                                                                checked = isHapticFeedbackOn.value,
                                                                onCheckedChange = {
                                                                        isHapticFeedbackOn.value = it
                                                                        context.updateHapticFeedback(it)
                                                                },
                                                                colors = switchColors
                                                        )
                                                }
                                        }
                                }
                        }
                        item {
                                Column(
                                        modifier = Modifier
                                                .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                .fillMaxWidth()
                                ) {
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_show_lowercase_keys_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = showLowercaseKeys.value,
                                                        onCheckedChange = {
                                                                showLowercaseKeys.value = it
                                                                context.updateShowLowercaseKeys(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode.value)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_globe_key_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = needsInputModeSwitchKey.value,
                                                        onCheckedChange = {
                                                                needsInputModeSwitchKey.value = it
                                                                context.updateNeedsInputModeSwitchKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode.value)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_left_key_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = needsLeftKey.value,
                                                        onCheckedChange = {
                                                                needsLeftKey.value = it
                                                                context.updateNeedsLeftKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode.value)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_right_key_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = needsRightKey.value,
                                                        onCheckedChange = {
                                                                needsRightKey.value = it
                                                                context.updateNeedsRightKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                }
                        }
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_comment_style_header),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = tintColor,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                        .fillMaxWidth()
                                        ) {
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCommentStyle(CommentStyle.AboveCandidates)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_comment_style_above))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.value.isAbove()) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCommentStyle(CommentStyle.BelowCandidates)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_comment_style_below))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.value.isBelow()) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCommentStyle(CommentStyle.NoComments)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_comment_style_none))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.value.isNone()) 1f else 0f)
                                                        )
                                                }
                                        }
                                }
                        }
                        item {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_emoji_switch_title),
                                                color = tintColor
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(
                                                checked = isEmojiSuggestionsOn.value,
                                                onCheckedChange = {
                                                        isEmojiSuggestionsOn.value = it
                                                        context.updateEmojiSuggestionsState(it)
                                                },
                                                colors = switchColors
                                        )
                                }
                        }
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_cangjie_variant_header),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = tintColor,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                        .fillMaxWidth()
                                        ) {
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCangjieVariant(CangjieVariant.Cangjie5)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_cangjie_variant_cangjie5))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant.value == CangjieVariant.Cangjie5) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCangjieVariant(CangjieVariant.Cangjie3)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_cangjie_variant_cangjie3))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant.value == CangjieVariant.Cangjie3) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCangjieVariant(CangjieVariant.Quick5)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_cangjie_variant_quick5))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant.value == CangjieVariant.Quick5) 1f else 0f)
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCangjieVariant(CangjieVariant.Quick3)
                                                        },
                                                        shape = RectangleShape,
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_cangjie_variant_quick3))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant.value == CangjieVariant.Quick3) 1f else 0f)
                                                        )
                                                }
                                        }
                                }
                        }
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_user_lexicon_header),
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = tintColor,
                                                style = MaterialTheme.typography.bodySmall
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                        .fillMaxWidth()
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_input_memory_switch_title),
                                                                color = tintColor
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Switch(
                                                                checked = isInputMemoryOn.value,
                                                                onCheckedChange = {
                                                                        isInputMemoryOn.value = it
                                                                        context.updateInputMemoryState(it)
                                                                },
                                                                colors = switchColors
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode.value)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.clearUserLexicon()
                                                                // TODO: Confirm deletion
                                                        },
                                                        shape = RectangleShape,
                                                        colors = destructiveButtonColors,
                                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_clear_user_lexicon))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                }
                                        }
                                }
                        }
                        item {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.about_label_version),
                                                color = tintColor
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        SelectionContainer {
                                                Text(
                                                        text = version,
                                                        color = tintColor
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ResponsiveDivider(isDarkMode: Boolean) {
        HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = if (isDarkMode) PresetColor.keyDarkEmphatic else PresetColor.keyLightEmphatic
        )
}
