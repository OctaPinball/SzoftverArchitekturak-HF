package com.example.turaalkalmazas.service

import com.example.turaalkalmazas.model.Route

interface RouteService {
    suspend fun addRoute(route: Route)
    suspend fun deleteRoute(route: Route)
    suspend fun updateRoute(route: Route)
    suspend fun getAllRoutes(): List<Route>
    suspend fun getRouteById(routeId: String): Route
}