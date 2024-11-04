package com.example.turaalkalmazas.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.turaalkalmazas.ACCOUNT_CENTER_SCREEN
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MainScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = User())
    val navController = rememberNavController()

    var username = "Click to log in"
    if(user.displayName != ""){
        username = user.displayName
    }

    Scaffold(
        topBar = {
            UserCard(userName = username, profileImage = Icons.Default.Person) {
                viewModel.onProfileClick(openScreen)
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Map.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Map.route) { MapScreen() }
            composable(BottomNavScreen.Routes.route) { RoutesScreen() }
            composable(BottomNavScreen.MyRoutes.route) { MyRoutesScreen() }
            composable(BottomNavScreen.Friends.route) { FriendsScreen() }
        }
    }
}


//TODO: Refactor the following code

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Map : BottomNavScreen("map", "Map", Icons.Default.Home)
    object Routes : BottomNavScreen("routes", "Routes", Icons.Default.Search)
    object MyRoutes : BottomNavScreen("my_routes", "My routes", Icons.Default.Menu)
    object Friends : BottomNavScreen("friends", "Friends", Icons.Default.Person)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
        BottomNavScreen.Map,
        BottomNavScreen.Routes,
        BottomNavScreen.MyRoutes,
        BottomNavScreen.Friends
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomNavigation {
        screens.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Composable
fun MapScreen() {
    Text(text = "Map Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}

@Composable
fun RoutesScreen() {
    Text(text = "Routes Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}

@Composable
fun MyRoutesScreen() {
    Text(text = "My Routes Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}

@Composable
fun FriendsScreen() {
    Text(text = "Friends Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}