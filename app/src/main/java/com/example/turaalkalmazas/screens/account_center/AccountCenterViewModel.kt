package com.example.turaalkalmazas.screens.account_center

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.SPLASH_SCREEN
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class AccountCenterViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> get() = _user

    init {
        launchCatching {
            accountService.currentUser.collect { user ->
                if (user != null) {
                    _user.value = user
                }
            }
        }
    }

    fun onUpdateDisplayNameClick(newDisplayName: String) {
        launchCatching {
            accountService.updateDisplayName(newDisplayName)
        }
    }

    fun onSignOutClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.signOut()
            restartApp(SPLASH_SCREEN)
        }
    }

    fun onDeleteAccountClick(restartApp: (String) -> Unit) {
        launchCatching {
            accountService.deleteAccount()
            restartApp(SPLASH_SCREEN)
        }
    }
}