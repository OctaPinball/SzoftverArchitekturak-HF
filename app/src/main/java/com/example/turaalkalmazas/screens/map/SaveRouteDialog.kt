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
            Text(text = "Útvonal mentése")
        },
        text = {
            Column {
                Text(text = "Kérlek, add meg az útvonal nevét!")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = routeName,
                    onValueChange = { routeName = it },
                    label = { Text("Útvonal neve") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(routeName)
                onDismiss()
            }) {
                Text("Mentés")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}