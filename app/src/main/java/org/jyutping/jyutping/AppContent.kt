package org.jyutping.jyutping

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.jyutping.jyutping.mainapp.about.AboutScreen
import org.jyutping.jyutping.mainapp.cantonese.CantoneseScreen
import org.jyutping.jyutping.mainapp.cantonese.ConfusionScreen
import org.jyutping.jyutping.mainapp.cantonese.ExpressionsScreen
import org.jyutping.jyutping.mainapp.home.HomeScreen
import org.jyutping.jyutping.mainapp.home.IntroductionsScreen
import org.jyutping.jyutping.mainapp.romanization.JyutpingFinalsScreen
import org.jyutping.jyutping.mainapp.romanization.JyutpingInitialsScreen
import org.jyutping.jyutping.mainapp.romanization.JyutpingScreen
import org.jyutping.jyutping.mainapp.romanization.JyutpingTonesScreen

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
                        CantoneseScreen(navController = navController)
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
                composable(route = Screen.Expressions.route) {
                        ExpressionsScreen()
                }
                composable(route = Screen.Confusion.route) {
                        ConfusionScreen(navController = navController)
                }
        }
}
