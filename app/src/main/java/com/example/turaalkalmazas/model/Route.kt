package com.example.turaalkalmazas.model

data class Route(
    override val id: String = "",
    override val name: String = "",
    override val length: String = "",
    override val difficulty: String = "",
    override val altitudeDiff: String = "",
    override val duration: String = "",
    val ownerId: String = "",
    val shared: Boolean = false,
    val routePoints: List<LatLngSimple> = emptyList()
) : BaseRoute


