package com.example.turaalkalmazas.service.impl

import com.example.turaalkalmazas.model.GlobalRoute
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.GlobalRouteService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GlobalRouteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService,
) : GlobalRouteService {

    private val routesCollection = firestore.collection("globalRoutes")

    override suspend fun addRoute(route: GlobalRoute) {
        val routeDoc = routesCollection.document()
        val id = routeDoc.id
        val routePoints = route.routePoints
        val routeData = mapOf(
            "id" to id,
            "name" to route.name,
            "length" to route.length,
            "duration" to route.duration,
            "difficulty" to route.difficulty,
            "altitudeDiff" to route.altitudeDiff,
            "attractions" to route.attractions,
            "routePoints" to routePoints
        )
        routeDoc.set(routeData, SetOptions.merge()).await()
    }

    override suspend fun getAllRoutes(): List<GlobalRoute> {
        return try {
            val snapshot = routesCollection.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(GlobalRoute::class.java)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch global routes: ${e.message}")
        }
    }

    override suspend fun getRouteById(routeId: String): GlobalRoute {
        try {
            val routeDocument = routesCollection.document(routeId).get().await()
            return routeDocument.toObject(GlobalRoute::class.java)
                ?: throw Exception("Route with ID $routeId not found.")
        } catch (e: Exception) {
            throw Exception("Failed to fetch route with ID $routeId: ${e.message}")
        }
    }
}