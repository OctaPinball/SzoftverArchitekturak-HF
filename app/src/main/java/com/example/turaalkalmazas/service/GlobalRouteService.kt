package com.example.turaalkalmazas.service

import com.example.turaalkalmazas.model.GlobalRoute

interface GlobalRouteService {
    suspend fun addRoute(route: GlobalRoute)
    suspend fun getAllRoutes(): List<GlobalRoute>
    suspend fun getRouteById(routeId: String): GlobalRoute
}