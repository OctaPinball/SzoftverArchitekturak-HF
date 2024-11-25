package com.example.turaalkalmazas.screens.routes

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.GlobalRoute
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.service.GlobalRouteService
import com.example.turaalkalmazas.service.RouteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalDetailViewModel @Inject constructor(
    private val globalRouteService: GlobalRouteService
) : AppViewModel() {

    private val _routeDetails = MutableStateFlow(GlobalRoute())
    val routeDetails: StateFlow<GlobalRoute> get() = _routeDetails
    val routeID = MutableStateFlow("")

    fun loadRouteDetails() {
        viewModelScope.launch {
            _routeDetails.value = globalRouteService.getRouteById(routeID.value)
        }
    }


    fun addRoute(route: GlobalRoute) {
        viewModelScope.launch {
            globalRouteService.addRoute(route)
        }
    }

    fun inicialize(routeId: String){
        launchCatching{
            routeID.value = routeId
            loadRouteDetails()
        }
    }
}