package com.example.turaalkalmazas.screens.friends

import android.util.Log
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject
constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()
    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        launchCatching {
            _user.value = accountService.getUserProfile()
            val userId = user.value.id
            if (userId.isNotEmpty()) {
                val userDocRef = firestore.collection("users").document(userId)

                userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FriendRequestViewModel", "Snapshot listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        launchCatching {
                            _users.value = friendsService.getAllInRequests(user.value.id)
                        }
                    }
                }
            }
        }
    }

    fun onAcceptClick(friendId: String) {
        launchCatching {
            friendsService.acceptFriendRequest(user.value.id, friendId)
        }
    }

    fun onRejectClick(friendId: String) {
        launchCatching {
            friendsService.rejectFriendRequest(user.value.id, friendId)
        }
    }

}