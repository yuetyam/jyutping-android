package org.jyutping.jyutping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jyutping.jyutping.speech.TTSProvider
import org.jyutping.jyutping.ui.theme.JyutpingTheme
import org.jyutping.jyutping.utilities.DatabasePreparer

val LocalTTSProvider = staticCompositionLocalOf<TTSProvider> {
        error("No TTSProvider provided")
}

class MainActivity : ComponentActivity() {

        private lateinit var ttsProvider: TTSProvider

        @OptIn(ExperimentalMaterial3Api::class)
        override fun onCreate(savedInstanceState: Bundle?) {
                enableEdgeToEdge()
                super.onCreate(savedInstanceState)
                DatabasePreparer.prepare(this)
                ttsProvider = TTSProvider(this)
                setContent {
                        val navController = rememberNavController()
                        val entry by navController.currentBackStackEntryAsState()
                        val route: String? = entry?.destination?.route
                        val topTitle: String = stringResource(id = titleOf(route = route))
                        val canNavigateUp: Boolean = canNavigateUp(route = route)
                        CompositionLocalProvider(LocalTTSProvider provides ttsProvider) {
                                JyutpingTheme {
                                        Scaffold(
                                                topBar = {
                                                        TopAppBar(
                                                                title = { Text(text = topTitle) },
                                                                navigationIcon = {
                                                                        if (canNavigateUp) {
                                                                                IconButton(onClick = { navController.navigateUp() }) {
                                                                                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
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
                                        ) { paddingValues ->
                                                Box(modifier = Modifier.padding(paddingValues)) {
                                                        AppContent(navController = navController)
                                                }
                                        }
                                }
                        }
                }
        }

        override fun onDestroy() {
                ttsProvider.shutdown()
                super.onDestroy()
        }

        private fun titleOf(route: String?): Int = when (route) {
                Screen.Home.route -> Screen.Home.title
                Screen.Introductions.route -> Screen.Introductions.title
                Screen.Jyutping.route -> Screen.Jyutping.title
                Screen.JyutpingInitials.route -> Screen.JyutpingInitials.title
                Screen.JyutpingFinals.route -> Screen.JyutpingFinals.title
                Screen.JyutpingTones.route -> Screen.JyutpingTones.title
                Screen.Cantonese.route -> Screen.Cantonese.title
                Screen.Expressions.route -> Screen.Expressions.title
                Screen.Confusion.route -> Screen.Confusion.title
                Screen.About.route -> Screen.About.title
                else -> Screen.Home.title
        }
        private fun canNavigateUp(route: String?): Boolean = when (route) {
                Screen.Home.route -> false
                Screen.Jyutping.route -> false
                Screen.Cantonese.route -> false
                Screen.About.route -> false
                else -> true
        }
}
