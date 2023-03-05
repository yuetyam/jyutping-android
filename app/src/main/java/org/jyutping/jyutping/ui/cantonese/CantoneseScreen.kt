package org.jyutping.jyutping.ui.cantonese

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jyutping.jyutping.ui.common.WebLinkLabel

@Composable
fun CantoneseScreen() {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "冚唪唥粵文", uri = "https://hambaanglaang.hk")
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "學識 Hok6", uri = "https://www.hok6.com")
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "迴響", uri = "https://resonate.hk")

                        }
                }
        }
}
