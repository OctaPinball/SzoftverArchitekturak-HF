package com.example.turaalkalmazas.service.impl

import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.example.turaalkalmazas.service.RouteService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class RouteServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService,
    private val friendsService: FriendsService
) : RouteService {

    private val routesCollection = firestore.collection("paths")

    override suspend fun addRoute(route: Route) {
        val routeDoc = firestore.collection("paths").document()
        val id = routeDoc.id
        val name = route.name
        val length = route.length
        val duration = route.duration
        val difficulty = route.difficulty
        val isShared = route.isShared
        val ownerID = accountService.currentUserId
        val altitudeDiff = route.altitudeDiff
        val routePoints = route.routePoints
        val routeData = mapOf(
            "id" to id,
            "name" to name,
            "length" to length,
            "duration" to duration,
            "difficulty" to difficulty,
            "isShared" to isShared,
            "ownerID" to ownerID,
            "altitudeDiff" to altitudeDiff,
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
            routesCollection.document(route.id).set(route).await()
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

    override suspend fun getFriendRoutes(friendId: String): List<Route> {
        if(!friendsService.isFriends(accountService.currentUserId, friendId))
        {
            return emptyList()
        }

        return try {
            val querySnapshot = routesCollection
                .whereEqualTo("ownerID", friendId)
                .whereEqualTo("isShared", true)
                .get()
                .await()
            querySnapshot.toObjects(Route::class.java)

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getRouteById(routeId: String): Route {
        try {
            val allRoutes = getAllRoutes()
            val foundRoute = allRoutes.find { it.id == routeId }
            return foundRoute ?: Route()
        } catch (e: Exception) {
            throw Exception("Failed to fetch route with ID $routeId: ${e.message}")
        }

    }
}