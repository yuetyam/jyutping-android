package org.jyutping.jyutping.ninekey

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
import org.jyutping.jyutping.keyboard.ToolBar
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.presets.AltPresetColor
import org.jyutping.jyutping.presets.PresetColor
import org.jyutping.jyutping.presets.PresetConstant

@Composable
fun NineKeyKeyboard(keyHeight: Dp) {
        val context = LocalContext.current as JyutpingInputMethodService
        val isBuffering by context.isBuffering.collectAsState()
        val useNineKeyNumberPad by context.useNineKeyNumberPad.collectAsState()
        val needsInputModeSwitchKey by context.needsInputModeSwitchKey.collectAsState()
        val isDarkMode by context.isDarkMode.collectAsState()
        val isHighContrastPreferred by context.isHighContrastPreferred.collectAsState()
        val extraBottomPadding by context.extraBottomPadding.collectAsState()
        val totalHeight: Dp = (keyHeight * 4) + PresetConstant.ToolBarHeight.dp
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
                        .height(totalHeight)
                        .fillMaxWidth()
        ) {
                Box(
                        modifier = Modifier
                                .height(PresetConstant.ToolBarHeight.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        if (isBuffering) {
                                CandidateScrollBar()
                        } else {
                                ToolBar()
                        }
                }
                Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                        Column(
                                modifier = Modifier.weight(0.188f)
                        ) {
                                SidebarPanel(
                                        unitHeight = sidebarUnitHeight,
                                        modifier = Modifier.weight(0.75f)
                                )
                                NineKeyNavigateKey(
                                        destination = if (useNineKeyNumberPad) KeyboardForm.NineKeyNumeric else KeyboardForm.Numeric,
                                        modifier = Modifier.weight(0.25f)
                                )
                        }
                        Column(
                                modifier = Modifier.weight(0.624f)
                        ) {
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        NineKeySpecialKey(modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.ABC, modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.DEF, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        NineKeyInputKey(Combo.GHI, modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.JKL, modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.MNO, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        NineKeyInputKey(Combo.PQRS, modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.TUV, modifier = Modifier.weight(1f))
                                        NineKeyInputKey(Combo.WXYZ, modifier = Modifier.weight(1f))
                                }
                                Row(
                                        modifier = Modifier.weight(0.25f)
                                ) {
                                        if (needsInputModeSwitchKey) {
                                                NineKeyGlobeKey(modifier = Modifier.weight(0.33f))
                                                NineKeySpaceKey(modifier = Modifier.weight(0.67f))
                                        } else {
                                                NineKeySpaceKey(modifier = Modifier.weight(1f))
                                        }
                                }
                        }
                        Column(
                                modifier = Modifier.weight(0.188f)
                        ) {
                                NineKeyBackspaceKey(modifier = Modifier.weight(0.25f))
                                NineKeyNavigateKey(
                                        destination = if (useNineKeyNumberPad) KeyboardForm.Numeric else KeyboardForm.Symbolic,
                                        modifier = Modifier.weight(0.25f)
                                )
                                NineKeyReturnKey(modifier = Modifier.weight(0.5f))
                        }
                }
        }
}
