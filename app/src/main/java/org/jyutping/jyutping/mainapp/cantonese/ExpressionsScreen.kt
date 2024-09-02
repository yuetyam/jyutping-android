package org.jyutping.jyutping.mainapp.cantonese

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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                                        LabelEntry(type = LabelType.INFO, text = "佢 亦作 渠、𠍲{⿰亻渠}")
                                )
                        )
                }
                item {
                        DifferentView(
                                heading = "區分「係」以及「喺」",
                                lines = listOf(
                                        "係 hai6：謂語，義同「是」。",
                                        "喺 hai2：表方位、時間，義同「在」。",
                                        "例：我係曹阿瞞。",
                                        "例：我喺天后站落車。"
                                )
                        )
                }
                item {
                        DifferentView(
                                heading = "區分「諗」以及「冧」",
                                lines = listOf(
                                        "諗 nam2：想、思考、覺得。",
                                        "冧 lam3：表示倒塌、倒下。",
                                        "例：我諗緊今晚食咩。",
                                        "例：佢畀人㨃冧咗。"
                                )
                        )
                }
                item {
                        DifferentView(
                                heading = "區分「咁」以及「噉」",
                                lines = listOf(
                                        "咁 gam3，音同「禁」。",
                                        "噉 gam2，音同「錦」。",
                                        "例：我生得咁靚仔。",
                                        "例：噉又未必。"
                                )
                        )
                }
                item {
                        DifferentView(
                                heading = "推薦【嘅／個得噉】，避免【的得地】",
                                lines = listOf(
                                        "例：我嘅細佬／我個細佬。",
                                        "例：講得好！",
                                        "例：細細聲噉講話。"
                                )
                        )
                }
                item {
                        Expression(
                                heading = "推薦【啩、啊嘛】，避免【吧】",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "下個禮拜會出啩。"),
                                        LabelEntry(type = LabelType.CHECKED, text = "毋係啊嘛，真係冇？"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "下個禮拜會出吧。"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "毋係吧，真係冇？")
                                )
                        )
                }
                item {
                        Expression(
                                heading = "推薦【啦、嘞】，避免【了】",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "各位，我毋客氣啦。"),
                                        LabelEntry(type = LabelType.CHECKED, text = "係嘞，你試過箇間餐廳未啊？"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "各位，我毋客氣了。"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "係了，你試過箇間餐廳未啊？")
                                )
                        )
                }
                item {
                        Expression(
                                heading = "推薦【使】，避免【駛、洗】",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "毋使驚"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "毋駛驚"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "毋洗驚")
                                )
                        )
                }
                item {
                        Expression(
                                heading = "推薦【而家】，避免【宜家】",
                                labels = listOf(
                                        LabelEntry(type = LabelType.CHECKED, text = "我而家食緊飯。"),
                                        LabelEntry(type = LabelType.MISTAKE, text = "我宜家食緊飯。")
                                )
                        )
                }
        }
}

@Composable
private fun DifferentView(heading: String, lines: List<String>) {
        SelectionContainer {
                Column(
                        modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                                .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(
                                text = heading,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold
                        )
                        lines.map { Text(text = it, color = colorScheme.onBackground) }
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
                        .background(color = colorScheme.background, shape = RoundedCornerShape(size = 8.dp))
                        .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                SelectionContainer {
                        Text(
                                text = heading,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold
                        )
                }
                labels.map {
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
                Icon(
                        imageVector = image,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colorScheme.onBackground
                )
                SelectionContainer {
                        Text(text = entry.text, color = colorScheme.onBackground)
                }
        }
}