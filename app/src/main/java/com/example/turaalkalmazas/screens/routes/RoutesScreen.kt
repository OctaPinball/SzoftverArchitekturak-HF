package com.example.turaalkalmazas.screens.routes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ROUTE_DETAIL_SCREEN
import com.example.turaalkalmazas.ROUTE_ID
import com.example.turaalkalmazas.model.Route

@Composable
fun RoutesScreen(
    openScreen: (String) -> Unit,
    viewModel: RoutesViewModel = hiltViewModel()
) {
    val routes by remember { derivedStateOf { viewModel.routes } }
    RouteList(openScreen, routes)
}

@Composable
fun RouteList(
    openScreen: (String) -> Unit,
    routes: List<Route>
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderRow()

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routes) { route ->
                RouteItem(route) {
                    openScreen("$ROUTE_DETAIL_SCREEN?$ROUTE_ID=${route.id}")
                }
            }
        }
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tour",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Difficulty",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Distance",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = route.difficulty,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${"%.1f".format(route.length.toDouble())} m",
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
