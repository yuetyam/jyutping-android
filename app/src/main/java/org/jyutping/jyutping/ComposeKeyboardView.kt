package org.jyutping.jyutping

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.AlphabeticKeyboard
import org.jyutping.jyutping.keyboard.CantoneseNumericKeyboard
import org.jyutping.jyutping.keyboard.CantoneseSymbolicKeyboard
import org.jyutping.jyutping.keyboard.EditingPanel
import org.jyutping.jyutping.keyboard.EmojiBoard
import org.jyutping.jyutping.keyboard.SettingsScreen

class ComposeKeyboardView(context: Context) : AbstractComposeView(context) {

        @Composable
        override fun Content() {
                val keyboardForm = remember { (context as JyutpingInputMethodService).keyboardForm }
                when (keyboardForm.value) {
                        KeyboardForm.Alphabetic -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight())
                        KeyboardForm.Numeric -> CantoneseNumericKeyboard(keyHeight = responsiveKeyHeight())
                        KeyboardForm.Symbolic -> CantoneseSymbolicKeyboard(keyHeight = responsiveKeyHeight())
                        KeyboardForm.Settings -> SettingsScreen(height = keyboardHeight())
                        KeyboardForm.EmojiBoard -> EmojiBoard(height = keyboardHeight())
                        KeyboardForm.EditingPanel -> EditingPanel(height = keyboardHeight())
                        else -> AlphabeticKeyboard(keyHeight = responsiveKeyHeight())
                }
        }

        @Composable
        private fun keyboardHeight(): Dp {
                val toolBarHeight = 60.dp
                val keyRowsHeight = responsiveKeyHeight() * 4
                return keyRowsHeight + toolBarHeight
        }

        @Composable
        private fun responsiveKeyHeight(): Dp {
                val screenWidth = LocalConfiguration.current.screenWidthDp
                val screenHeight = LocalConfiguration.current.screenHeightDp
                if (screenWidth > screenHeight) return 40.dp
                val value: Int = 50 + ((screenWidth - 300) / 20)
                return value.dp
        }
}
