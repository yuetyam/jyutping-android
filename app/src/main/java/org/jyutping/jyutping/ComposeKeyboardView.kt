package org.jyutping.jyutping

import android.content.Context
import android.os.Build
import androidx.annotation.DeprecatedSinceApi
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.editingpanel.EditingPanel
import org.jyutping.jyutping.emoji.EmojiBoard
import org.jyutping.jyutping.keyboard.AlphabeticKeyboard
import org.jyutping.jyutping.keyboard.CandidateBoard
import org.jyutping.jyutping.keyboard.CangjieKeyboard
import org.jyutping.jyutping.keyboard.CantoneseNumericKeyboard
import org.jyutping.jyutping.keyboard.CantoneseSymbolicKeyboard
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.NumericKeyboard
import org.jyutping.jyutping.keyboard.QwertyForm
import org.jyutping.jyutping.keyboard.SettingsScreen
import org.jyutping.jyutping.keyboard.StrokeKeyboard
import org.jyutping.jyutping.keyboard.SymbolicKeyboard
import org.jyutping.jyutping.presets.PresetConstant
import splitties.systemservices.windowManager

class ComposeKeyboardView(context: Context) : AbstractComposeView(context) {

        @Composable
        override fun Content() {
                val ctx = context as JyutpingInputMethodService
                val isAudioFeedbackOn by ctx.isAudioFeedbackOn.collectAsState()
                val isHapticFeedbackOn by ctx.isHapticFeedbackOn.collectAsState()
                LocalView.current.isSoundEffectsEnabled = isAudioFeedbackOn
                LocalView.current.isHapticFeedbackEnabled = isHapticFeedbackOn
                val keyboardForm by ctx.keyboardForm.collectAsState()
                val qwertyForm by ctx.qwertyForm.collectAsState()
                val inputMethodMode by ctx.inputMethodMode.collectAsState()
                when (keyboardForm) {
                        KeyboardForm.Alphabetic -> when (qwertyForm) {
                                QwertyForm.Cangjie -> CangjieKeyboard(keyHeight = responsiveKeyHeight())
                                QwertyForm.Stroke -> StrokeKeyboard(keyHeight = responsiveKeyHeight())
                                else -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight())
                        }
                        KeyboardForm.CandidateBoard -> CandidateBoard(height = keyboardHeight())
                        KeyboardForm.Numeric -> if (inputMethodMode.isABC()) NumericKeyboard(keyHeight = responsiveKeyHeight()) else CantoneseNumericKeyboard(keyHeight = responsiveKeyHeight())
                        KeyboardForm.Symbolic -> if (inputMethodMode.isABC()) SymbolicKeyboard(keyHeight = responsiveKeyHeight()) else CantoneseSymbolicKeyboard(keyHeight = responsiveKeyHeight())
                        KeyboardForm.Settings -> SettingsScreen(height = keyboardHeight())
                        KeyboardForm.EmojiBoard -> EmojiBoard(height = keyboardHeight())
                        KeyboardForm.EditingPanel -> EditingPanel(height = keyboardHeight())
                        else -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight())
                }
        }

        @Composable
        private fun keyboardHeight(): Dp {
                val keyRowHeight: Dp = responsiveKeyHeight()
                val summedHeight: Dp = keyRowHeight * 4
                return summedHeight + PresetConstant.ToolBarHeight.dp
        }

        @Composable
        private fun responsiveKeyHeight(): Dp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) api34ResponsiveKeyHeight() else legacyResponsiveKeyHeight()

        @Composable
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private fun api34ResponsiveKeyHeight(): Dp {
                val windowMetrics = context.windowManager.currentWindowMetrics
                val bounds = windowMetrics.bounds
                val density = windowMetrics.density
                val screenWidth: Int = (bounds.width() / density).toInt()
                val screenHeight: Int = (bounds.height() / density).toInt()
                val isPhoneLandscape: Boolean = (screenHeight < 500) && (screenWidth > screenHeight)
                if (isPhoneLandscape) return 40.dp
                val value: Int = 52 + ((screenWidth - 300) / 20)
                return value.dp
        }

        @Composable
        @DeprecatedSinceApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private fun legacyResponsiveKeyHeight(): Dp {
                val screenWidth = LocalConfiguration.current.screenWidthDp
                val screenHeight = LocalConfiguration.current.screenHeightDp
                val isPhoneLandscape: Boolean = (screenHeight < 500) && (screenWidth > screenHeight)
                if (isPhoneLandscape) return 40.dp
                val value: Int = 52 + ((screenWidth - 300) / 20)
                return value.dp
        }
}
