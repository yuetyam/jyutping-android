package org.jyutping.jyutping

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.DeprecatedSinceApi
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jyutping.jyutping.editingpanel.EditingPanel
import org.jyutping.jyutping.emoji.EmojiBoard
import org.jyutping.jyutping.keyboard.AlphabeticKeyboard
import org.jyutping.jyutping.keyboard.CandidateBoard
import org.jyutping.jyutping.keyboard.CangjieKeyboard
import org.jyutping.jyutping.keyboard.CantoneseNumericKeyboard
import org.jyutping.jyutping.keyboard.CantoneseSymbolicKeyboard
import org.jyutping.jyutping.keyboard.CommentStyle
import org.jyutping.jyutping.models.InputMethodMode
import org.jyutping.jyutping.models.KeyboardForm
import org.jyutping.jyutping.models.KeyboardInterface
import org.jyutping.jyutping.keyboard.NumericKeyboard
import org.jyutping.jyutping.keyboard.PhysicalKeyboardCandidateBar
import org.jyutping.jyutping.keyboard.QwertyForm
import org.jyutping.jyutping.keyboard.SettingsScreen
import org.jyutping.jyutping.keyboard.StrokeKeyboard
import org.jyutping.jyutping.keyboard.SymbolicKeyboard
import org.jyutping.jyutping.keyboard.TripleStrokeKeyboard
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

                // Check if physical keyboard is active
                val isPhysicalKeyboard by ctx.isPhysicalKeyboardActive.collectAsState()

                // Observe physical key preview and clear after short timeout
                val lastPhysicalKey by ctx.lastPhysicalKey.collectAsState()
                LaunchedEffect(lastPhysicalKey) {
                        if (lastPhysicalKey != null) {
                                delay(250L)
                                ctx.lastPhysicalKey.value = null
                        }
                }

                // If physical keyboard is active, show candidates view (collapsed or expanded)
                if (isPhysicalKeyboard) {
                        val keyboardForm by ctx.keyboardForm.collectAsState()
                        val commentStyle by ctx.commentStyle.collectAsState()

                        // Check if we're in expanded mode
                        val isExpanded = keyboardForm == KeyboardForm.CandidateBoard

                        if (isExpanded) {
                                // Expanded mode: show full candidate board
                                // val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                                // val expandedHeight = screenHeight * 0.5f // 50% of screen height
                                val expandedHeight = keyboardHeight(0)
                                CandidateBoard(height = expandedHeight, isPhysicalKeyboard = true)
                        } else {
                                // Collapsed mode: show horizontal scrolling candidates
                                // Increased height to prevent number labels from overlapping with Jyutping romanization
                                val collapsedHeight = when (commentStyle) {
                                        CommentStyle.AboveCandidates, CommentStyle.BelowCandidates -> 56.dp
                                        else -> 50.dp
                                }
                                PhysicalKeyboardCandidateBar(height = collapsedHeight)
                        }
                        return
                }

                val keyboardForm by ctx.keyboardForm.collectAsState()
                val qwertyForm by ctx.qwertyForm.collectAsState()
                val inputMethodMode by ctx.inputMethodMode.collectAsState()
                val keyOffset by ctx.keyHeightOffset.collectAsState()
                when (keyboardForm) {
                        KeyboardForm.Alphabetic -> when (qwertyForm) {
                                QwertyForm.Cangjie -> CangjieKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                QwertyForm.Stroke -> StrokeKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                QwertyForm.TripleStroke -> when (inputMethodMode) {
                                        InputMethodMode.ABC -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                        InputMethodMode.Cantonese -> TripleStrokeKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                }
                                else -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                        }
                        KeyboardForm.Numeric -> when (inputMethodMode) {
                                InputMethodMode.ABC -> NumericKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                InputMethodMode.Cantonese -> CantoneseNumericKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                        }
                        KeyboardForm.Symbolic -> when (inputMethodMode) {
                                InputMethodMode.ABC -> SymbolicKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                                InputMethodMode.Cantonese -> CantoneseSymbolicKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                        }
                        KeyboardForm.TenKeyNumeric -> TenKeyNumericKeyboard(height = keyboardHeight(keyOffset))
                        KeyboardForm.CandidateBoard -> CandidateBoard(height = keyboardHeight(keyOffset))
                        KeyboardForm.Settings -> SettingsScreen(height = keyboardHeight(keyOffset))
                        KeyboardForm.EmojiBoard -> EmojiBoard(height = keyboardHeight(keyOffset))
                        KeyboardForm.EditingPanel -> EditingPanel(height = keyboardHeight(keyOffset))
                        else -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight(keyOffset))
                }
        }

        @Composable
        private fun keyboardHeight(keyOffset: Int): Dp {
                val keyRowHeight: Dp = responsiveKeyHeight(keyOffset)
                val summedHeight: Dp = keyRowHeight * 4
                return summedHeight + PresetConstant.ToolBarHeight.dp
        }

        @Composable
        private fun responsiveKeyHeight(offset: Int): Dp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) api34ResponsiveKeyHeight(offset) else legacyResponsiveKeyHeight(offset)

        @Composable
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private fun api34ResponsiveKeyHeight(offset: Int): Dp {
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
                if (keyboardInterface.isPhoneLandscape) return 40.dp
                val keyHeight: Int = 53 + ((windowWidth - 300) / 20)
                return (keyHeight + offset).dp
        }

        @Composable
        @DeprecatedSinceApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        private fun legacyResponsiveKeyHeight(offset: Int): Dp {
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
                if (keyboardInterface.isPhoneLandscape) return 40.dp
                val keyHeight: Int = 53 + ((windowWidth - 300) / 20)
                return (keyHeight + offset).dp
        }
}
