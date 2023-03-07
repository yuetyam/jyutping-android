package org.jyutping.jyutping

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Public
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector = Icons.Outlined.Info, val title: Int) {
        object Home : Screen(route = "home", icon = Icons.Outlined.Home, title = R.string.screen_title_home)
        object Jyutping : Screen(route = "jyutping", icon = Icons.Outlined.Description, title = R.string.screen_title_jyutping)
        object Cantonese : Screen(route = "cantonese", icon = Icons.Outlined.Public, title = R.string.screen_title_cantonese)
        object About : Screen(route = "about", icon = Icons.Outlined.Info, title = R.string.screen_title_about)
        object Introductions : Screen(route = "introductions", title = R.string.screen_title_introductions)
        object JyutpingInitials: Screen(route = "initials", title = R.string.jyutping_screen_title_initials)
        object JyutpingFinals: Screen(route = "finals", title = R.string.jyutping_screen_title_finals)
        object JyutpingTones: Screen(route = "tones", title = R.string.jyutping_screen_title_tones)
        object Expressions: Screen(route = "expressions", title = R.string.cantonese_screen_title_expressions)
}
