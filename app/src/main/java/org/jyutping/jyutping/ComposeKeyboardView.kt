package org.jyutping.jyutping

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.keyboard.KeyboardScreen

class ComposeKeyboardView(context: Context) : AbstractComposeView(context) {

        @Composable
        override fun Content() {
                KeyboardScreen(keyHeight = 58.dp)
        }
}
