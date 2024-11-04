package com.example.turaalkalmazas.screens.account_center

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.SIGN_IN_SCREEN
import com.example.turaalkalmazas.SPLASH_SCREEN
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class AccountCenterViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {
    // Backing property to avoid state updates from other classes
    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        launchCatching {
            _user.value = accountService.getUserProfile()
        }
    }

    fun onUpdateDisplayNameClick(newDisplayName: String) {
        launchCatching {
            accountService.updateDisplayName(newDisplayName)
            _user.value = accountService.getUserProfile()
        }
    }

    fun onSignInClick(openScreen: (String) -> Unit) = openScreen(SIGN_IN_SCREEN)

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