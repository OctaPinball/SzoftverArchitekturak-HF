package com.example.turaalkalmazas.screens.myroutes

import android.util.Log
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
    /*
    fun loadRouteDetails() {
        viewModelScope.launch {
            _routeDetails.value = routeService.getRouteById(routeID.value)
        }
    }
    */
    fun loadRouteDetails() {
        viewModelScope.launch {
            try {
                val route = routeService.getRouteById(routeID.value)
                Log.d("RouteDetailViewModel", "Loaded route: $route")
                _routeDetails.value = route
            } catch (e: Exception) {
                Log.e("RouteDetailViewModel", "Error loading route: ${e.message}")
            }
        }
    }

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
