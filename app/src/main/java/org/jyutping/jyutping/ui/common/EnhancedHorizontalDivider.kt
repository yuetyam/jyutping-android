package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnhancedHorizontalDivider() = HorizontalDivider(
        modifier = Modifier.padding(start = 44.dp, end = 10.dp),
        thickness = 1.dp,
        color = DividerDefaults.color.copy(alpha = 0.66f)
)
