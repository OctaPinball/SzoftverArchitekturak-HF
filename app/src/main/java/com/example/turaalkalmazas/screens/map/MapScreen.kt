package com.example.turaalkalmazas.screens.map


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

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

    /*
    // Helykövetés
    LaunchedEffect(isTracking, locationPermissionGranted) {
        if (isTracking && locationPermissionGranted) {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                2000L
            ).apply {
                setMinUpdateIntervalMillis(1000L)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                        result.lastLocation?.let { location ->
                            val newPoint = LatLng(location.latitude, location.longitude)
                            (currentRoute.route as MutableList).add(newPoint) // Új pont hozzáadása az útvonalhoz
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newPoint, 15f)
                        }
                    }
                },
                null
            )
        } else {
            fusedLocationClient.removeLocationUpdates(object : com.google.android.gms.location.LocationCallback() {})
        }
    }*/

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
        }

        // Túra indító/leállító gomb
        Button(
            onClick = {
                if (viewModel.isTracking.value) {
                    viewModel.stopTracking()
                    showSaveDialog = true
                } else {
                    viewModel.startTracking()
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
            Text(text = if (viewModel.isTracking.value) "Túra Leállítása" else "Túra Indítása")
        }

        Text(
            text = "Time: ${viewModel.formatDuration(viewModel.getElapsedTime())}",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(25.dp)
                .align(Alignment.TopStart)
        )

        // Mentési dialógus megjelenítése
        if (showSaveDialog) {
            SaveRouteDialog(
                onDismiss = {
                    viewModel.stopTracking()
                    showSaveDialog = false
                },
                onSave = { routeName ->
                    // saveRouteToFirebase(routeName, routePoints)
                    viewModel.saveRoute(routeName)
                    viewModel.stopTracking()
                    showSaveDialog = false // Dialógus bezárása

                }
            )
        }
    }
}