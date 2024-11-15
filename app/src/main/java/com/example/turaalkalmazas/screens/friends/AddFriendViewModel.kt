package com.example.turaalkalmazas.screens.friends;

import android.util.Log
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.model.UserRelation
import com.example.turaalkalmazas.service.AccountService;
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FirebaseFirestore

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class AddFriendViewModel @Inject
constructor(
        private val accountService:AccountService,
        private val friendsService: FriendsService,
        private val firestore: FirebaseFirestore
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()
    private val _users = MutableStateFlow(emptyList<UserRelation>())
    val users: StateFlow<List<UserRelation>> = _users.asStateFlow()

    var searchQuery: String = ""

    init {
        launchCatching {
            _user.value = accountService.getUserProfile()
            val userId = user.value.id
            if (userId.isNotEmpty()) {
                val userDocRef = firestore.collection("users").document(userId)

                userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("AddFriendViewModel", "Snapshot listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        launchCatching {
                            _users.value = friendsService.searchUserToAdd(searchQuery, user.value.id)
                        }
                    }
                }
            }
        }
    }

    fun onSearchValueChange(){
        launchCatching {
            _users.value = friendsService.searchUserToAdd(searchQuery, user.value.id)
        }
    }

    fun onAddButtonClick(input: String){
        launchCatching {
            friendsService.addFriend(user.value.id, input)
        }
    }
}