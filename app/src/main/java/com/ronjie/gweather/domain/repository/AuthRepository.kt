package com.ronjie.gweather.domain.repository

import com.ronjie.gweather.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserAuthenticated: Boolean
    val currentUserId: String?
    
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String): AuthResult
    fun signOut()
    
    fun getAuthState(): Flow<Boolean>
}
