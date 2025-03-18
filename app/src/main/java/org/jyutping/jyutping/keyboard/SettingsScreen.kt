package org.jyutping.jyutping.keyboard

import android.os.Build
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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jyutping.jyutping.BuildConfig
import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun SettingsScreen(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val characterStandard by context.characterStandard.collectAsState()
        val isAudioFeedbackOn by context.isAudioFeedbackOn.collectAsState()
        val isHapticFeedbackOn by context.isHapticFeedbackOn.collectAsState()
        val useTenKeyNumberPad by context.useTenKeyNumberPad.collectAsState()
        val showLowercaseKeys by context.showLowercaseKeys.collectAsState()
        val previewKeyText by context.previewKeyText.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val needsInputModeSwitchKey by context.needsInputModeSwitchKey.collectAsState()
        val needsLeftKey by context.needsLeftKey.collectAsState()
        val needsRightKey by context.needsRightKey.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val commentStyle by context.commentStyle.collectAsState()
        val cangjieVariant by context.cangjieVariant.collectAsState()
        val isEmojiSuggestionsOn by context.isEmojiSuggestionsOn.collectAsState()
        val isInputMemoryOn by context.isInputMemoryOn.collectAsState()
        var isTryingToClearInputMemory by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val tintColor: Color = if (isDarkMode) Color.White else Color.Black
        val backColor: Color = if (isDarkMode) Color.Black else Color.White
        val buttonColors: ButtonColors = if (isDarkMode) {
                ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
        } else {
                ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        }
        val destructiveButtonColors: ButtonColors = if (isDarkMode) {
                ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.Red)
        } else {
                ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red)
        }
        val switchColors: SwitchColors = if (isDarkMode) {
                SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PresetColor.green, uncheckedThumbColor = Color.LightGray, uncheckedTrackColor = Color.DarkGray, uncheckedBorderColor = Color.Gray)
        } else {
                SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PresetColor.green, uncheckedThumbColor = Color.DarkGray, uncheckedTrackColor = Color.LightGray, uncheckedBorderColor = Color.Gray)
        }
        val segmentedButtonColors: SegmentedButtonColors = if (isDarkMode) {
                SegmentedButtonDefaults.colors(activeContainerColor = PresetColor.green, activeContentColor = Color.White, activeBorderColor = Color.DarkGray, inactiveContainerColor = Color.Black, inactiveContentColor = Color.White, inactiveBorderColor = Color.DarkGray)
        } else {
                SegmentedButtonDefaults.colors(activeContainerColor = PresetColor.green, activeContentColor = Color.White, activeBorderColor = Color.LightGray, inactiveContainerColor = Color.White, inactiveContentColor = Color.Black, inactiveBorderColor = Color.LightGray)
        }
        val version: String by lazy { BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")" }
        Column(
                modifier = Modifier
                        .background(
                                if (isDarkMode) {
                                        if (isHighContrastPreferred) AltPresetColor.keyboardDarkBackground else PresetColor.keyboardDarkBackground
                                } else {
                                        if (isHighContrastPreferred) AltPresetColor.keyboardLightBackground else PresetColor.keyboardLightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.paddingValue().dp)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_character_standard_traditional),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard == CharacterStandard.Traditional) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_character_standard_hongkong),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard == CharacterStandard.HongKong) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_character_standard_taiwan),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard == CharacterStandard.Taiwan) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_character_standard_simplified),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.isSimplified()) 1f else 0f),
                                                                tint = PresetColor.blue
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
                                                                checked = isAudioFeedbackOn,
                                                                onCheckedChange = {
                                                                        context.updateAudioFeedback(it)
                                                                },
                                                                colors = switchColors
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                                checked = isHapticFeedbackOn,
                                                                onCheckedChange = {
                                                                        context.updateHapticFeedback(it)
                                                                },
                                                                colors = switchColors
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
                                                text = stringResource(id = R.string.keyboard_settings_numeric_layout_switch_title),
                                                color = tintColor
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(
                                                checked = useTenKeyNumberPad,
                                                onCheckedChange = {
                                                        context.updateUseTenKeyNumberPad(it)
                                                },
                                                colors = switchColors
                                        )
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
                                                        checked = showLowercaseKeys,
                                                        onCheckedChange = {
                                                                context.updateShowLowercaseKeys(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode, isHighContrastPreferred)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_key_text_preview_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = previewKeyText,
                                                        onCheckedChange = {
                                                                context.updatePreviewKeyText(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode, isHighContrastPreferred)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Text(
                                                        text = stringResource(id = R.string.keyboard_settings_high_contrast_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = isHighContrastPreferred,
                                                        onCheckedChange = {
                                                                context.updateHighContrast(it)
                                                        },
                                                        colors = switchColors
                                                )
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
                                                        text = stringResource(id = R.string.keyboard_settings_globe_key_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = needsInputModeSwitchKey,
                                                        onCheckedChange = {
                                                                context.updateNeedsInputModeSwitchKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        checked = needsLeftKey,
                                                        onCheckedChange = {
                                                                context.updateNeedsLeftKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        checked = needsRightKey,
                                                        onCheckedChange = {
                                                                context.updateNeedsRightKey(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                }
                        }
                        item {
                                Row(
                                        modifier = Modifier
                                                .background(color = backColor, shape = RoundedCornerShape(6.dp))
                                                .padding(start = 8.dp, end = 4.dp)
                                                .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_bottom_padding_title),
                                                color = tintColor
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        SingleChoiceSegmentedButtonRow {
                                                ExtraBottomPadding.entries.forEachIndexed { index, level ->
                                                        SegmentedButton(
                                                                selected = extraBottomPadding == level,
                                                                onClick = { context.updateExtraBottomPadding(level) },
                                                                shape = SegmentedButtonDefaults.itemShape(index, ExtraBottomPadding.entries.count()),
                                                                colors = segmentedButtonColors,
                                                                icon = {}
                                                        ) {
                                                                Text(
                                                                        text = stringResource(id = when (level) {
                                                                                ExtraBottomPadding.None -> R.string.keyboard_settings_bottom_padding_none
                                                                                ExtraBottomPadding.Low -> R.string.keyboard_settings_bottom_padding_low
                                                                                ExtraBottomPadding.Medium -> R.string.keyboard_settings_bottom_padding_medium
                                                                                ExtraBottomPadding.High -> R.string.keyboard_settings_bottom_padding_high
                                                                        }),
                                                                        color = if (extraBottomPadding == level) Color.White else tintColor,
                                                                        fontWeight = FontWeight.Normal
                                                                )
                                                        }
                                                }
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_comment_style_above),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.isAbove()) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_comment_style_below),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.isBelow()) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_comment_style_none),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (commentStyle.isNone()) 1f else 0f),
                                                                tint = PresetColor.blue
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_cangjie_variant_cangjie5),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant == CangjieVariant.Cangjie5) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_cangjie_variant_cangjie3),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant == CangjieVariant.Cangjie3) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_cangjie_variant_quick5),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant == CangjieVariant.Quick5) 1f else 0f),
                                                                tint = PresetColor.blue
                                                        )
                                                }
                                                ResponsiveDivider(isDarkMode, isHighContrastPreferred)
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
                                                        Text(
                                                                text = stringResource(id = R.string.keyboard_settings_cangjie_variant_quick3),
                                                                fontWeight = FontWeight.Normal
                                                        )
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (cangjieVariant == CangjieVariant.Quick3) 1f else 0f),
                                                                tint = PresetColor.blue
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
                                                checked = isEmojiSuggestionsOn,
                                                onCheckedChange = {
                                                        context.updateEmojiSuggestionsState(it)
                                                },
                                                colors = switchColors
                                        )
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
                                                        text = stringResource(id = R.string.keyboard_settings_input_memory_switch_title),
                                                        color = tintColor
                                                )
                                                Spacer(modifier = Modifier.weight(1f))
                                                Switch(
                                                        checked = isInputMemoryOn,
                                                        onCheckedChange = {
                                                                context.updateInputMemoryState(it)
                                                        },
                                                        colors = switchColors
                                                )
                                        }
                                        ResponsiveDivider(isDarkMode, isHighContrastPreferred)
                                        Row(
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                TextButton(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                isTryingToClearInputMemory = true
                                                        },
                                                        colors = destructiveButtonColors,
                                                        contentPadding = PaddingValues(end = 12.dp)
                                                ) {
                                                        if (isTryingToClearInputMemory) {
                                                                Text(
                                                                        text = stringResource(id = R.string.keyboard_settings_clear_input_memory_message),
                                                                        fontWeight = FontWeight.Normal
                                                                )
                                                        } else {
                                                                Text(
                                                                        text = stringResource(id = R.string.keyboard_settings_clear_input_memory),
                                                                        fontWeight = FontWeight.Normal
                                                                )
                                                        }
                                                }
                                                if (isTryingToClearInputMemory) {
                                                        TextButton(
                                                                onClick = {
                                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                                                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                                                        } else {
                                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                        }
                                                                        context.clearInputMemory()
                                                                        coroutineScope.launch {
                                                                                delay(1000L) // 1s
                                                                                isTryingToClearInputMemory = false
                                                                        }
                                                                },
                                                                colors = destructiveButtonColors,
                                                                contentPadding = PaddingValues(horizontal = 12.dp)
                                                        ) {
                                                                Text(
                                                                        text = stringResource(id = R.string.keyboard_settings_clear_input_memory_confirm),
                                                                        fontWeight = FontWeight.Normal
                                                                )
                                                        }
                                                        TextButton(
                                                                onClick = {
                                                                        view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                        coroutineScope.launch {
                                                                                delay(300L) // 0.3s
                                                                                isTryingToClearInputMemory = false
                                                                        }
                                                                },
                                                                contentPadding = PaddingValues(horizontal = 12.dp)
                                                        ) {
                                                                Text(
                                                                        text = stringResource(id = R.string.keyboard_settings_clear_input_memory_cancel),
                                                                        fontWeight = FontWeight.Normal
                                                                )
                                                        }
                                                }
                                                Spacer(modifier = Modifier.weight(1f))
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
private fun ResponsiveDivider(isDarkMode: Boolean, isHighContrastPreferred: Boolean) {
        HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                thickness = 1.dp,
                color = if (isDarkMode) {
                        if (isHighContrastPreferred) AltPresetColor.keyDarkEmphatic else PresetColor.keyDarkEmphatic
                } else {
                        if (isHighContrastPreferred) AltPresetColor.keyLightEmphatic else PresetColor.keyLightEmphatic
                }
        )
}
