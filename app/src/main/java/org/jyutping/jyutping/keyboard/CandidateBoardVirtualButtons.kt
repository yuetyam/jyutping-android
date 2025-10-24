package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
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
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@Composable
fun CandidateBoardVirtualButtons(
        collapseWidth: Dp,
        collapseHeight: Dp,
        isDarkMode: Boolean,
        isHighContrastPreferred: Boolean
) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService

        IconButton(
                onClick = {
                        context.audioFeedback(SoundEffect.Back)
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        context.transformTo(KeyboardForm.Alphabetic)
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
                        .padding(top = 4.dp, end = 4.dp)
        ) {
                Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.chevron_up),
                        contentDescription = "Collapse",
                        modifier = Modifier
                                .padding(bottom = 4.dp, start = 4.dp)
                                .size(20.dp),
                        tint = if (isDarkMode) Color.White else Color.Black
                )
        }
}
