package com.example.turaalkalmazas.service.impl

import android.util.Log
import com.example.turaalkalmazas.SnackbarManager
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import com.example.turaalkalmazas.model.UserRelationType
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

    override suspend fun getAllOutRequests(userId: String): List<User> = withContext(Dispatchers.IO) {
        val userDocRef = firestore.collection("users").document(userId)
        val userSnapshot = userDocRef.get().await()

        val requestIds = userSnapshot.get("requests_out") as? List<String> ?: emptyList()

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

    override suspend fun searchUserToAdd(query: String, userId: String): List<UserRelation> = withContext(Dispatchers.IO) {
        val users = mutableListOf<UserRelation>()

        users.addAll(
            searchUser(query).map { user ->
                UserRelation(user = user, relationType = UserRelationType.NONE)
            }
        )

        users.removeAll { it.user.id == userId }

        val friends = getAllFriends(userId)
        users.removeAll { friends.contains(it.user) }


        val outRequests = getAllOutRequests(userId)
        users.forEach { userRelation ->
            if (outRequests.any { friend -> friend.id == userRelation.user.id }) {
                userRelation.relationType = UserRelationType.OUT_REQUEST
            }
        }

        val inRequests = getAllInRequests(userId)
        users.forEach { userRelation ->
            if (inRequests.any { friend -> friend.id == userRelation.user.id }) {
                userRelation.relationType = UserRelationType.IN_REQUEST
            }
        }

        users
    }

    override suspend fun getUserDetails(signedInUserId: String, userId: String): UserRelation {
        Log.d("SuperLog FriendsService", "getUserDetails called with userId: $signedInUserId")

        val userDocRef = firestore.collection("users").document(userId)
        val user = userDocRef.get().await().toObject(User::class.java)
            ?: throw Exception("User not found")

        if(getAllFriends(signedInUserId).any { friend -> friend.id == userId }) {
            return UserRelation(user = user, relationType = UserRelationType.FRIEND)
        }
        else if (getAllInRequests(signedInUserId).any { friend -> friend.id == userId }){
            return UserRelation(user = user, relationType = UserRelationType.IN_REQUEST)
        }
        else if (getAllOutRequests(signedInUserId).any { friend -> friend.id == userId }){
            return UserRelation(user = user, relationType = UserRelationType.OUT_REQUEST)
        }
        return UserRelation(user = user, relationType = UserRelationType.NONE)
    }

    override suspend fun removeFriend(userId: String, friendId: String) {
        val userDocRef = firestore.collection("users").document(userId)
        val friendDocRef = firestore.collection("users").document(friendId)

        firestore.runTransaction { transaction ->
            transaction.update(userDocRef, "friends", FieldValue.arrayRemove(friendId))
            transaction.update(friendDocRef, "friends", FieldValue.arrayRemove(userId))
        }
    }

    override suspend fun searchFriends(userId: String, search: String): List<User> = withContext(Dispatchers.IO) {
        val friends = getAllFriends(userId)
        val out = friends.filter { user -> user.displayName.contains(search, ignoreCase = true) || user.email.contains(search, ignoreCase = true) }
        out
    }

    private suspend fun searchUser(query: String): List<User> {
        val users = mutableListOf<User>()

        val userQuery = firestore.collection("users")
            .whereArrayContains("keywords", query.lowercase())
            .get()
            .await()

        for (doc in userQuery.documents) {
            doc.toObject(User::class.java)?.let { users.add(it) }
        }

        return users
    }
}