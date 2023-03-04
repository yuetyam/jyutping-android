package org.jyutping.jyutping.ui.romanization

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jyutping.jyutping.R
import org.jyutping.jyutping.Screen
import org.jyutping.jyutping.ui.common.NavigationLabel

@Composable
fun JyutpingScreen(navController: NavHostController) {
        LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
                item {
                        NavigationLabel(icon = Icons.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_initials)) {
                                navController.navigate(route = Screen.JyutpingInitials.route)
                        }
                }
                item {
                        NavigationLabel(icon = Icons.Outlined.FormatListBulleted, text = stringResource(id = R.string.jyutping_label_finals)) {
                                navController.navigate(route = Screen.JyutpingFinals.route)
                        }
                }
                item {
                        NavigationLabel(icon = Icons.Outlined.Notifications, text = stringResource(id = R.string.jyutping_label_tones)) {
                                navController.navigate(route = Screen.JyutpingTones.route)
                        }
                }
        }
}
