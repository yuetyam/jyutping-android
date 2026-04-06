package org.jyutping.jyutping.stroke

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.JyutpingInputMethodService
import org.jyutping.jyutping.keyboard.CandidateScrollBar
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.tenkey.TenKeyBackspaceKey
import org.jyutping.jyutping.tenkey.TenKeyReturnKey
import org.jyutping.jyutping.tenkey.TenKeySpaceKey

@Composable
fun TailoredStrokeKeyboard(height: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        Column(
                modifier = Modifier
                        .background(
                                if (isHighContrastPreferred) {
                                        if (isDarkMode) AltPresetColor.darkBackground else AltPresetColor.lightBackground
                                } else {
                                        if (isDarkMode) PresetColor.darkBackground else PresetColor.lightBackground
                                }
                        )
                        .systemBarsPadding()
                        .padding(bottom = extraBottomPadding.applyingValue.dp)
                        .height(height)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
                                .height(PresetConstant.ToolBarHeight.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        CandidateScrollBar()
                }
                Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                        Column(
                                modifier = Modifier.weight(0.2f)
                        ) {
                                TailoredStrokePlaceholderKey(modifier = Modifier.weight(0.75f))
                                TailoredStrokePlaceholderKey(modifier = Modifier.weight(0.25f))
                        }
                        Column(
                                modifier = Modifier.weight(0.6f)
                        ) {
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredStrokeKey(StrokeVirtualKey.Horizontal, modifier = Modifier.weight(1f))
                                        TailoredStrokeKey(StrokeVirtualKey.Vertical, modifier = Modifier.weight(1f))
                                        TailoredStrokeKey(StrokeVirtualKey.LeftFalling, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredStrokeKey(StrokeVirtualKey.RightFalling, modifier = Modifier.weight(1f))
                                        TailoredStrokeKey(StrokeVirtualKey.Turning, modifier = Modifier.weight(1f))
                                        TailoredStrokeKey(StrokeVirtualKey.Wildcard, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredStrokePlaceholderKey(modifier = Modifier.weight(1f))
                                        TailoredStrokePlaceholderKey(modifier = Modifier.weight(1f))
                                        TailoredStrokePlaceholderKey(modifier = Modifier.weight(1f))
                                }
                                TenKeySpaceKey(modifier = Modifier.weight(0.25f))
                        }
                        Column(
                                modifier = Modifier.weight(0.2f)
                        ) {
                                TenKeyBackspaceKey(modifier = Modifier.weight(0.25f))
                                TailoredStrokePlaceholderKey(modifier = Modifier.weight(0.25f))
                                TenKeyReturnKey(modifier = Modifier.weight(0.5f))
                        }
                }
        }
}
