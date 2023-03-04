package org.jyutping.jyutping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_tones_input),
                                content = stringResource(id = R.string.home_content_tones_input),
                                monospace = true
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_pinyin_reverse_lookup),
                                content = stringResource(id = R.string.home_content_pinyin_reverse_lookup)
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_stroke_reverse_lookup),
                                content = stringResource(id = R.string.home_content_stroke_reverse_lookup)
                        )
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_stroke_code),
                                content = "w = 橫(waang)\ns = 豎(syu)\na = 撇\nd = 點(dim)\nz = 折(zit)",
                                monospace = true
                        )
                }
                item {
                        TextCard(
                                heading = stringResource(id = R.string.home_heading_compose_reverse_lookup),
                                content = stringResource(id = R.string.home_content_compose_reverse_lookup)
                        )
                }
                item {
                        Row(
                                modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clip(shape = RoundedCornerShape(size = 8.dp))
                                        .background(color = colorScheme.secondaryContainer)
                                        .padding(8.dp)
                        ) {
                                Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = null,
                                )
                                Text(
                                        text = stringResource(id = R.string.home_label_more_introductions),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Icon(
                                        Icons.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.fillMaxSize())
                        }
                }
        }
}

@Composable
fun TextCard(heading: String, content: String, monospace: Boolean = false) {
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.secondaryContainer)
                        .padding(8.dp)
        ) {
                Column {
                        Text(
                                text = heading,
                                modifier = Modifier.padding(bottom = 8.dp),
                                fontWeight = FontWeight.Medium,
                        )
                        Text(
                                text = content,
                                fontFamily = if (monospace) FontFamily.Monospace else FontFamily.Default
                        )
                }
                Spacer(modifier = Modifier.fillMaxSize())
        }
}
