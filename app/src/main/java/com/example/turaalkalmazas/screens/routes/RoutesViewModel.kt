package com.example.turaalkalmazas.screens.routes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(private val routeService: RouteService) : AppViewModel() {

    private val _routes = mutableStateOf<List<Route>>(emptyList())

    // Inicializálás és route-ok lekérése
    init {
        viewModelScope.launch {
            _routes.value = routeService.getAllRoutes()
        }
    }

    // Közvetlen hozzáférés az adatokhoz
    val routes: List<Route> get() = _routes.value

    // Új route hozzáadása
    fun addRoute(route: Route) {
        viewModelScope.launch {
            routeService.addRoute(route)
            _routes.value = routeService.getAllRoutes()
        }
    }
}
