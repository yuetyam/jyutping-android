package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(
                        text = heading,
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider()
                if (shouldMonospaceContent) {
                        Text(
                                text = content,
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily.Monospace
                        )
                } else {
                        Text(
                                text = content,
                                color = colorScheme.onBackground,
                        )
                }
                if (subContent != null) {
                        HorizontalDivider()
                        if (shouldMonospaceSubContent) {
                                Text(
                                        text = subContent,
                                        color = colorScheme.onBackground,
                                        fontFamily = FontFamily.Monospace
                                )
                        } else {
                                Text(
                                        text = subContent,
                                        color = colorScheme.onBackground,
                                )
                        }
                }
        }
}
