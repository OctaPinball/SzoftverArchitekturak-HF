package com.example.turaalkalmazas.service.impl

import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendsServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FriendsService {
    override suspend fun addFriend(userId: String, friendId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        val friendDocRef = firestore.collection("users").document(friendId)

        firestore.runTransaction { transaction ->
            transaction.update(userDocRef, "requests_out", FieldValue.arrayUnion(friendId))
            transaction.update(friendDocRef, "requests_in", FieldValue.arrayUnion(userId))
        }.await()
    }

    override suspend fun acceptFriendRequest(userId: String, friendId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        val friendDocRef = firestore.collection("users").document(friendId)

        firestore.runTransaction { transaction ->
            transaction.update(userDocRef, "requests_in", FieldValue.arrayRemove(friendId))
            transaction.update(friendDocRef, "requests_out", FieldValue.arrayRemove(userId))
            transaction.update(userDocRef, "friends", FieldValue.arrayUnion(friendId))
            transaction.update(friendDocRef, "friends", FieldValue.arrayUnion(userId))
        }
    }

    override suspend fun rejectFriendRequest(userId: String, friendId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        val friendDocRef = firestore.collection("users").document(friendId)

        firestore.runTransaction { transaction ->
            transaction.update(userDocRef, "requests_in", FieldValue.arrayRemove(friendId))
            transaction.update(friendDocRef, "requests_out", FieldValue.arrayRemove(userId))
        }
    }

    override suspend fun getAllInRequests(userId: String): List<User> = withContext(Dispatchers.IO) {
        val userDocRef = firestore.collection("users").document(userId)
        val userSnapshot = userDocRef.get().await()

        val requestIds = userSnapshot.get("requests_in") as? List<String> ?: emptyList()

        val users = mutableListOf<User>()
        for (requestId in requestIds) {
            val friendDocRef = firestore.collection("users").document(requestId)
            friendDocRef.get().await().toObject(User::class.java)?.let { users.add(it) }
        }

        users
    }

    override suspend fun getAllFriends(userId: String): List<User> = withContext(Dispatchers.IO){
        val userDocRef = firestore.collection("users").document(userId)
        val userSnapshot = userDocRef.get().await()

        val friendIds = userSnapshot.get("friends") as? List<String> ?: emptyList()

        val friends = mutableListOf<User>()
        for (friendId in friendIds) {
            val friendDocRef = firestore.collection("users").document(friendId)
            friendDocRef.get().await().toObject(User::class.java)?.let { friends.add(it) }
        }

        friends
    }

}