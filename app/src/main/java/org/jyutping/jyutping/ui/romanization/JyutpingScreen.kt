package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
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
fun JyutpingScreen(navController: NavHostController) {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                                NavigationLabel(icon = Icons.AutoMirrored.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_initials)) {
                                        navController.navigate(route = Screen.JyutpingInitials.route)
                                }
                                NavigationLabel(icon = Icons.AutoMirrored.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_finals)) {
                                        navController.navigate(route = Screen.JyutpingFinals.route)
                                }
                                NavigationLabel(icon = Icons.Outlined.Notifications, text = stringResource(id = R.string.jyutping_label_tones)) {
                                        navController.navigate(route = Screen.JyutpingTones.route)
                                }
                        }
                }
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵音資料集叢", uri = "https://jyut.net")
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵典", uri = "https://words.hk")
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵語審音配詞字庫", uri = "https://humanum.arts.cuhk.edu.hk/Lexis/lexi-can")
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "泛粵典", uri = "https://www.jyutdict.org")
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "羊羊粵語", uri = "https://shyyp.net/hant")
                        }
                }
                item {
                        Column(
                                modifier = Modifier.padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "粵拼 Jyutping", uri = "https://jyutping.org")
                                WebLinkLabel(icon = Icons.Outlined.Link, text = "粵語拼音速遞 - CUHK", uri = "https://www.ilc.cuhk.edu.hk/workshop/Chinese/Cantonese/Romanization")
                        }
                }
        }
}
