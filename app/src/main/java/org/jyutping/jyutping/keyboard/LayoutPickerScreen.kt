package org.jyutping.jyutping.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.R
import org.jyutping.jyutping.feedback.SoundEffect
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.models.KeyboardLayout
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor

@OptIn(ExperimentalGridApi::class)
@Composable
fun LayoutPickerScreen(height: Dp) {
        val view = LocalView.current
        val context = LocalContext.current as JyutpingInputMethodService
        val keyboardLayout by context.keyboardLayout.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val tintColor: Color = if (isDarkMode) Color.White else Color.Black
        fun select(layout: KeyboardLayout) {
                context.audioFeedback(SoundEffect.Click)
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                context.updateKeyboardLayout(layout)
        }
        Column(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.emphaticDark else AltPresetColor.emphaticLight
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.applyingValue.dp)
                        .height(height)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Row(
                        modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        AdvancedIconButton(
                                icon = Icons.Rounded.ArrowUpward
                        ) {
                                context.audioFeedback(SoundEffect.Back)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                context.transformTo(KeyboardForm.Alphabetic)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                                text = stringResource(id = R.string.keyboard_settings_navigation_hint),
                                modifier = Modifier.alpha(0f),
                                color = tintColor
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        AdvancedIconButton(
                                modifier = Modifier.alpha(0f),
                                icon = ImageVector.vectorResource(id = R.drawable.button_expand),
                                iconSize = 20.dp
                        ) {
                                context.audioFeedback(SoundEffect.Click)
                                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                }
                LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        item {
                                Grid(
                                        config = {
                                                repeat(times = 4) {
                                                        column(percentage = 0.25f)
                                                }
                                                repeat(times = 2) {
                                                        row(size = GridTrackSize.Auto)
                                                }
                                                rowGap(12.dp)
                                        },
                                        modifier = Modifier
                                                .border(
                                                        width = 1.dp,
                                                        color = Color.Transparent,
                                                        shape = RoundedCornerShape(20.dp)
                                                )
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                ) {
                                        LayoutOptionButton(layout = KeyboardLayout.Qwerty, isSelected = keyboardLayout.isQwerty, isDarkMode = isDarkMode) { select(KeyboardLayout.Qwerty) }
                                        LayoutOptionButton(layout = KeyboardLayout.TripleStroke, isSelected = keyboardLayout.isTripleStroke, isDarkMode = isDarkMode) { select(KeyboardLayout.TripleStroke) }
                                        LayoutOptionButton(layout = KeyboardLayout.NineKey, isSelected = keyboardLayout.isNineKey, isDarkMode = isDarkMode) { select(KeyboardLayout.NineKey) }
                                        Spacer(modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.size(64.dp))
                                        Spacer(modifier = Modifier.size(64.dp))
                                }
                        }
                }
        }
}

@Composable
private fun LayoutOptionButton(layout: KeyboardLayout, isSelected: Boolean, isDarkMode: Boolean, onClick: () -> Unit) {
        val accentColor = MaterialTheme.colorScheme.primary
        Column(
                modifier = Modifier.width(64.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                IconButton(
                        onClick = onClick,
                        modifier = Modifier
                                .size(45.dp)
                                .border(
                                        width = 2.dp,
                                        color = if (isSelected) accentColor else Color.Transparent,
                                        shape = CircleShape
                                ),
                        colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (isDarkMode) PresetColor.solidShallowDark else PresetColor.solidShallowLight,
                                contentColor = if (isSelected) accentColor else if (isDarkMode) Color.White else Color.Black
                        ),
                        shape = CircleShape
                ) {
                        Icon(
                                imageVector = ImageVector.vectorResource(id = if (isSelected) R.drawable.icon_keyboard_checked else R.drawable.icon_keyboard),
                                contentDescription = null,
                                modifier = Modifier.size(if (isSelected) 41.dp else 28.dp),
                        )
                }
                Text(
                        text = stringResource(
                                id = when (layout) {
                                        KeyboardLayout.Qwerty -> R.string.keyboard_layout_layout_qwerty
                                        KeyboardLayout.TripleStroke -> R.string.keyboard_layout_layout_triple_stroke
                                        KeyboardLayout.NineKey -> R.string.keyboard_layout_layout_nine_key
                                }
                        ),
                        color = if (isDarkMode) Color.White else Color.Black,
                        autoSize = TextAutoSize.StepBased(minFontSize = 9.sp, maxFontSize = 12.sp),
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                )
        }
}
