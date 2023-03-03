package org.jyutping.jyutping

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(var route: String, var icon: ImageVector, var title: String) {
        object Home : Screen(route = "home", icon = Icons.Outlined.Home, title = "Home")
        object Jyutping : Screen(route = "jyutping", icon = Icons.Outlined.Search, title = "Jyutping")
        object Cantonese : Screen(route = "cantonese", icon = Icons.Outlined.List, title = "Cantonese")
        object About : Screen(route = "about", icon = Icons.Outlined.Info, title = "About")
}
