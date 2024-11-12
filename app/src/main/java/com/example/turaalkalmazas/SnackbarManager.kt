package com.example.turaalkalmazas

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class SnackbarMessage(val text: String) {
    class Info(text: String) : SnackbarMessage(text)
    class Error(text: String) : SnackbarMessage(text)
}

object SnackbarManager {
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackbarMessage?>
        get() = messages

    fun showInfoMessage(message: String) {
        messages.value = SnackbarMessage.Info(message)
    }

    fun showErrorMessage(message: String) {
        messages.value = SnackbarMessage.Error(message)
    }

    fun clearSnackbarState() {
        messages.value = null
    }
}
