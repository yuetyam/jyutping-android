package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SeparatorMark() {
        DisableSelection {
                Text(
                        text = ": ",
                        color = colorScheme.secondary
                )
        }
}

@Composable
fun EnhancedHorizontalDivider() = HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
