package com.example.turaalkalmazas.model

data class UserRelation (
    val user: User,
    var relationType: UserRelationType
)

enum class UserRelationType {
    IN_REQUEST,
    OUT_REQUEST,
    NONE,
}
