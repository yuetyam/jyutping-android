package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
        shouldContentMonospaced: Boolean = false,
        shouldSubContentMonospaced: Boolean = false
) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(text = heading, fontWeight = FontWeight.Medium)
                if (shouldContentMonospaced) {
                        Text(text = content, fontFamily = FontFamily.Monospace)
                } else {
                        Text(text = content)
                }
                if (subContent != null) {
                        if (shouldSubContentMonospaced) {
                                Text(text = subContent, fontFamily = FontFamily.Monospace)
                        } else {
                                Text(text = subContent)
                        }
                }
        }
}
