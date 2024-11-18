package com.example.turaalkalmazas.model

import kotlinx.coroutines.flow.StateFlow
import com.google.android.gms.maps.model.LatLng

data class Route(
    val id: String,
    val name: String,
    val length: String,
    //val duration: String,
    val difficulty: String
    //var isShared: Boolean,
    //val route: List<LatLng>
)