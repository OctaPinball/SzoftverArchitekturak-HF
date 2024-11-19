package com.example.turaalkalmazas.screens.main

import androidx.lifecycle.viewModelScope
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    init {
        viewModelScope.launch {
            accountService.currentUser.collect { user ->
                if (user != null) {
                    _user.value = user
                }
            }
        }
    }
}
