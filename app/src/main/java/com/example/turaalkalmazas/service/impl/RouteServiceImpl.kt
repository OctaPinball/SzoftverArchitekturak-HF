package com.example.turaalkalmazas.service.impl

import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.RouteService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RouteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RouteService {

    private val routesCollection = firestore.collection("routes")

    override suspend fun addRoute(route: Route) {
        val routeDoc = firestore.collection("paths").document()
        val id = routeDoc.id
        val name = route.name.orEmpty()
        val length = route.length.orEmpty()
        val duration = route.duration.orEmpty()
        val difficulty = route.difficulty.orEmpty()
        val isShared = route.isShared
        val routePoints = route.routePoints
        val routeData = mapOf(
            "id" to id,
            "name" to name,
            "length" to length,
            "duration" to duration,
            "difficulty" to difficulty,
            "isShared" to isShared,
            "routePoints" to routePoints
        )
        routeDoc.set(routeData, SetOptions.merge()).await()
    }

    override suspend fun deleteRoute(route: Route) {
        try {
            routesCollection.document(route.id).delete().await()
        } catch (e: Exception) {
            throw Exception("Failed to delete route: ${e.message}")
        }
    }

    override suspend fun updateRoute(route: Route) {
        try {
            routesCollection.document(route.id).set(route)
        } catch (e: Exception) {
            throw Exception("Failed to update route: ${e.message}")
        }
    }

    override suspend fun getAllRoutes(): List<Route> {
        return try {
            val snapshot = routesCollection.get().await()
            snapshot.toObjects(Route::class.java)
        } catch (e: Exception) {
            throw Exception("Failed to fetch routes: ${e.message}")
        }
    }
}