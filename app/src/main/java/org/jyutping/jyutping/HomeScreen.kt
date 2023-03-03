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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                                heading = "Tones Input",
                                content = "v = 1, vv = 4\nx = 2, xx = 5\nq = 3, qq = 6",
                                monospace = true
                        )
                }
                item {
                        TextCard(
                                heading = "Reverse Lookup Jyutping by Cangjie",
                                content = "以 v 開始，再輸入倉頡碼即可。"
                        )
                }
                item {
                        Row(
                                modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clip(shape = RoundedCornerShape(size = 8.dp))
                                        .background(color = Color.White)
                                        .padding(8.dp)
                        ) {
                                Icon(
                                        Icons.Outlined.Info,
                                        contentDescription = "",
                                )
                                Text(
                                        text = "More Introduction",
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Icon(
                                        Icons.Rounded.ArrowForward,
                                        contentDescription = "",
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
                        .background(color = Color.White)
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
