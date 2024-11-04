package com.example.turaalkalmazas


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.turaalkalmazas.screens.account_center.AccountCenterScreen
import com.example.turaalkalmazas.screens.authentication.sign_in.SignInScreen
import com.example.turaalkalmazas.screens.authentication.sign_up.SignUpScreen
import com.example.turaalkalmazas.screens.main.MainScreen
import com.example.turaalkalmazas.screens.splash.SplashScreen
import com.example.turaalkalmazas.ui.theme.Theme
import kotlinx.coroutines.CoroutineScope

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appState = rememberAppState(snackbarHostState)

            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    notesGraph(appState)
                }
            }
        }
    }
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): NotesAppState {
    return remember(snackbarHostState, navController, snackbarManager, coroutineScope) {
        NotesAppState(snackbarHostState, navController, snackbarManager, coroutineScope)
    }
}

fun NavGraphBuilder.notesGraph(appState: NotesAppState) {
    composable(MAIN_SCREEN) {
        MainScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }


    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(ACCOUNT_CENTER_SCREEN) {
        AccountCenterScreen(restartApp = { route -> appState.clearAndNavigate(route) })
    }
}