package org.jyutping.jyutping

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavController) {
        val screens: List<Screen> = listOf(
                Screen.Home,
                Screen.Jyutping,
                Screen.Cantonese,
                Screen.About
        )
        NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                screens.forEach { screen ->
                        NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                        navController.navigate(screen.route) {
                                                navController.graph.startDestinationRoute?.let { route ->
                                                        popUpTo(route) {
                                                                saveState = true
                                                        }
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                        }
                                },
                                icon = { Icon(imageVector = screen.icon, contentDescription = stringResource(id = screen.title)) },
                                label = { Text(text = stringResource(id = screen.title)) }
                        )
                }
        }
}
