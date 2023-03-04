package org.jyutping.jyutping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                        val canNavigateUp: Boolean = shouldNavigateUp(route = route) // navController.previousBackStackEntry != null
                        JyutpingTheme {
                                Scaffold(
                                        topBar = {
                                                TopAppBar(
                                                        title = { Text(text = topTitle) },
                                                        navigationIcon = {
                                                                if (canNavigateUp) {
                                                                        IconButton(onClick = { navController.navigateUp() }) {
                                                                                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                                                                        }
                                                                }
                                                        },
                                                        colors = TopAppBarDefaults.topAppBarColors(
                                                                containerColor = colorScheme.secondaryContainer,
                                                                scrolledContainerColor = colorScheme.tertiaryContainer,
                                                                navigationIconContentColor = colorScheme.onBackground,
                                                                titleContentColor = colorScheme.onBackground,
                                                                actionIconContentColor = colorScheme.onBackground
                                                        )
                                                )
                                        },
                                        bottomBar = { AppBottomBar(navController = navController) },
                                        containerColor = colorScheme.tertiaryContainer
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
                        Screen.Home.route -> Screen.Home.title
                        Screen.Jyutping.route -> Screen.Jyutping.title
                        Screen.Cantonese.route -> Screen.Cantonese.title
                        Screen.About.route -> Screen.About.title
                        Screen.Introductions.route -> Screen.Introductions.title
                        else -> Screen.Home.title
                }
        }
        private fun shouldNavigateUp(route: String?): Boolean {
                return when (route) {
                        Screen.Home.route -> false
                        Screen.Jyutping.route -> false
                        Screen.Cantonese.route -> false
                        Screen.About.route -> false
                        else -> true
                }
        }
}

@Composable
fun AppContent(navController: NavHostController) {
        NavHost(
                navController = navController,
                startDestination = Screen.Home.route
        ) {
                composable(route = Screen.Home.route) {
                        HomeScreen(navController = navController)
                }
                composable(route = Screen.Jyutping.route) {
                        JyutpingScreen()
                }
                composable(route = Screen.Cantonese.route) {
                        CantoneseScreen()
                }
                composable(route = Screen.About.route) {
                        AboutScreen()
                }
                composable(route = Screen.Introductions.route) {
                        IntroductionsScreen()
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
