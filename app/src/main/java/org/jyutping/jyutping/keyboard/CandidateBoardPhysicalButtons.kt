package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun CandidateBoardPhysicalButtons(
        collapseWidth: Dp,
        collapseHeight: Dp,
        isDarkMode: Boolean,
        isHighContrastPreferred: Boolean
) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        
        Row(
                modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                // Expand/collapse button
                val currentForm by context.keyboardForm.collectAsState()
                val isExpanded = currentForm == KeyboardForm.CandidateBoard
                
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Back)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                // Toggle between collapsed and expanded states
                                if (isExpanded) {
                                        context.transformTo(KeyboardForm.Alphabetic)
                                } else {
                                        context.transformTo(KeyboardForm.CandidateBoard)
                                }
                        },
                        modifier = Modifier
                                .width(collapseWidth)
                                .height(collapseHeight)
                                .border(
                                        width = 1.dp,
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else Color.Transparent
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else Color.Transparent
                                        },
                                        shape = CircleShape
                                )
                                .background(
                                        color = if (isHighContrastPreferred) {
                                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                        } else {
                                                if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
                                        },
                                        shape = CircleShape
                                )
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(
                                        id = if (isExpanded) R.drawable.chevron_up else R.drawable.chevron_down
                                ),
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                modifier = Modifier
                                        .padding(bottom = 4.dp, start = 4.dp)
                                        .size(20.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
                
                // Keyboard button
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Back)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.showSoftKeyboard()
                        },
                        modifier = Modifier
                                .width(collapseWidth)
                                .height(collapseHeight)
                                .border(
                                        width = 1.dp,
                                        color = if (isDarkMode) {
                                                if (isHighContrastPreferred) Color.White else Color.Transparent
                                        } else {
                                                if (isHighContrastPreferred) Color.Black else Color.Transparent
                                        },
                                        shape = CircleShape
                                )
                                .background(
                                        color = if (isHighContrastPreferred) {
                                                if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                        } else {
                                                if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
                                        },
                                        shape = CircleShape
                                )
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.button_dismiss_keyboard),
                                contentDescription = "Show keyboard",
                                modifier = Modifier
                                        .padding(bottom = 4.dp, start = 4.dp)
                                        .size(20.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                        )
                }
        }
}
