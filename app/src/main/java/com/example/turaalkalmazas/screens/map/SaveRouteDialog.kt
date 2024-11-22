package com.example.turaalkalmazas.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

@Composable
fun SaveRouteDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var routeName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Saving a path")
        },
        text = {
            Column {
                Text(text = "Please enter the name of the path!")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text("Name of the path") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(routeName)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}