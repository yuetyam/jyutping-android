package org.jyutping.jyutping

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.keyboard.KeyboardForm
import org.jyutping.jyutping.keyboard.AlphabeticKeyboard
import org.jyutping.jyutping.keyboard.CantoneseNumericKeyboard
import org.jyutping.jyutping.keyboard.CantoneseSymbolicKeyboard

class ComposeKeyboardView(context: Context) : AbstractComposeView(context) {

        @Composable
        override fun Content() {
                val keyHeight = 58.dp // TODO: Responsive keyHeight
                val ctx = context as JyutpingInputMethodService
                val keyboardForm = remember { ctx.keyboardForm }
                when (keyboardForm.value) {
                        KeyboardForm.Alphabetic -> AlphabeticKeyboard(keyHeight = keyHeight)
                        KeyboardForm.Numeric -> CantoneseNumericKeyboard(keyHeight = keyHeight)
                        KeyboardForm.Symbolic -> CantoneseSymbolicKeyboard(keyHeight = keyHeight)
                        else -> AlphabeticKeyboard(keyHeight = keyHeight)
                }
        }
}
