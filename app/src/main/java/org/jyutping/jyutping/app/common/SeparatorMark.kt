package org.jyutping.jyutping.app.common

import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SeparatorMark() {
        DisableSelection {
                Text(
                        text = ": ",
                        color = colorScheme.secondary
                )
        }
}
