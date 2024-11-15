package com.example.turaalkalmazas.service.impl


import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AccountService {

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser.toNotesUser())
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getUserProfile(): User {
        return Firebase.auth.currentUser.toNotesUser()
    }

    override suspend fun createAnonymousAccount() {
        Firebase.auth.signInAnonymously().await()
    }

    override suspend fun updateDisplayName(newDisplayName: String) {
        val profileUpdates = userProfileChangeRequest {
            displayName = newDisplayName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).await()
        Firebase.auth.currentUser?.let { user ->
            saveUserToFirestore(user)
        }
    }

    override suspend fun linkAccountWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.currentUser!!.linkWithCredential(firebaseCredential).await()
    }

    override suspend fun linkAccountWithEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        Firebase.auth.currentUser!!.linkWithCredential(credential).await()
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(firebaseCredential).await()
        Firebase.auth.currentUser?.let { user ->
            saveUserToFirestore(user)
        }
    }

    override suspend fun signInWithEmail(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
        Firebase.auth.currentUser?.let { user ->
            saveUserToFirestore(user)
        }
    }

    override suspend fun signOut() {
        Firebase.auth.signOut()
        createAnonymousAccount()
    }

    override suspend fun deleteAccount() {
        val userId = Firebase.auth.currentUser?.uid.orEmpty()
        Firebase.auth.currentUser!!.delete().await()
        firestore.collection("users").document(userId).delete().await()
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser) {
        val userDoc = firestore.collection("users").document(user.uid)
        val email = user.email.orEmpty()
        val displayName = user.displayName.orEmpty()
        val keywords = generateKeywords(email) + generateKeywords(displayName)
        val userData = mapOf(
            "id" to user.uid,
            "email" to email,
            "displayName" to displayName,
            "isAnonymous" to user.isAnonymous,
            "keywords" to keywords
        )
        userDoc.set(userData, SetOptions.merge()).await()
    }

    private fun generateKeywords(text: String): List<String> {
        val keywords = mutableListOf<String>()
        val lowerText = text.lowercase()

        for (i in lowerText.indices) {
            for (j in i + 1..lowerText.length) {
                keywords.add(lowerText.substring(i, j))
            }
        }
        return keywords.distinct()
    }

    private fun FirebaseUser?.toNotesUser(): User {
        return if (this == null) User() else User(
            id = this.uid,
            email = this.email ?: "",
            displayName = this.displayName ?: "",
            isAnonymous = this.isAnonymous
        )
    }
}
