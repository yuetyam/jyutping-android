package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.utilities.ToolBox

@Composable
fun CandidateBoardPhysicalButtons() {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()

        // Expand/collapse button
        val currentForm by context.keyboardForm.collectAsState()
        val isExpanded = currentForm == KeyboardForm.CandidateBoard

        // Input mode switch button (Jyutping/ABC)
        val inputMethodMode by context.inputMethodMode.collectAsState()
        val characterStandard by context.characterStandard.collectAsState()
        Row(
                modifier = Modifier.padding(end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                AdvancedIconButton(
                        icon = ImageVector.vectorResource(id = if (isExpanded) R.drawable.button_collapse else R.drawable.button_expand)
                ) {
                        context.audioFeedback(SoundEffect.Click)
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        // Toggle between collapsed and expanded states
                        if (isExpanded) {
                                context.transformTo(KeyboardForm.Alphabetic)
                        } else {
                                context.transformTo(KeyboardForm.CandidateBoard)
                        }
                }
                IconButton(
                        onClick = {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.toggleInputMethodMode()
                        },
                        modifier = Modifier
                                .size(44.dp)
                                .border(
                                        width = 1.dp,
                                        color = ToolBox.keyBorderColor(isDarkMode, isHighContrastPreferred),
                                        shape = CircleShape
                                ),
                        colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                } else {
                                        if (isDarkMode) PresetColor.solidEmphaticDark else PresetColor.solidEmphaticLight
                                },
                                contentColor = if (isDarkMode) Color.White else Color.Black
                        ),
                        shape = CircleShape
                ) {
                        Text(
                                text = if (inputMethodMode.isCantonese) {
                                        if (characterStandard.isSimplified) "粤" else "粵"
                                } else {
                                        "A"
                                },
                                color = if (isDarkMode) Color.White else Color.Black,
                                fontSize = 18.sp
                        )
                }
                AdvancedIconButton(
                        icon = ImageVector.vectorResource(id = R.drawable.button_show_keyboard)
                ) {
                        context.audioFeedback(SoundEffect.Click)
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        context.showSoftKeyboard()
                }
        }
}
