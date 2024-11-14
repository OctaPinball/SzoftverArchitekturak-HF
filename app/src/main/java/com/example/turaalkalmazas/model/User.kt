package com.example.turaalkalmazas.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val isAnonymous: Boolean = true
)