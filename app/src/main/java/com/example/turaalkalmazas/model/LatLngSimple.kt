package com.example.turaalkalmazas.model

import com.google.android.gms.maps.model.LatLng

data class LatLngSimple(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

fun LatLng.toLatLngSimple(): LatLngSimple {
    return LatLngSimple(latitude, longitude)
}

fun LatLngSimple.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}
