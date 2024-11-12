package com.example.turaalkalmazas.screens.authentication.sign_in

import android.content.Context
import android.provider.Settings.Secure.getString
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.ERROR_TAG
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.SIGN_IN_SCREEN
import com.example.turaalkalmazas.SnackbarManager
import com.example.turaalkalmazas.UNEXPECTED_CREDENTIAL
import com.example.turaalkalmazas.service.AccountService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {
    // Backing properties to avoid state updates from other classes
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun onSignInClick(context: Context, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (_email.value.isBlank()) {
                SnackbarManager.showErrorMessage(context.getString(R.string.email_empty))
                return@launchCatching
            }

            if (_password.value.isBlank()) {
                SnackbarManager.showErrorMessage(context.getString(R.string.password_empty))
                return@launchCatching
            }

            try
            {
                accountService.signInWithEmail(_email.value, _password.value)
                openAndPopUp(MAP_SCREEN, SIGN_IN_SCREEN)
            }
            catch (e: Exception){
                SnackbarManager.showErrorMessage(e.message ?: "Unknown error")
                Log.e(ERROR_TAG, e.message ?: "Unknown error")
            }

        }
    }

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                openAndPopUp(MAP_SCREEN, SIGN_IN_SCREEN)
            } else {
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
                SnackbarManager.showErrorMessage(UNEXPECTED_CREDENTIAL)
            }
        }
    }
}