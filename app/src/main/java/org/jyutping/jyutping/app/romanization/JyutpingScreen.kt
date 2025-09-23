package org.jyutping.jyutping.app.romanization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
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
import org.jyutping.jyutping.ui.common.EnhancedHorizontalDivider
import org.jyutping.jyutping.ui.common.NavigationLabel
import org.jyutping.jyutping.ui.common.WebLinkLabel

@Composable
fun JyutpingScreen(navController: NavHostController) {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                NavigationLabel(icon = Icons.AutoMirrored.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_initials)) {
                                        navController.navigate(route = Screen.JyutpingInitials.route)
                                }
                                EnhancedHorizontalDivider()
                                NavigationLabel(icon = Icons.AutoMirrored.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_finals)) {
                                        navController.navigate(route = Screen.JyutpingFinals.route)
                                }
                                EnhancedHorizontalDivider()
                                NavigationLabel(icon = Icons.Outlined.Notifications, text = stringResource(id = R.string.jyutping_label_tones)) {
                                        navController.navigate(route = Screen.JyutpingTones.route)
                                }
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵音資料集叢", uri = "https://jyut.net")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵典", uri = "https://words.hk")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "粵語審音配詞字庫", uri = "https://humanum.arts.cuhk.edu.hk/Lexis/lexi-can")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Search, text = "羊羊粵語", uri = "https://shyyp.net/hant")
                        }
                }
                item {
                        Column(
                                modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(colorScheme.background)
                                        .fillMaxWidth()
                        ) {
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "粵拼 Jyutping", uri = "https://jyutping.org")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "粵語拼音速遞 - CUHK", uri = "https://www.ilc.cuhk.edu.hk/workshop/Chinese/Cantonese/Romanization")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "粵語網路課堂 - CUHK", uri = "https://www.ilc.cuhk.edu.hk/workshop/Chinese/Cantonese/OnlineTutorial")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "翻轉粵語教室 - PolyU", uri = "https://www.polyu.edu.hk/clc/cantonese/home")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "Zidou - 粵拼版 Wordle", uri = "https://chaaklau.github.io/zidou")
                                EnhancedHorizontalDivider()
                                WebLinkLabel(icon = Icons.Outlined.Public, text = "六合 | 粵拼版 Wordle", uri = "https://lukhap.jonathanl.dev")
                        }
                }
        }
}
