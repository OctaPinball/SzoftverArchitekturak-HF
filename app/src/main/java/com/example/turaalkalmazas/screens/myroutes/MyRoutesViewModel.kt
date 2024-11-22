package com.example.turaalkalmazas.screens.myroutes

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRoutesViewModel @Inject constructor(
    private val routeService: RouteService,
    private val accountService: AccountService
)
    : AppViewModel() {

    private val _routes = mutableStateOf<List<Route>>(emptyList())
    val routes: List<Route> get() = _routes.value

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    init {
        launchCatching {
            val currentUser = accountService.currentUser.firstOrNull()
            if (currentUser != null) {
                _user.value = currentUser
            }

            viewModelScope.launch {
                _routes.value = routeService.getUserRoutes()
            }
        }
    }

    fun fetchRoutePointsById(routeId: String) {
        viewModelScope.launch {
            val route = routeService.getRouteById(routeId)
            if (route != null) {
                Log.d("MapViewModel", "Route Points for route id $routeId: ${route.routePoints}")
            } else {
                Log.d("MapViewModel", "Route not found with id $routeId")
            }
        }
    }


    fun addRoute(route: Route) {
        viewModelScope.launch {
            routeService.addRoute(route)
            _routes.value = routeService.getUserRoutes()
        }
    }

    // Method to add test routes
    fun testAddRoute() {

        val newRoute1 = Route(name = "River Trail", length = "6km",
            duration = "2hrs", difficulty = "Easy", isShared = true, routePoints =  mutableListOf())
        val newRoute2 = Route(name = "Mountain Path", length = "12km",
            duration = "3hrs", difficulty = "Hard", isShared = true, routePoints =  mutableListOf())
        addRoute(newRoute1)
        addRoute(newRoute2)
    }

    fun deleteRoute(route: Route) {
        viewModelScope.launch {
            routeService.deleteRoute(route)
            _routes.value = routeService.getUserRoutes()
        }
    }

    fun updateSharedState(route: Route, isShared: Boolean) {
        _routes.value = _routes.value.map {
            if (it.id == route.id) it.copy(isShared = isShared) else it
        }

        viewModelScope.launch {
            routeService.updateRoute(route.copy(isShared = isShared))
        }
    }
}