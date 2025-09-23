package org.jyutping.jyutping

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.DeprecatedSinceApi
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
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
import org.jyutping.jyutping.keyboard.KeyboardInterface
import org.jyutping.jyutping.keyboard.NumericKeyboard
import org.jyutping.jyutping.keyboard.QwertyForm
import org.jyutping.jyutping.keyboard.SettingsScreen
import org.jyutping.jyutping.keyboard.StrokeKeyboard
import org.jyutping.jyutping.keyboard.SymbolicKeyboard
import org.jyutping.jyutping.presets.PresetConstant
import org.jyutping.jyutping.tenkey.TenKeyNumericKeyboard
import splitties.systemservices.windowManager
import kotlin.math.min
import kotlin.math.roundToInt

class ComposeKeyboardView(context: Context) : AbstractComposeView(context) {

        @Composable
        override fun Content() {
                val ctx = context as JyutpingInputMethodService
                val isHapticFeedbackOn by ctx.isHapticFeedbackOn.collectAsState()
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
                        KeyboardForm.TenKeyNumeric -> TenKeyNumericKeyboard(height = keyboardHeight())
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
                val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                val windowMetrics = context.windowManager.currentWindowMetrics
                val bounds = windowMetrics.bounds
                val density = windowMetrics.density
                val windowWidth: Int = (bounds.width() / density).toInt()
                val windowHeight: Int = (bounds.height() / density).toInt()
                val minDimension: Int = min(windowWidth, windowHeight)
                val isPhone: Boolean = minDimension < 500
                val keyboardInterface: KeyboardInterface = when {
                        isPhone && isLandscape -> KeyboardInterface.PhoneLandscape
                        isPhone -> KeyboardInterface.PhonePortrait
                        isLandscape -> KeyboardInterface.PadLandscape
                        else -> KeyboardInterface.PadPortrait
                }
                (context as JyutpingInputMethodService).updateKeyboardInterface(keyboardInterface)
                if (keyboardInterface.isPhoneLandscape()) return 40.dp
                val keyHeight: Int = 53 + ((windowWidth - 300) / 20)
                return keyHeight.dp
        }

        @Composable
        @DeprecatedSinceApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private fun legacyResponsiveKeyHeight(): Dp {
                val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                val containerSize = LocalWindowInfo.current.containerSize
                val density = LocalDensity.current.density
                val windowWidth: Int = (containerSize.width / density).roundToInt()
                val windowHeight: Int = (containerSize.height / density).roundToInt()
                val minDimension = min(windowWidth, windowHeight)
                val isPhone: Boolean = minDimension < 500
                val keyboardInterface: KeyboardInterface = when {
                        isPhone && isLandscape -> KeyboardInterface.PhoneLandscape
                        isPhone -> KeyboardInterface.PhonePortrait
                        isLandscape -> KeyboardInterface.PadLandscape
                        else -> KeyboardInterface.PadPortrait
                }
                (context as JyutpingInputMethodService).updateKeyboardInterface(keyboardInterface)
                if (keyboardInterface.isPhoneLandscape()) return 40.dp
                val keyHeight: Int = 53 + ((windowWidth - 300) / 20)
                return keyHeight.dp
        }
}
