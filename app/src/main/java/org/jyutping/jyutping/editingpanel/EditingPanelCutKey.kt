package org.jyutping.jyutping.editingpanel

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun EditingPanelCutKey(modifier: Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed = interactionSource.collectIsPressedAsState()
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode = remember { context.isDarkMode }
        Box(
                modifier = modifier
                        .clickable(interactionSource = interactionSource, indication = null) {
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                context.cutAllText()
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        modifier = Modifier
                                .padding(4.dp)
                                .shadow(
                                        elevation = 0.5.dp,
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .background(
                                        if (isDarkMode.value) {
                                                if (isPressed.value) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressed.value) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        }
                                )
                                .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Icon(
                                imageVector = Icons.Outlined.ContentCut,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isDarkMode.value) Color.White else Color.Black
                        )
                        Text(
                                text = stringResource(id = R.string.editing_panel_key_cut),
                                color = if (isDarkMode.value) Color.White else Color.Black,
                                fontSize = 11.sp,
                        )
                }
        }
}
