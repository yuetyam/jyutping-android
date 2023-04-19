package org.jyutping.jyutping.ui.cantonese

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpressionsScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        Expression(
                                heading = "第一人稱代詞",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "單數：我"),
                                        LabelEntry(type = LabelType.CHECKED, text = "複數：我哋（我等）"),
                                        LabelEntry(type = LabelType.WARNING, text = "毋用「咱、咱們」")
                                )
                        )
                }
                item {
                        Expression(
                                heading = "第二人稱代詞",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "單數：你"),
                                        LabelEntry(type = LabelType.CHECKED, text = "複數：你哋（你等）"),
                                        LabelEntry(type = LabelType.WARNING, text = "毋用「您」。「您」係北京方言用字，好少見於其他漢語。如果要用敬詞，粵語一般用「閣下」。"),
                                        LabelEntry(type = LabelType.WARNING, text = "毋推薦用「妳」，冇必要畫蛇添足。")
                                )
                        )
                }
                item {
                        Expression(
                                heading = "第三人稱代詞",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "單數：佢"),
                                        LabelEntry(type = LabelType.CHECKED, text = "複數：佢哋（佢等）"),
                                        LabelEntry(type = LabelType.INFO, text = "毋分性別、人、物，一律用佢。"),
                                        LabelEntry(type = LabelType.INFO, text = "佢 亦作 渠、⿰亻渠")
                                )
                        )
                }
        }
}

private enum class LabelType {
        CHECKED,
        INFO,
        MISTAKE,
        WARNING
}
private class LabelEntry(val type: LabelType, val text: String)

@Composable
private fun Expression(heading: String, labels: List<LabelEntry>) {
        Column(
                modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(text = heading, fontWeight = FontWeight.SemiBold)
                labels.onEach {
                        IconLabel(entry = it)
                }
        }
}

@Composable
private fun IconLabel(entry: LabelEntry) {
        val image: ImageVector = when (entry.type) {
                LabelType.CHECKED -> Icons.Outlined.CheckCircle
                LabelType.INFO -> Icons.Outlined.Info
                LabelType.MISTAKE -> Icons.Outlined.Cancel
                LabelType.WARNING -> Icons.Outlined.ErrorOutline
        }
        Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(imageVector = image, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(text = entry.text)
        }
}
