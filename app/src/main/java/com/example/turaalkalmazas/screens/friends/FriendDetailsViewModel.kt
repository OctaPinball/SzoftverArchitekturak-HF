package com.example.turaalkalmazas.screens.friends

import android.util.Log
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import com.example.turaalkalmazas.model.UserRelationType
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FriendDetailsViewModel @Inject
constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()
    private val _userDetails = MutableStateFlow(UserRelation(User(), UserRelationType.NONE))
    val userDetails: StateFlow<UserRelation> = _userDetails.asStateFlow()
    val userId = MutableStateFlow("")

    fun initialize(userIdIn: String) {
        launchCatching {
            userId.value = userIdIn
            _user.value = accountService.getUserProfile()

            val userId = userId.value
            if (userId.isNotEmpty()) {
                val userDocRef = firestore.collection("users").document(userId)

                userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FriendDetailsViewModel", "Snapshot listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        launchCatching {
                            _userDetails.value = friendsService.getUserDetails(user.value.id, userId)
                        }
                    }
                }
            }
        }
    }

    fun onAddFriendClick(){
        launchCatching {
            friendsService.addFriend(user.value.id, userId.value)
            _userDetails.value = friendsService.getUserDetails(user.value.id, userId.value)
        }
    }

    fun onRemoveFriendClick(){
        launchCatching {
            friendsService.removeFriend(user.value.id, userId.value)
            _userDetails.value = friendsService.getUserDetails(user.value.id, userId.value)
        }
    }

    fun onAcceptClick(){
        launchCatching {
            friendsService.acceptFriendRequest(user.value.id, userId.value)
            _userDetails.value = friendsService.getUserDetails(user.value.id, userId.value)
        }
    }

    fun onRejectClick(){
        launchCatching {
            friendsService.rejectFriendRequest(user.value.id, userId.value)
            _userDetails.value = friendsService.getUserDetails(user.value.id, userId.value)
        }
    }
}