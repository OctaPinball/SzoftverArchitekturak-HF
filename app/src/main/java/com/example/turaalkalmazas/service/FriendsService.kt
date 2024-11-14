package com.example.turaalkalmazas.service

import com.example.turaalkalmazas.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FriendsService {
    suspend fun addFriend(userId: String, friendId: String)
    suspend fun acceptFriendRequest(userId: String, friendId: String)
    suspend fun rejectFriendRequest(userId: String, friendId: String)
    suspend fun getAllInRequests(userId: String) : List<User>
    suspend fun getAllFriends(userId: String): List<User>
}