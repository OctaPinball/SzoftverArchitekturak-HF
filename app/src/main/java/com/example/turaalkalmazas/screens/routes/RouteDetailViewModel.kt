package com.example.turaalkalmazas.screens.routes

import androidx.lifecycle.ViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val routeService: RouteService
) : AppViewModel() {

    private val _routeDetails = MutableStateFlow(Route())
    val routeDetails: StateFlow<Route> get() = _routeDetails
    val routeID = MutableStateFlow("")

    fun loadRouteDetails() {
        // Példa logika, hogy hogyan töltheted le a részleteket a service-ből
        viewModelScope.launch {
            _routeDetails.value = routeService.getRouteById(routeID.value)
        }
    }

    // Ha szükséges új route hozzáadása
    fun addRoute(route: Route) {
        viewModelScope.launch {
            routeService.addRoute(route)
        }
    }

    fun inicialize(routeId: String){
        launchCatching{
            routeID.value = routeId
            loadRouteDetails()
        }
    }
}
