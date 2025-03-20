package org.jyutping.jyutping.editingpanel

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun EditingPanelConversionKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        Column(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                context.convertAllText()
                        }
                        .padding(4.dp)
                        .border(
                                width = 1.dp,
                                color = if (isDarkMode) {
                                        if (isHighContrastPreferred) Color.White else Color.Transparent
                                } else {
                                        if (isHighContrastPreferred) Color.Black else Color.Transparent
                                },
                                shape = RoundedCornerShape(6.dp)
                        )
                        .shadow(
                                elevation = 0.5.dp,
                                shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                                color = backgroundColor(isDarkMode, isHighContrastPreferred, isPressed)
                        )
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Icon(
                        imageVector = Icons.Outlined.SyncAlt,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isDarkMode) Color.White else Color.Black
                )
                Text(
                        text = stringResource(id = R.string.editing_panel_key_conversion),
                        color = if (isDarkMode) Color.White else Color.Black,
                        fontSize = 11.sp,
                )
        }
}

private fun backgroundColor(isDarkMode: Boolean, isHighContrastPreferred: Boolean, isPressing: Boolean) =
        if (isDarkMode) {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyDark else AltPresetColor.keyDarkEmphatic
                } else {
                        if (isPressing) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                }
        } else {
                if (isHighContrastPreferred) {
                        if (isPressing) AltPresetColor.keyLight else AltPresetColor.keyLightEmphatic
                } else {
                        if (isPressing) PresetColor.keyLight else PresetColor.keyLightEmphatic
                }
        }
