package org.jyutping.jyutping.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.ui.common.SeparatorMark

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
                        Text(
                                text = "文字",
                                color = colorScheme.onBackground
                        )
                        SeparatorMark()
                        Text(
                                text = text,
                                color = colorScheme.onBackground
                        )
                }
                unicode?.let {
                        Text(
                                text = it,
                                modifier = Modifier.alpha(0.75f),
                                color = colorScheme.onBackground,
                                fontFamily = FontFamily.Monospace
                        )
                }
        }
}
