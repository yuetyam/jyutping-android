package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

@Composable
fun CantoneseTextLabel(text: String) {
        val unicode: String? = when (text.length) {
                0 -> null
                1 -> "U+" + text.first().code.toString(16).uppercase()
                else -> null
        }
        Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Row {
                        Text(text = "文字")
                        Text(text = ": ")
                        Text(text = text)
                }
                unicode?.let {
                        Text(
                                text = it,
                                color = MaterialTheme.colorScheme.secondary,
                                fontFamily = FontFamily.Monospace
                        )
                }
        }
}
