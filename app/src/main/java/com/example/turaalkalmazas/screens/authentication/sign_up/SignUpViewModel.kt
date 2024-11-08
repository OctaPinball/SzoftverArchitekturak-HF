package com.example.turaalkalmazas.screens.authentication.sign_up

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.ERROR_TAG
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.SIGN_UP_SCREEN
import com.example.turaalkalmazas.UNEXPECTED_CREDENTIAL
import com.example.turaalkalmazas.screens.authentication.isValidEmail
import com.example.turaalkalmazas.screens.authentication.isValidPassword
import com.example.turaalkalmazas.service.AccountService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService
) : AppViewModel() {
    // Backing properties to avoid state updates from other classes
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (!_email.value.isValidEmail()) {
                throw IllegalArgumentException("Invalid email format")
            }

            if (!_password.value.isValidPassword()) {
                throw IllegalArgumentException("Invalid password format")
            }

            if (_password.value != _confirmPassword.value) {
                throw IllegalArgumentException("Passwords do not match")
            }

            accountService.linkAccountWithEmail(_email.value, _password.value)
            openAndPopUp(MAP_SCREEN, SIGN_UP_SCREEN)
        }
    }

    fun onSignUpWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                openAndPopUp(MAP_SCREEN, SIGN_UP_SCREEN)
            } else {
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
            }
        }
    }
}