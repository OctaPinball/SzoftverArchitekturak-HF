package com.example.turaalkalmazas.screens.routes

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.turaalkalmazas.screens.map.MapScreen

@Composable
fun RouteDetailScreen(routeId: String,
                      popUpScreen: (String) -> Unit,
                      restartApp: (String) -> Unit,
                      modifier: Modifier = Modifier) {
    val name = "asd"
    val difficulty = "nehez"
    val length = "15 km"

    if (name == "N/A" || difficulty == "N/A" || length == "N/A") {
        Log.e("RouteDetailsScreen", "Hibás paraméterek! Név: $name, Nehézség: $difficulty, Hossz: $length")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Details:",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom= 16.dp)
        )
        Text(text = "Név: $name", fontSize = 18.sp, modifier = Modifier.padding(bottom= 8.dp))
        Text(text = "Nehézség: $difficulty", fontSize = 18.sp, modifier = Modifier.padding(bottom= 8.dp))
        Text(text = "Hossz: $length", fontSize = 18.sp)
        MapScreen()

    }

}