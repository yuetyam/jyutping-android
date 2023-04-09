package org.jyutping.jyutping.ui.cantonese

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.WebLinkLabel

@Composable
fun CantoneseScreen(navController: NavHostController) {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        NavigationLabel(icon = Icons.Outlined.Verified, text = stringResource(id = R.string.cantonese_label_expressions)) {
                                navController.navigate(route = Screen.Expressions.route)
                        }
                }
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "懶音診療室 - PolyU", uri = "https://www.polyu.edu.hk/cbs/pronunciation")
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "冚唪唥粵文", uri = "https://hambaanglaang.hk")
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "迴響", uri = "https://resonate.hk")
                        }
                }
        }
}
