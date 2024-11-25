package com.example.turaalkalmazas.screens.routes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.BaseRoute
import com.example.turaalkalmazas.model.GlobalRoute
import com.example.turaalkalmazas.model.LatLngSimple
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.GlobalRouteService
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(private val routeService: GlobalRouteService ) : AppViewModel() {

    private val _routes = mutableStateOf<List<GlobalRoute>>(emptyList())

    init {
        viewModelScope.launch {
            _routes.value = routeService.getAllRoutes()

            if (_routes.value.isEmpty()) {
                createGlobalRoute()
            }
        }
    }

    val routes: List<GlobalRoute> get() = _routes.value

    /*
    fun addRoute(route: GlobalRoute) {
        viewModelScope.launch {
            when (route) {
                is Route -> routeService.addRoute(route)
                is GlobalRoute -> {
                    (routeService as? GlobalRouteService)?.addRoute(route)
                }
            }
            _routes.value = routeService.getAllRoutes()
        }
    }*/

    fun addRoute(route: GlobalRoute) {
        viewModelScope.launch {
            routeService.addRoute(route)
            _routes.value = routeService.getAllRoutes()
        }
    }

    fun createGlobalRoute() {
        val newRoute1 = GlobalRoute(
            name = "River Trail",
            length = "6",
            duration = "2hrs",
            difficulty = "Easy",
            attractions = "Beautiful river scenery",
            altitudeDiff = "50",
            routePoints = listOf(
                    LatLngSimple(47.497912, 19.040235),
                    LatLngSimple(47.498112, 19.041135),
                    LatLngSimple(47.498512, 19.042135),
                    LatLngSimple(47.498812, 19.043235),
                    LatLngSimple(47.499112, 19.043635),
                    LatLngSimple(47.499312, 19.044135)
            )

        )

        val newRoute2 = GlobalRoute(
            name = "Mountain Path",
            length = "12",
            duration = "3hrs",
            difficulty = "Hard",
            attractions = "Stunning mountain views, Challenging terrain",
            routePoints = mutableListOf(),
            altitudeDiff = "600"
        )

        val newRoute3 = GlobalRoute(
            name = "Lakeside Walk",
            length = "4",
            duration = "1.5hrs",
            difficulty = "Moderate",
            attractions = "Calm lake, Picnic spots, Bird-watching areas",
            routePoints = mutableListOf(),
            altitudeDiff = "20"
        )

        val newRoute4 = GlobalRoute(
            name = "Forest Trek",
            length = "15",
            duration = "5hrs",
            difficulty = "Hard",
            attractions = "Dense forest, Waterfall, Unique flora and fauna, Scenic viewpoints",
            routePoints = mutableListOf(),
            altitudeDiff = "400"
        )

        val newRoute5 = GlobalRoute(
            name = "Countryside Loop",
            length = "10",
            duration = "3hrs",
            difficulty = "Easy",
            attractions = "Rolling hills, Farmlands, Historical ruins",
            routePoints = mutableListOf(),
            altitudeDiff = "100"
        )

        val newRoute6 = GlobalRoute(
            name = "Desert Adventure",
            length = "20",
            duration = "6hrs",
            difficulty = "Extreme",
            attractions = "Sand dunes, Sunset views, Exotic wildlife",
            routePoints = mutableListOf(),
            altitudeDiff = "50"
        )

        val newRoute7 = GlobalRoute(
            name = "Canyon Edge Trail",
            length = "8",
            duration = "2.5hrs",
            difficulty = "Moderate",
            attractions = "Canyon views, Rock formations",
            routePoints = mutableListOf(),
            altitudeDiff = "300"
        )

        val newRoute8 = GlobalRoute(
            name = "Coastal Pathway",
            length = "5",
            duration = "2hrs",
            difficulty = "Easy",
            attractions = "Ocean breeze, Beach access",
            routePoints = mutableListOf(),
            altitudeDiff = "10"
        )

        val newRoute9 = GlobalRoute(
            name = "Valley Crossing",
            length = "14",
            duration = "4.5hrs",
            difficulty = "Hard",
            attractions = "Hidden valleys, Wildflowers, Small streams",
            routePoints = mutableListOf(),
            altitudeDiff = "500"
        )

        val newRoute10 = GlobalRoute(
            name = "Urban Jungle",
            length = "3",
            duration = "1hr",
            difficulty = "Easy",
            altitudeDiff = "30",
            attractions = "City park, Skyscraper views, Art installations",
            routePoints = mutableListOf(),
        )

        addRoute(newRoute1)
        addRoute(newRoute2)
        addRoute(newRoute3)
        addRoute(newRoute4)
        addRoute(newRoute5)
        addRoute(newRoute6)
        addRoute(newRoute7)
        addRoute(newRoute8)
        addRoute(newRoute9)
        addRoute(newRoute10)
    }
}
