package com.example.turaalkalmazas.screens.map


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.turaalkalmazas.utils.DrawPolyline
import com.example.turaalkalmazas.screens.map.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    routeId: String,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val preloadedRoute by viewModel.preloadedRoute.collectAsState()
    val preloadedRoutePoints by viewModel.preloadedRoutePoints.collectAsState()


    LaunchedEffect(routeId) {
        viewModel.loadRoute(routeId)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.locationPermissionGranted.value = it
    }

    // Kamera pozíció
    val initialPosition = LatLng(47.4979, 19.0402)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }

    var showSaveDialog by remember { mutableStateOf(false) }

    // Engedélyek kezelése
    LaunchedEffect(Unit) {
        viewModel.checkAndRequestPermission(context) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(viewModel.getElapsedTime()) {
        // Bármilyen változás az elapsedTime értékénél indít egy frissítést
        // Az UI automatikusan frissülni fog, amikor a value változik
    }

    // Térkép és polilinia megjelenítése
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = viewModel.locationPermissionGranted.value,
                mapType = MapType.NORMAL
            )
        ) {
            DrawPolyline(routePoints = viewModel.routePoints, color = Color.Blue)
            if(routeId != "-1") {
                DrawPolyline(routePoints = preloadedRoutePoints, color = Color.Red, width = 10f)
            }
        }

        // Túra indító/leállító gomb
        Button(
            onClick = {
                if (viewModel.isTracking.value) {
                    viewModel.stopTracking()
                    viewModel.stopLocationUpdates()
                    showSaveDialog = true
                } else {
                    viewModel.startTracking()
                    viewModel.startLocationUpdates(context)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.isTracking.value) Color.Red else Color.Green,
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = if (viewModel.isTracking.value) "Stop" else "Start")
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(
                    color = Color(0x80000000),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Text(
                text = "Time: ${viewModel.formatDuration(viewModel.getElapsedTime())}",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Altitude: ${viewModel.currentAltitude.value.toInt()} m",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Mentési dialógus megjelenítése
        if (showSaveDialog) {
            SaveRouteDialog(
                onDismiss = {
                    viewModel.stopTracking()
                    viewModel.stopLocationUpdates()
                    showSaveDialog = false
                },
                onSave = { routeName ->
                    // saveRouteToFirebase(routeName, routePoints)
                    viewModel.saveRoute(routeName)
                    viewModel.stopTracking()
                    viewModel.stopLocationUpdates()
                    showSaveDialog = false // Dialógus bezárása

                }
            )
        }
    }
}