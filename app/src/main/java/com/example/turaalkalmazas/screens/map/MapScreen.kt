package com.example.turaalkalmazas.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Polyline

@Composable
fun MapScreen() {
    val context = LocalContext.current

    // Helyváltozások kezelése
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    // Engedélyek
    var locationPermissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Kamera pozíció és útvonal pontjai
    val initialPosition = LatLng(47.4979, 19.0402) // Budapest példaként
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    }
    val routePoints = remember { mutableStateListOf<LatLng>() }

    // Túra állapota
    var isTracking by remember { mutableStateOf(false) }

    var showSaveDialog by remember { mutableStateOf(false) }

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
                            routePoints.add(newPoint) // Új pont hozzáadása az útvonalhoz
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newPoint, 15f)
                        }
                    }
                },
                null
            )
        } else {
            fusedLocationClient.removeLocationUpdates(object : com.google.android.gms.location.LocationCallback() {})
        }
    }

    // Térkép és polilinia megjelenítése
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted,
                mapType = MapType.NORMAL
            )
        ) {
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints.toList(),
                    color = Color.Blue,
                    width = 5f
                )
            }
        }

        // Túra indító/leállító gomb
        Button(
            onClick = {
                if (isTracking) {
                    isTracking = false
                    showSaveDialog = true
                } else {
                    isTracking = true
                    routePoints.clear()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTracking) Color.Red else Color.Green,
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = if (isTracking) "Túra Leállítása" else "Túra Indítása")
        }

        // Mentési dialógus megjelenítése
        if (showSaveDialog) {
            SaveRouteDialog(
                onDismiss = {
                    isTracking = false
                    routePoints.clear()
                    showSaveDialog = false
                },
                onSave = { routeName ->
                    // saveRouteToFirebase(routeName, routePoints)
                    isTracking = false
                    routePoints.clear()
                    showSaveDialog = false // Dialógus bezárása

                }
            )
        }
    }
}