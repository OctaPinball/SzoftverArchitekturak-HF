package com.example.turaalkalmazas.screens.friends

import android.util.Log
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.Route
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import com.example.turaalkalmazas.model.UserRelationType
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.example.turaalkalmazas.service.RouteService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailsViewModel @Inject
constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService,
    private val routeService: RouteService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user
    private val _userDetails = MutableStateFlow(UserRelation(User(), UserRelationType.NONE))
    val userDetails: StateFlow<UserRelation> = _userDetails.asStateFlow()
    val userId = MutableStateFlow("")
    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> get() = _routes

    fun initialize(userIdIn: String) {
        launchCatching {
            userId.value = userIdIn

            val currentUser = accountService.currentUser.firstOrNull()
            if (currentUser != null) {
                _user.value = currentUser
            }

            val userIdValue = userId.value
            if (userIdValue.isNotEmpty()) {
                val userDocRef = firestore.collection("users").document(userIdValue)

                userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FriendDetailsViewModel", "Snapshot listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        launchCatching {
                            _userDetails.value = friendsService.getUserDetails(user.value.id, userIdValue)
                        }
                    }
                }
            }

            launch {
                accountService.currentUser.collect { user ->
                    if (user != null) {
                        _user.value = user
                    }
                }
            }
            launch {
                _routes.value = routeService.getFriendRoutes(userIdIn)
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