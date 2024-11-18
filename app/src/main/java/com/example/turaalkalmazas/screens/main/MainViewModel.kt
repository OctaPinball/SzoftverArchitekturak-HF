package com.example.turaalkalmazas.screens.main

import android.util.Log
import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    init {
        launchCatching {
            accountService.currentUser
                .collect { profile ->
                    profile?.let {
                        // Kezdetben a gyorsabb getUserProfile eredményét állítjuk be
                        _user.value = accountService.getUserProfile()

                        // Ha van érvényes felhasználói azonosító, indítjuk a részletes adatok lekérését
                        val userId = _user.value.id
                        if (userId.isNotEmpty()) {
                            val detailedUserJob = launch {
                                try {
                                    // Frissítjük az állapotot a részletes adatokkal
                                    _user.value = accountService.getDetailedUserProfile()
                                } catch (e: Exception) {
                                    Log.e("MainViewModel", "Failed to load detailed profile", e)
                                }
                            }

                            // Snapshot listener az adatok folyamatos frissítéséhez
                            val userDocRef = firestore.collection("users").document(userId)
                            userDocRef.addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    Log.e("MainViewModel", "Snapshot listener error", error)
                                    return@addSnapshotListener
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    launch {
                                        try {
                                            val updatedUser = snapshot.toObject(User::class.java)
                                            if (updatedUser != null) {
                                                _user.value = updatedUser
                                            }
                                        } catch (e: Exception) {
                                            Log.e("MainViewModel", "Failed to process snapshot", e)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}
