package org.jyutping.jyutping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun IntroductionsScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        Text(text = "Introductions")
                }
        }
}

@Composable
fun HomeScreen(navController: NavHostController) {
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
                        Label(icon = Icons.Outlined.Info, text = stringResource(id = R.string.home_label_more_introductions), symbol = Icons.Rounded.ArrowForward) {
                                navController.navigate(route = Screen.Introductions.route)
                        }
                }
        }
}

@Composable
fun TextCard(heading: String, content: String, monospace: Boolean = false) {
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .padding(8.dp)
        ) {
                Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(text = heading, fontWeight = FontWeight.Medium)
                        if (monospace) {
                                Text(text = content, fontFamily = FontFamily.Monospace)
                        } else {
                                Text(text = content)
                        }
                }
        }
}

@Composable
fun Label(icon: ImageVector, text: String, symbol: ImageVector, onClick: () -> Unit = {}) {
        Row(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = colorScheme.background)
                        .clickable { onClick() }
                        .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(text = text)
                Spacer(modifier = Modifier.weight(1.0f))
                Icon(imageVector = symbol, contentDescription = null)
        }
}
