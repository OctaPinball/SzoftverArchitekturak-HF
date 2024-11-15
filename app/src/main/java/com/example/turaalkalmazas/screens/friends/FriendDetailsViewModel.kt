package com.example.turaalkalmazas.screens.friends

import com.example.turaalkalmazas.AppViewModel
import com.example.turaalkalmazas.service.AccountService
import com.example.turaalkalmazas.service.FriendsService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendDetailsViewModel @Inject
constructor(
    private val accountService: AccountService,
    private val friendsService: FriendsService,
    private val firestore: FirebaseFirestore
) : AppViewModel() {

}