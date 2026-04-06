package org.jyutping.jyutping.numeric

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
import org.jyutping.jyutping.keyboard.ToolBar
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.models.VirtualInputKey
import org.jyutping.jyutping.ninekey.NineKeyBackspaceKey
import org.jyutping.jyutping.ninekey.NineKeyNavigateKey
import org.jyutping.jyutping.ninekey.NineKeyReturnKey
import org.jyutping.jyutping.ninekey.NineKeySpaceKey
import org.jyutping.jyutping.ninekey.SidebarPanel
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun TailoredNumericKeyboard(height: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val keyHeight: Dp = (height - PresetConstant.ToolBarHeight.dp) / 4f
        val sidebarUnitHeight: Dp = keyHeight * 3f / 4f
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
                        ToolBar()
                }
                Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                        Column(
                                modifier = Modifier.weight(0.2f)
                        ) {
                                SidebarPanel(
                                        unitHeight = sidebarUnitHeight,
                                        modifier = Modifier.weight(0.75f)
                                )
                                NineKeyNavigateKey(
                                        destination = KeyboardForm.Alphabetic,
                                        modifier = Modifier.weight(0.25f)
                                )
                        }
                        Column(
                                modifier = Modifier.weight(0.6f)
                        ) {
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredNumberKey(VirtualInputKey.number1, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number2, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number3, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredNumberKey(VirtualInputKey.number4, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number5, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number6, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredNumberKey(VirtualInputKey.number7, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number8, modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number9, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        TailoredNumericDotKey(modifier = Modifier.weight(1f))
                                        TailoredNumberKey(VirtualInputKey.number0, modifier = Modifier.weight(1f))
                                        NineKeySpaceKey(modifier = Modifier.weight(1f))
                                }
                        }
                        Column(
                                modifier = Modifier.weight(0.2f)
                        ) {
                                NineKeyBackspaceKey(modifier = Modifier.weight(0.25f))
                                NineKeyNavigateKey(
                                        destination = KeyboardForm.Numeric,
                                        modifier = Modifier.weight(0.25f)
                                )
                                NineKeyReturnKey(modifier = Modifier.weight(0.5f))
                        }
                }
        }
}
