package com.example.turaalkalmazas.screens.myroutes

import androidx.compose.runtime.mutableStateOf
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyRoutesViewModel @Inject constructor() : AppViewModel() {

    private val _routes = mutableStateOf(
        listOf(
            Route("1", "Forest Trail", "5km", "1hr", "Easy", false),
            Route("2", "Mountain Path", "12km", "3hrs", "Hard", true)
        )
    )
    val routes: List<Route> get() = _routes.value

    fun deleteRoute(route: Route) {
        _routes.value = _routes.value.filter { it.id != route.id }
    }

    fun updateSharedState(route: Route, isShared: Boolean) {
        _routes.value = _routes.value.map {
            if (it.id == route.id) it.copy(isShared = isShared) else it
        }
    }
}