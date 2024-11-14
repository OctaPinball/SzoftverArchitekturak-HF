package com.example.turaalkalmazas.screens.friends

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService
) : AppViewModel() {
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()
    private val _users = MutableStateFlow(emptyList<User>())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        launchCatching {
            _user.value = accountService.getUserProfile()
            _users.value = friendsService.getAllFriends(user.value.id)
        }
    }

    fun onSearchValueChange(input: String){
        launchCatching {
            // Search implement
        }
    }
}