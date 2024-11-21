package com.example.turaalkalmazas.model

import com.google.android.gms.maps.model.LatLng

data class LatLngSimple(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Route(
    val id: String = "",
    val name: String = "",
    val length: String = "",
    val duration: String = "",
    val difficulty: String = "",
    var isShared: Boolean = true,
    val ownerId: String = "",
    val altitudeDiff: String = "",
    val routePoints: List<LatLngSimple> = emptyList()
)

fun LatLng.toLatLngSimple(): LatLngSimple {
    return LatLngSimple(latitude, longitude)
}

fun LatLngSimple.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}