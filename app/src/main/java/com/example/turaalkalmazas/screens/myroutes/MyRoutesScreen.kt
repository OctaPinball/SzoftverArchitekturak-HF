package com.example.turaalkalmazas.screens.myroutes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.turaalkalmazas.ROUTE_DETAIL_SCREEN
import com.example.turaalkalmazas.ROUTE_ID
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.screens.common.LogInToAccessFeature
import com.example.turaalkalmazas.ui.theme.Theme

@Composable
fun MyRoutesScreen(
    viewModel: MyRoutesViewModel = hiltViewModel(),
    openScreen: (String) -> Unit
) {
    val routes by remember { derivedStateOf { viewModel.routes } }
    val user by viewModel.user.collectAsState()

    LaunchedEffect(routes) {
        Log.d("MyRoutesScreen", "Current Routes: $routes")
    }

    Theme {
        if (user.isAnonymous) {
            LogInToAccessFeature(openScreen)
        } else {
            Scaffold { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(routes) { route ->
                        RouteItem(
                            route = route,
                            onClick = {
                                openScreen("$ROUTE_DETAIL_SCREEN?$ROUTE_ID=${route.id}")
                            },
                            onDeleteClick = { viewModel.deleteRoute(it) },
                            onSharedChange = { updatedRoute, shared ->
                                viewModel.updateSharedState(updatedRoute, shared)
                            }
                        )
                    }
                }
            }
        }}
}

@Composable
fun RouteItem(
    route: Route,
    onClick: (Route) -> Unit,
    onDeleteClick: (Route) -> Unit,
    onSharedChange: (Route, Boolean) -> Unit
) {
    var shared by remember { mutableStateOf(route.shared) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(route) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = route.name, style = MaterialTheme.typography.h6)
            Text(
                text = "Length: ${"%.1f".format(route.length.toDouble())} m, Duration: ${route.duration}, " +
                        "Difficulty: ${route.difficulty}",
                style = MaterialTheme.typography.body2
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Route"
                    )
                }
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (route.shared) "Shared" else "Private",
                        style = MaterialTheme.typography.body2)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = route.shared,
                        onCheckedChange = { newValue ->
                            onSharedChange(route, newValue)
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this route?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(route)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
