package com.ronjie.gweather.data.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.ronjie.gweather.domain.model.AuthResult
import com.ronjie.gweather.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val isUserAuthenticated: Boolean
        get() = auth.currentUser != null

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult(isSuccess = true)
        } catch (e: Exception) {
            AuthResult(errorMessage = e.message)
        }
    }

    override suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult(isSuccess = true)
        } catch (e: Exception) {
            AuthResult(errorMessage = e.message)
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        
        auth.addAuthStateListener(authStateListener)
        
        // Remove the listener when the flow is cancelled
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }
}
