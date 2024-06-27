package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SeparatorMark() {
        DisableSelection {
                Text(text = ": ")
        }
}
