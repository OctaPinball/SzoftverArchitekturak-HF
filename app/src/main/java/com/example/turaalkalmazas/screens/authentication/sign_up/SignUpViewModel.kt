package com.example.turaalkalmazas.screens.authentication.sign_up

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.ERROR_TAG
import com.example.turaalkalmazas.MAP_SCREEN
import com.example.turaalkalmazas.R
import com.example.turaalkalmazas.SIGN_UP_SCREEN
import com.example.turaalkalmazas.SnackbarManager
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

    fun onSignUpClick(context: Context, restartApp: (String) -> Unit) {
        launchCatching {
            if (!_email.value.isValidEmail()) {
                SnackbarManager.showErrorMessage(context.getString(R.string.invalid_email_format))
                Log.e(ERROR_TAG, context.getString(R.string.invalid_email_format))
                throw IllegalArgumentException(context.getString(R.string.invalid_email_format))
            }

            if (!_password.value.isValidPassword()) {
                SnackbarManager.showErrorMessage(context.getString(R.string.invalid_password_format))
                Log.e(ERROR_TAG, context.getString(R.string.invalid_password_format))
                throw IllegalArgumentException(context.getString(R.string.invalid_password_format))
            }

            if (_password.value != _confirmPassword.value) {
                SnackbarManager.showErrorMessage(context.getString(R.string.passwords_not_match))
                Log.e(ERROR_TAG, context.getString(R.string.passwords_not_match))
                throw IllegalArgumentException(context.getString(R.string.passwords_not_match))
            }

            try {
                accountService.linkAccountWithEmail(_email.value, _password.value)
                restartApp(MAP_SCREEN)
            }
            catch (e: Exception){
                SnackbarManager.showErrorMessage(e.message ?: "Unknown error")
                Log.e(ERROR_TAG, e.message ?: "Unknown error")
            }
        }
    }

    fun onSignUpWithGoogle(credential: Credential, restartApp: (String) -> Unit) {
        launchCatching {
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                accountService.linkAccountWithGoogle(googleIdTokenCredential.idToken)
                restartApp(MAP_SCREEN)
            } else {
                Log.e(ERROR_TAG, UNEXPECTED_CREDENTIAL)
            }
        }
    }
}