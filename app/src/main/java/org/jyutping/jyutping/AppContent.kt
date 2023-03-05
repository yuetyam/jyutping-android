package org.jyutping.jyutping

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jyutping.jyutping.ui.about.*
import org.jyutping.jyutping.ui.cantonese.*
import org.jyutping.jyutping.ui.home.*
import org.jyutping.jyutping.ui.romanization.*

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
                        JyutpingScreen(navController = navController)
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
                composable(route = Screen.JyutpingInitials.route) {
                        JyutpingInitialsScreen()
                }
                composable(route = Screen.JyutpingFinals.route) {
                        JyutpingFinalsScreen()
                }
                composable(route = Screen.JyutpingTones.route) {
                        JyutpingTonesScreen()
                }
        }
}
