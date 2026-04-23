package org.jyutping.jyutping.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jyutping.jyutping.presets.AppleColor

@Composable
fun TextCard(
        indicator: String = "i",
        indicatorColor: Color = AppleColor.blue,
        heading: String,
        content: String,
        subContent: String? = null,
        shouldMonospaceContent: Boolean = false,
        shouldMonospaceSubContent: Boolean = false
) {
        val density = LocalDensity.current
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                                color = colorScheme.background,
                                shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = indicator,
                                modifier = Modifier
                                        .border(
                                                width = 2.dp,
                                                color = indicatorColor,
                                                shape = CircleShape
                                        )
                                        .size(24.dp),
                                color = indicatorColor,
                                fontSize = with(density) { 15.dp.toSp() },
                                textAlign = TextAlign.Center
                        )
                        Text(
                                text = heading,
                                color = colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                        )
                }
                HorizontalDivider(
                        modifier = Modifier.padding(start = 32.dp, end = 2.dp),
                        thickness = 1.dp,
                        color = DividerDefaults.color.copy(alpha = 0.66f)
                )
                Text(
                        text = content,
                        color = colorScheme.onBackground,
                        fontFamily = if (shouldMonospaceContent) FontFamily.Monospace else FontFamily.Default
                )
                subContent?.let {
                        HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 2.dp),
                                thickness = 1.dp,
                                color = DividerDefaults.color.copy(alpha = 0.66f)
                        )
                        Text(
                                text = it,
                                color = colorScheme.onBackground,
                                fontFamily = if (shouldMonospaceSubContent) FontFamily.Monospace else FontFamily.Default
                        )
                }
        }
}
