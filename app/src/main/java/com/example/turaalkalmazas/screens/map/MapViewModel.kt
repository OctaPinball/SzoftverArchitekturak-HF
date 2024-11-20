package com.example.turaalkalmazas.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.RouteService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val routeService: RouteService
) : AppViewModel() {
    private var fusedLocationClient: FusedLocationProviderClient? = null

    // Állapotok
    val locationPermissionGranted = mutableStateOf(false)
    val currentRoute = mutableStateOf(
        Route(
            id = "",
            name = "",
            length = "",
            duration = "",
            difficulty = "",
            isShared = false,
            routePoints = mutableListOf()
        )
    )
    val isTracking = mutableStateOf(false)

    // Útvonal pontjai
    val routePoints = mutableStateListOf<LatLng>()

    // Engedélyek ellenőrzése és kérése
    fun checkAndRequestPermission(context: Context, onPermissionRequired: () -> Unit) {
        locationPermissionGranted.value = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!locationPermissionGranted.value) {
            onPermissionRequired()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                val newPoint = LatLng(location.latitude, location.longitude)
                routePoints.add(newPoint)
            }
        }
    }

    private fun calculateRouteDistance(routePoints: List<LatLng>): Double {
        if (routePoints.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 0 until routePoints.size - 1) {
            val start = routePoints[i]
            val end = routePoints[i + 1]
            totalDistance += haversine(start.latitude, start.longitude, end.latitude, end.longitude)
        }
        return totalDistance
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Föld sugara méterben
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δφ = Math.toRadians(lat2 - lat1)
        val Δλ = Math.toRadians(lon2 - lon1)

        val a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ / 2) * Math.sin(Δλ / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    // Helyfrissítések kezelése
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).setMinUpdateIntervalMillis(1000L).build()

        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(object : LocationCallback() {})
    }

    // Új túra indítása
    fun startTracking() {
        isTracking.value = true
        routePoints.clear()
        currentRoute.value = currentRoute.value.copy(
            id = "",
            routePoints = mutableListOf()
        )
    }

    // Túra leállítása
    fun stopTracking() {
        isTracking.value = false
        routePoints.clear()
    }

    // Útvonal mentése
    fun saveRoute(name: String) {
        val routeDistance = calculateRouteDistance(routePoints)
        currentRoute.value = currentRoute.value.copy(
            name = name,
            //length = "${routeDistance / 1000} km",
            length = routeDistance.toString(),
            duration = "10 perc",
            difficulty = "1",
            isShared = false,
            routePoints = routePoints.toList()
        )
        viewModelScope.launch {
            routeService.addRoute(currentRoute.value)
        }
    }
}