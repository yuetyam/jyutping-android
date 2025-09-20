package org.jyutping.jyutping.app.cantonese

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                NavigationLabel(icon = Icons.Outlined.Verified, text = stringResource(id = R.string.cantonese_label_expressions)) {
                                        navController.navigate(route = Screen.Expressions.route)
                                }
                                HorizontalDivider()
                                NavigationLabel(icon = Icons.AutoMirrored.Outlined.List, text = stringResource(id = R.string.cantonese_label_confusion)) {
                                        navController.navigate(route = Screen.Confusion.route)
                                }
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "懶音診療室 - PolyU", uri = "https://www.polyu.edu.hk/cbs/pronunciation")
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "粵語語氣詞", uri = "https://jyutping.org/blog/particles")
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "CANTONESE.com.hk", uri = "https://www.cantonese.com.hk")
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "中國古詩文精讀", uri = "https://www.classicalchineseliterature.org")
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "冚唪唥粵文", uri = "https://hambaanglaang.hk")
                                HorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "迴響", uri = "https://www.resonatehk.com")
                        }
                }
        }
}
