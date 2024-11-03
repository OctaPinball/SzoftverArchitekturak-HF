package com.example.turaalkalmazas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    object Map : BottomNavScreen("map", "Map", Icons.Default.Home)
    object Routes : BottomNavScreen("routes", "Routes", Icons.Default.Search)
    object MyRoutes : BottomNavScreen("my_routes", "My routes", Icons.Default.Menu)
    object Friends : BottomNavScreen("friends", "Friends", Icons.Default.Person)
}

@Composable
fun UserCard(userName: String, profileImage: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = profileImage,
            contentDescription = "Profile Image",
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = userName, style = MaterialTheme.typography.h6)
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            UserCard(userName = "John Doe", profileImage = Icons.Default.Person)
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}
