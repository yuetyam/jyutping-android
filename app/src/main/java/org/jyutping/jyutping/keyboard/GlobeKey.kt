package org.jyutping.jyutping.keyboard

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun GlobeKey(modifier: Modifier) {
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
                                val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                manager.showInputMethodPicker()
                        }
                        .fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                Box(
                        modifier = modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .background(
                                        color = if (isDarkMode.value) {
                                                if (isPressed.value) PresetColor.keyDark else PresetColor.keyDarkEmphatic
                                        } else {
                                                if (isPressed.value) PresetColor.keyLight else PresetColor.keyLightEmphatic
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                )
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.key_globe),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isDarkMode.value) Color.White else Color.Black
                        )
                }
        }
}
