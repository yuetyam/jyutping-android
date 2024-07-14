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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.CharacterStandard
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.extensions.keyboardLightBackground

@Composable
fun SettingsScreen(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val characterStandard = remember { context.characterStandard }
        val buttonColors = ButtonColors(Color.White, Color.Black, Color.White, Color.White)
        Column(
                modifier = Modifier
                        .background(Color.keyboardLightBackground)
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
                                        view.playSoundEffect(SoundEffectConstants.NAVIGATION_UP)
                                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        context.transformTo(KeyboardForm.Alphabetic)
                                }
                        ) {
                               Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
                        }
                        Text(text = stringResource(id = R.string.keyboard_settings_navigation_hint))
                        IconButton(
                                onClick = { /* TODO: Expansion */ },
                                modifier = Modifier.alpha(0f)
                        ) {
                                Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
                        }
                }
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                Column(
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                        Text(
                                                text = stringResource(id = R.string.keyboard_settings_character_standard_header),
                                                modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        Column(
                                                modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White)
                                                        .padding(horizontal = 8.dp)
                                                        .fillMaxWidth()
                                        ) {
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Traditional)
                                                        },
                                                        shape = RoundedCornerShape(4.dp),
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(0.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_traditional))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.Traditional) 1f else 0f)
                                                        )
                                                }
                                                HorizontalDivider(thickness = 1.dp)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.HongKong)
                                                        },
                                                        shape = RoundedCornerShape(4.dp),
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(0.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_hongkong))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.HongKong) 1f else 0f)
                                                        )
                                                }
                                                HorizontalDivider(thickness = 1.dp)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Taiwan)
                                                        },
                                                        shape = RoundedCornerShape(4.dp),
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(0.dp)
                                                ) {
                                                        Text(text = stringResource(id = R.string.keyboard_settings_character_standard_taiwan))
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                                imageVector = Icons.Outlined.Check,
                                                                contentDescription = null,
                                                                modifier = Modifier.alpha(if (characterStandard.value == CharacterStandard.Taiwan) 1f else 0f)
                                                        )
                                                }
                                                HorizontalDivider(thickness = 1.dp)
                                                Button(
                                                        onClick = {
                                                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                                                context.updateCharacterStandard(CharacterStandard.Simplified)
                                                        },
                                                        shape = RoundedCornerShape(4.dp),
                                                        colors = buttonColors,
                                                        contentPadding = PaddingValues(0.dp)
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
                                Text(text = "更多設定尚未實現")
                        }
                        item {
                                Text(text = "More settings are yet to be implemented")
                        }
                }
        }
}
