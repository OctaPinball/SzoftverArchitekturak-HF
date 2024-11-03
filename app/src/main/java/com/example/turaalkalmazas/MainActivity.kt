package com.example.turaalkalmazas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavController
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Map : BottomNavScreen("map", "Map", Icons.Default.Home)
    object Routes : BottomNavScreen("routes", "Routes", Icons.Default.Search)
    object MyRoutes : BottomNavScreen("my_routes", "My routes", Icons.Default.Menu)
    object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
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
        composable(BottomNavScreen.Profile.route) { ProfileScreen() }
    }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val screens = listOf(
            BottomNavScreen.Map,
            BottomNavScreen.Routes,
            BottomNavScreen.MyRoutes,
            BottomNavScreen.Profile
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
fun ProfileScreen() {
    Text(text = "Profile Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}
