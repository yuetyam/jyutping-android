package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun TextCard(
        heading: String,
        content: String,
        subContent: String? = null,
        shouldMonospaceContent: Boolean = false,
        shouldMonospaceSubContent: Boolean = false
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                                color = colorScheme.background,
                                shape = RoundedCornerShape(size = 8.dp)
                        )
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(
                        text = heading,
                        color = colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                )
                HorizontalDivider()
                Text(
                        text = content,
                        color = colorScheme.onBackground,
                        fontFamily = if (shouldMonospaceContent) FontFamily.Monospace else FontFamily.Default
                )
                subContent?.let {
                        HorizontalDivider()
                        Text(
                                text = it,
                                color = colorScheme.onBackground,
                                fontFamily = if (shouldMonospaceSubContent) FontFamily.Monospace else FontFamily.Default
                        )
                }
        }
}
