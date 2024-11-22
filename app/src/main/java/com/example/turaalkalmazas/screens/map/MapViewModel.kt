package com.example.turaalkalmazas.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.model.toLatLng
import com.example.turaalkalmazas.model.toLatLngSimple
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale
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
            ownerId = "",
            altitudeDiff = "",
            routePoints = mutableListOf()
        )
    )

    private val _preloadedRoute = MutableStateFlow(Route())
    val preloadedRoute: StateFlow<Route> get() = _preloadedRoute
    private val _preloadedRoutePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val preloadedRoutePoints: StateFlow<List<LatLng>> get() = _preloadedRoutePoints

    val isTracking = mutableStateOf(false)
    private val elapsedTime = mutableStateOf(0L)
    private val timerRunning = mutableStateOf(false)
    private var timerJob: Job? = null

    var startingAltitude: Double? = null
    val currentLocation = mutableStateOf<LatLng?>(null)
    val currentAltitude = mutableStateOf(0.0)

    // Útvonal pontjai
    val routePoints = mutableStateListOf<LatLng>()

    fun inicialize(routeId: String){
        launchCatching{
            loadRoute(routeId)
        }
    }

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
                currentAltitude.value = location.altitude
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
    }

    fun stopLocationUpdates() {
        //fusedLocationClient?.removeLocationUpdates(object : LocationCallback() {})
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    // Új túra indítása
    fun startTracking() {
        isTracking.value = true
        routePoints.clear()
        currentRoute.value = currentRoute.value.copy(
            id = "",
            routePoints = mutableListOf()
        )

        startingAltitude = currentAltitude.value

        if (!timerRunning.value) {
            timerRunning.value = true
            startTimer()
        }
    }

    fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (timerRunning.value) {
                delay(1000)
                elapsedTime.value += 1
            }
        }
    }

    // Túra leállítása
    fun stopTracking() {
        isTracking.value = false
        //routePoints.clear() // ezt kell kivenni hogy mentse a pontokat jol

        val altitudeDifference = startingAltitude?.let { startAlt ->
            currentAltitude.value - startAlt
        } ?: 0.0

        currentRoute.value = currentRoute.value.copy(
            altitudeDiff = altitudeDifference.toString()
        )

        timerRunning.value = false
        timerJob?.cancel()
    }

    // Útvonal mentése
    fun saveRoute(name: String) {
        val routeDistance = calculateRouteDistance(routePoints)
        val simpleRoutePoints = routePoints.map { it.toLatLngSimple() }
        // Nehézség meghatározása
        val difficulty = when (routeDistance.toInt()) {
            in 0..999 -> "Easy"
            in 1000..2000 -> "Moderate"
            in 2001..3000 -> "Hard"
            else -> "Extreme" // Minden 3000 feletti eset
        }
        currentRoute.value = currentRoute.value.copy(
            name = name,
            //length = "${routeDistance / 1000} km",
            length = routeDistance.toString(),
            duration = formatDuration(elapsedTime.value),
            difficulty = difficulty,
            isShared = false,
            routePoints = simpleRoutePoints
        )
        viewModelScope.launch {
            routeService.addRoute(currentRoute.value)
        }
    }

    fun loadRoute(routeId: String) {
        viewModelScope.launch {
            if (routeId != "-1") {
                val route = routeService.getRouteById(routeId) // Fetch route from database
                if (route != null) {
                    _preloadedRoutePoints.value = route.routePoints.map { it.toLatLng() } // Convert to LatLng
                    _preloadedRoute.value = route // Set the current route
                } else {
                    Log.d("MapViewModel", "Route not found with id $routeId")
                }
            }
        }
    }

    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val sec = seconds % 60
        return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes, sec)
    }

    fun fetchRoutePointsById(routeId: String) {
        viewModelScope.launch {
            val route = routeService.getRouteById(routeId)
            if (route != null) {
                Log.d("MapViewModel", "Route Points for route id $routeId: ${route.routePoints}")
            } else {
                Log.d("MapViewModel", "Route not found with id $routeId")
            }
        }
    }

    fun getElapsedTime(): Long {
        return elapsedTime.value
    }
}