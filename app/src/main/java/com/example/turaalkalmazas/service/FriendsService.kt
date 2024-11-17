package com.example.turaalkalmazas.service

import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FriendsService {
    suspend fun addFriend(userId: String, friendId: String)
    suspend fun acceptFriendRequest(userId: String, friendId: String)
    suspend fun rejectFriendRequest(userId: String, friendId: String)
    suspend fun getAllInRequests(userId: String) : List<User>
    suspend fun getAllOutRequests(userId: String) : List<User>
    suspend fun getAllFriends(userId: String): List<User>
    suspend fun searchUserToAdd(query: String, userId: String): List<UserRelation>
    suspend fun getUserDetails(signedInUserId: String, userId: String): UserRelation
    suspend fun removeFriend(userId: String, friendId: String)
    suspend fun searchFriends(userId: String, search: String): List<User>
}