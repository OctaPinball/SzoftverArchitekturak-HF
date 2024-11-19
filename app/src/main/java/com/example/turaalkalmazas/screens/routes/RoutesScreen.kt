package com.example.turaalkalmazas.screens.routes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.turaalkalmazas.ROUTE_DETAIL_SCREEN
import com.example.turaalkalmazas.ROUTE_ID
import com.example.turaalkalmazas.model.Route

@Composable
fun RoutesScreen(openScreen: (String) -> Unit) {
    // Példa adatok
    val routes = listOf(
        Route("01asd","Kéktúra", "20 km", "1hr", "Közepes", true),
        Route("02asd","Mátra túra", "15 km", "0.5hr","Nehéz", true),
        Route("03asd","Balaton kör", "25 km", "1.5hr","Könnyű", true)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(routes) { route ->
            RouteItem(route) {
                // Navigálás a "details" képernyőre, a paraméterekkel
                val routeName = route.name
                val routeDifficulty = route.difficulty
                val routeLength = route.length
                openScreen("$ROUTE_DETAIL_SCREEN?$ROUTE_ID=1")
            }
        }
    }
}

@Composable
fun RouteItem(route: Route, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // Kattintáskezelő
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = route.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = route.difficulty,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = route.length,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
