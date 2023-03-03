package org.jyutping.jyutping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jyutping.jyutping.ui.theme.JyutpingTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                        val navController = rememberNavController()
                        val entry by navController.currentBackStackEntryAsState()
                        val route: String? = entry?.destination?.route
                        val topTitle: String = stringResource(id = titleOf(route = route))
                        JyutpingTheme {
                                Scaffold(
                                        topBar = { TopAppBar(title = { Text(text = topTitle) }) },
                                        bottomBar = { AppBottomBar(navController = navController) },
                                        containerColor = Color(red = 226, green = 226, blue = 226)
                                ) { padding ->
                                        Box(modifier = Modifier.padding(padding)) {
                                                AppContent(navController = navController)
                                        }
                                }
                        }
                }
        }

        private fun titleOf(route: String?): Int {
                return when (route) {
                        "home" -> R.string.screen_title_home
                        "jyutping" -> R.string.screen_title_jyutping
                        "cantonese" -> R.string.screen_title_cantonese
                        "about" -> R.string.screen_title_about
                        else -> R.string.screen_title_home
                }
        }
}

@Composable
fun AppContent(navController: NavHostController) {
        NavHost(
                navController = navController,
                startDestination = Screen.Home.route
        ) {
                composable(Screen.Home.route) {
                        HomeScreen()
                }
                composable(Screen.Jyutping.route) {
                        JyutpingScreen()
                }
                composable(Screen.Cantonese.route) {
                        CantoneseScreen()
                }
                composable(Screen.About.route) {
                        AboutScreen()
                }
        }
}

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
