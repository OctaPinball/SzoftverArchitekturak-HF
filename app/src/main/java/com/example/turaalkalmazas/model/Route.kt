package com.example.turaalkalmazas.model

import com.google.android.gms.maps.model.LatLng

data class Route(
    val id: String = "",
    val name: String = "",
    val length: String = "",
    val duration: String = "",
    val difficulty: String = "",
    var isShared: Boolean = true,
    val routePoints: List<LatLng> = emptyList()
)