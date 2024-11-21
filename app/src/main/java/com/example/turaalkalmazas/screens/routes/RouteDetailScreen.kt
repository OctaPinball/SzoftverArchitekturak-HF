package com.example.turaalkalmazas.screens.routes

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.screens.map.MapScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun RouteDetailScreen(
    routeId: String, // Átadott Route objektum
    restartApp: (String) -> Unit,
    viewModel: RouteDetailViewModel = hiltViewModel() // A RouteDetailViewModel példányosítása
) {
    LaunchedEffect(Unit) {
        viewModel.inicialize(routeId)
    }
    val routeDetails by viewModel.routeDetails.collectAsState()

    // UI komponensek megjelenítése
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Details:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Név: ${routeDetails.name}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Nehézség: ${routeDetails.difficulty}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Hossz: ${routeDetails.length}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Térkép komponens
        MapScreen()
    }
}
