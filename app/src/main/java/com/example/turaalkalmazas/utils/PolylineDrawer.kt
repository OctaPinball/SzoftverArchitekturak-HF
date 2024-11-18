package com.example.turaalkalmazas.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

@Composable
fun DrawPolyline(routePoints: List<LatLng>, color: Color = Color.Blue, width: Float = 5f) {
    if (routePoints.isNotEmpty()) {
        Polyline(points = routePoints, color = color, width = width)
    }
}