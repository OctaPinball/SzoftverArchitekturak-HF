package com.example.turaalkalmazas.screens.friends

import android.util.Log
import androidx.collection.emptyLongSet
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user
    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    var searchQuery: String = ""

    init {
        launchCatching {
            val currentUser = accountService.currentUser.firstOrNull()
            if (currentUser != null) {
                _user.value = currentUser
            }

            val userId = user.value.id
            if (userId.isNotEmpty()) {
                val userDocRef = firestore.collection("users").document(userId)

                userDocRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FriendViewModel", "Snapshot listener error", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        launchCatching {
                            _users.value = if (searchQuery.isEmpty())
                                friendsService.getAllFriends(user.value.id)
                            else friendsService.searchFriends(user.value.id, searchQuery)
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
        }
    }

    fun onSearchValueChange(){
        launchCatching {
            _users.value = friendsService.searchFriends(user.value.id, searchQuery)
        }
    }
}