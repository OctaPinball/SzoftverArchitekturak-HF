package com.example.turaalkalmazas.screens.main

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        launchCatching {
            accountService.currentUser.collect { profile ->
                profile?.let {
                    _user.value = it
                }
            }
        }
    }
}