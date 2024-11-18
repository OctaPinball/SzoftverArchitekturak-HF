package com.example.turaalkalmazas.screens.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapScreen() {
    // Text(text = "Map Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)

    val context = LocalContext.current
    //val initialPosition = LatLng(47.4979, 19.0402) // Budapest koordinátái példaként
    //val cameraPositionState = rememberCameraPositionState {
    //    position = CameraPosition.fromLatLngZoom(initialPosition, 10f)
    //}

    // Helyváltozások kezelése
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    // Az engedély állapota
    var locationPermissionGranted by remember { mutableStateOf(false) }

    // Engedélykérés indítása
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
    }

    // Ellenőrizzük, hogy van-e engedély
    LaunchedEffect(Unit) {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        //cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = locationPermissionGranted,
            mapType = MapType.NORMAL
        )
    )
}