package com.example.turaalkalmazas.service.impl


import android.util.Log
import com.example.turaalkalmazas.SnackbarManager
import com.example.turaalkalmazas.model.User
import com.example.turaalkalmazas.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class AccountServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AccountService {

    override val currentUser: Flow<User?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(firebaseUser.toUser()).isSuccess

                // Indítjuk a Firestore figyelőt, és adatokat küldünk a flow-ba
                val firestoreFlow = startFirestoreListener(firebaseUser.uid)
                val job = firestoreFlow.onEach { user ->
                    trySend(user).isSuccess
                }.launchIn(this) // A flow gyűjtése a callbackFlow scope-jában
            } else {
                trySend(null).isSuccess
            }
        }

        // Hozzáadjuk az auth listenert
        Firebase.auth.addAuthStateListener(authListener)

        // Lezáráskezelés
        awaitClose {
            Firebase.auth.removeAuthStateListener(authListener)
        }
    }



    private var firestoreListenerRegistration: ListenerRegistration? = null

    private fun startFirestoreListener(userId: String): Flow<User?> = callbackFlow {
        val userDocRef = firestore.collection("users").document(userId)
        val registration = userDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("SuperLog FirestoreListener", "Error listening to Firestore document: ${error.message}")
                close(error) // Hibával lezárjuk a flow-t
                return@addSnapshotListener
            }

            snapshot?.let {
                val data = it.data ?: return@let
                val user = User(
                    id = data["id"] as? String ?: "",
                    email = data["email"] as? String ?: "",
                    displayName = data["displayName"] as? String ?: "",
                    isAnonymous = data["isAnonymous"] as? Boolean ?: true
                )
                // Adat küldése a flow-ba
                trySend(user).isSuccess
            }
        }
        Log.d("SuperLog FirestoreListener", "Firestore listener started")

        awaitClose {
            // Lezáráskor a figyelő leállítása
            registration.remove()
            Log.d("SuperLog FirestoreListener", "Firestore listener stopped")
        }
    }



    private fun stopFirestoreListener() {
        firestoreListenerRegistration?.remove()
        firestoreListenerRegistration = null
        Log.d("SuperLog FirestoreListener", "Firestore listener stopped")
    }


    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getUserProfile(): User {
        return Firebase.auth.currentUser.toUser()
    }

    override suspend fun getDetailedUserProfile(): User {
        val userDocRef = firestore.collection("users").document(currentUserId)
        val documentSnapshot = userDocRef.get().await()
        Log.d("SuperLog Document", "Data: ${documentSnapshot.data}")
        if (!documentSnapshot.exists()) {
            throw Exception("User document not found")
        }

        val data = documentSnapshot.data ?: throw Exception("Document is empty")

        // Explicit mappelés
        val id = data["id"] as? String ?: ""
        val email = data["email"] as? String ?: ""
        val displayName = data["displayName"] as? String ?: ""
        val isAnonymous = data["isAnonymous"] as? Boolean ?: true


        return User(
            id = id,
            email = email,
            displayName = displayName,
            isAnonymous = isAnonymous
        )
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

    override suspend fun linkAccountWithEmail(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        Firebase.auth.currentUser!!.linkWithCredential(credential).await()
        Firebase.auth.currentUser?.reload()?.await()
        Firebase.auth.currentUser?.let { user ->
            saveUserToFirestore(user)
        }
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
        removeFromEverywhere(userId)
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser) {
        val userDoc = firestore.collection("users").document(user.uid)
        val email = user.email.orEmpty()
        val displayName = if(user.displayName == null || user.displayName == "") email else user.displayName.orEmpty()
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

    private fun removeFromEverywhere(userId: String) {
        val usersRef = firestore.collection("users")
        val batch = firestore.batch()

        usersRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentRef = usersRef.document(document.id)
                    val data = document.data

                    val updatedFriends = (data["friends"] as? List<String>)?.filterNot { it == userId }
                    val updatedRequestsIn = (data["requests_in"] as? List<String>)?.filterNot { it == userId }
                    val updatedRequestsOut = (data["requests_out"] as? List<String>)?.filterNot { it == userId }

                    if (updatedFriends != data["friends"] ||
                        updatedRequestsIn != data["requests_in"] ||
                        updatedRequestsOut != data["requests_out"]) {

                        batch.update(documentRef, mapOf(
                            "friends" to updatedFriends,
                            "requests_in" to updatedRequestsIn,
                            "requests_out" to updatedRequestsOut
                        ))
                    }
                }

                // Batch commit
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("BatchUpdate", "Batch update successful")
                    }
                    .addOnFailureListener { exception ->
                        Log.d("BatchUpdate", "Batch update failed: ${exception.message}")
                        exception.message?.let { SnackbarManager.showErrorMessage(it) }
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("BatchUpdate", "Error getting documents: ${exception.message}")
                exception.message?.let { SnackbarManager.showErrorMessage(it) }
            }
    }

    private fun FirebaseUser?.toUser(): User {
        return if (this == null) {
            User()
        } else {
            User(
                id = this.uid,
                email = this.email.orEmpty(),
                displayName = this.displayName.orEmpty(),
                isAnonymous = this.isAnonymous
            )
        }
    }

}
