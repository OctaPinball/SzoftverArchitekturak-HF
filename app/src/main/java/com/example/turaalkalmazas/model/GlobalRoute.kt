package com.example.turaalkalmazas.model

data class GlobalRoute(
    override val id: String = "",
    override val name: String = "",
    override val length: String = "",
    override val difficulty: String = "",
    override val altitudeDiff: String = "",
    override val duration: String = "",
    val attractions: String = "",
    val routePoints: List<LatLngSimple> = emptyList()
) : BaseRoute