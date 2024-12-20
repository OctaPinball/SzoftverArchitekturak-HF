package com.example.turaalkalmazas.screens.myroutes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.ROUTE_ID
import com.example.turaalkalmazas.model.toLatLng
import com.example.turaalkalmazas.utils.DrawPolyline
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.roundToInt

@Composable
fun RouteDetailScreen(
    routeId: String,
    restartApp: (String) -> Unit,
    viewModel: RouteDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.inicialize(routeId)
    }
    val routeDetails by viewModel.routeDetails.collectAsState()

    val initialPosition = LatLng(47.4979, 19.0402)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row (

        ) {
            Text(
                text = "Details:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(onClick = {
                restartApp("$MAP_SCREEN?$ROUTE_ID=${routeId}")
            }) {
                Text(stringResource(R.string.select_as_path))
            }
        }
        Text(
            text = "Tour name: ${routeDetails.name}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Difficulty: ${routeDetails.difficulty}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Distance: ${routeDetails.length.takeIf { it.isNotEmpty() }
                ?.toDoubleOrNull()?.div(1000)?.let { "%.2f".format(it) } ?: 0} km",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Duration: ${routeDetails.duration}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Height Difference: " +
                    "${routeDetails.altitudeDiff.toDoubleOrNull()?.roundToInt() ?: 0} m",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL
            )
        ) {
            DrawPolyline(routePoints = routeDetails.routePoints.map { it.toLatLng() }, color = Color.Blue)
        }

    }
}
