package org.jyutping.jyutping

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val title: Int) {
        object Home : Screen(route = "home", icon = Icons.Outlined.Home, title = R.string.screen_title_home)
        object Jyutping : Screen(route = "jyutping", icon = Icons.Outlined.Search, title = R.string.screen_title_jyutping)
        object Cantonese : Screen(route = "cantonese", icon = Icons.Outlined.List, title = R.string.screen_title_cantonese)
        object About : Screen(route = "about", icon = Icons.Outlined.Info, title = R.string.screen_title_about)
}
