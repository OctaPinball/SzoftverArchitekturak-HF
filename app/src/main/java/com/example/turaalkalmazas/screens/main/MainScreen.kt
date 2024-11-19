package com.example.turaalkalmazas.screens.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turaalkalmazas.ACCOUNT_CENTER_SCREEN
import com.example.turaalkalmazas.ADD_FRIENDS_SCREEN
import com.example.turaalkalmazas.AppState
import com.example.turaalkalmazas.FRIENDS_SCREEN
import com.example.turaalkalmazas.FRIEND_DETAILS_SCREEN
import com.example.turaalkalmazas.FRIEND_REQUEST_SCREEN
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.MY_ROUTES_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.ROUTES_SCREEN
import com.example.turaalkalmazas.SIGN_IN_SCREEN
import com.example.turaalkalmazas.SIGN_UP_SCREEN
import com.example.turaalkalmazas.SPLASH_SCREEN
import com.example.turaalkalmazas.SnackbarManager
import com.example.turaalkalmazas.USER_DEFAULT_ID
import com.example.turaalkalmazas.USER_ID
import com.example.turaalkalmazas.USER_ID_ARG
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.screens.account_center.AccountCenterScreen
import com.example.turaalkalmazas.screens.authentication.sign_in.SignInScreen
import com.example.turaalkalmazas.screens.authentication.sign_up.SignUpScreen
import com.example.turaalkalmazas.screens.friends.AddFriendsScreen
import com.example.turaalkalmazas.screens.friends.FriendDetailsScreen
import com.example.turaalkalmazas.screens.friends.FriendRequestScreen
import com.example.turaalkalmazas.screens.friends.FriendsScreen
import com.example.turaalkalmazas.screens.friends.TopNavigationFriends
import com.example.turaalkalmazas.screens.map.MapScreen
import com.example.turaalkalmazas.screens.myroutes.MyRoutesScreen
import com.example.turaalkalmazas.screens.routes.RoutesScreen
import com.example.turaalkalmazas.screens.splash.SplashScreen
import com.example.turaalkalmazas.ui.theme.Theme
import kotlinx.coroutines.CoroutineScope

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()

    Theme {
        Log.d("SuperLog Main", "UserName: ${user.displayName} ${user.isAnonymous}")
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appState = rememberAppState(snackbarHostState)

            val currentBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry?.destination?.route

            val dismissSnackbarOnTapModifier = Modifier.pointerInput(Unit) {
                detectTapGestures {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            }

            androidx.compose.material3.Scaffold(
                modifier = dismissSnackbarOnTapModifier,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        val isError = (data.duration == SnackbarDuration.Long)
                        Snackbar(
                            snackbarData = data,
                            backgroundColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                topBar = {
                    Column {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = if(currentRoute in listOf(FRIENDS_SCREEN, ADD_FRIENDS_SCREEN, FRIEND_REQUEST_SCREEN, MAP_SCREEN))
                                RoundedCornerShape(0.dp)
                            else RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                        ) {
                            UserCard(
                                userName = if (user.isAnonymous) stringResource(R.string.sign_in) else user.displayName,
                                profileImage = Icons.Default.Person
                            ) {
                                if (user.isAnonymous) {
                                    appState.navigate(SIGN_IN_SCREEN)
                                } else {
                                    appState.navigate(ACCOUNT_CENTER_SCREEN)
                                }
                            }
                        }
                        currentRoute?.let {
                            if (currentRoute in listOf(
                                    FRIENDS_SCREEN,
                                    ADD_FRIENDS_SCREEN,
                                    FRIEND_REQUEST_SCREEN
                                )
                            ) {
                                TopNavigationFriends(
                                    openScreen = { route -> appState.navigate(route) },
                                    currentRoute
                                )
                            }
                        }
                    }
                },
                bottomBar = { BottomNavigationBar(appState.navController, openScreen = { route -> appState.clearAndNavigate(route) }) }
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
): AppState {
    return remember(snackbarHostState, navController, snackbarManager, coroutineScope) {
        AppState(snackbarHostState, navController, snackbarManager, coroutineScope)
    }
}

fun NavGraphBuilder.notesGraph(appState: AppState) {

    composable(MAP_SCREEN){
        MapScreen()
    }

    composable(ROUTES_SCREEN){
        RoutesScreen()
    }

    composable(MY_ROUTES_SCREEN){
        MyRoutesScreen()
    }

    composable(FRIENDS_SCREEN){
        FriendsScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(ADD_FRIENDS_SCREEN){
        AddFriendsScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(FRIEND_REQUEST_SCREEN){
        FriendRequestScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) },
        )
    }

    composable(
        route = "$FRIEND_DETAILS_SCREEN$USER_ID_ARG",
        arguments = listOf(navArgument(USER_ID) { defaultValue = USER_DEFAULT_ID })
    ) {
        FriendDetailsScreen(
            userId = it.arguments?.getString(USER_ID) ?: USER_DEFAULT_ID,
            popUpScreen = { appState.popUp() },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable(SIGN_IN_SCREEN) {
        SignInScreen(
            openScreen = { route -> appState.navigate(route) },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }

    composable(SIGN_UP_SCREEN) {
        SignUpScreen(restartApp = { route -> appState.clearAndNavigate(route) })
    }

    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(ACCOUNT_CENTER_SCREEN) {
        AccountCenterScreen(
            openScreen = { route -> appState.navigate(route) },
            restartApp = { route -> appState.clearAndNavigate(route) }
        )
    }
}

sealed class BottomNavScreen(val routes: List<String>, @StringRes val label: Int, val icon: ImageVector) {
    object Map : BottomNavScreen(listOf(MAP_SCREEN), R.string.map, Icons.Default.Home)
    object Routes : BottomNavScreen(listOf(ROUTES_SCREEN), R.string.routes, Icons.Default.Search)
    object MyRoutes : BottomNavScreen(listOf(MY_ROUTES_SCREEN), R.string.my_routes, Icons.Default.Menu)
    object Friends : BottomNavScreen(
        listOf(FRIENDS_SCREEN, FRIEND_REQUEST_SCREEN, ADD_FRIENDS_SCREEN),
        R.string.friends,
        Icons.Default.Person
    )
}


@Composable
fun BottomNavigationBar(navController: NavController, openScreen: (String) -> Unit) {
    val screens = listOf(
        BottomNavScreen.Map,
        BottomNavScreen.Routes,
        BottomNavScreen.MyRoutes,
        BottomNavScreen.Friends
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = null,
                        tint = if (screen.routes.contains(currentRoute)) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = screen.label),
                        color = if (screen.routes.contains(currentRoute)) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                        fontWeight = if (screen.routes.contains(currentRoute)) FontWeight.ExtraBold else FontWeight.Normal,
                    )
                },
                selected = (screen.routes.contains(currentRoute)),
                onClick = {
                    if (!screen.routes.contains(currentRoute)) {
                        openScreen(screen.routes[0])
                    }
                },
            )
        }
    }
}

@Composable
fun UserCard(userName: String, profileImage: ImageVector, onClick: () -> Unit) {
    Log.d("SuperLog UserCard", "UserName: ${userName}")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = profileImage,
            contentDescription = "Profile Image",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = userName, style = MaterialTheme.typography.bodyLarge)
    }
}

