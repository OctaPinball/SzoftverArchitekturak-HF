package com.example.turaalkalmazas.screens.splash

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.SPLASH_SCREEN
import com.example.turaalkalmazas.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser()) openAndPopUp(MAP_SCREEN, SPLASH_SCREEN)
        else createAnonymousAccount(openAndPopUp)
    }

    private fun createAnonymousAccount(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.createAnonymousAccount()
            openAndPopUp(MAP_SCREEN, SPLASH_SCREEN)
        }
    }
}