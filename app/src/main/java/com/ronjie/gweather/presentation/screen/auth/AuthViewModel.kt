package com.ronjie.gweather.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.domain.repository.AuthRepository
import com.ronjie.gweather.utils.getFriendlyAuthErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SignIn -> signIn(event.email, event.password)
            is AuthEvent.SignUp -> signUp(event.email, event.password)
            AuthEvent.SignOut -> signOut()
            AuthEvent.ResetState -> _authState.value = AuthState.Initial
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signIn(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                val friendlyMessage = result.errorMessage?.let { error ->
                    getFriendlyAuthErrorMessage(Exception(error))
                } ?: "An unknown error occurred"
                AuthState.Error(friendlyMessage)
            }
        }
    }

    private fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Success
            } else {
                val friendlyMessage = result.errorMessage?.let { error ->
                    getFriendlyAuthErrorMessage(Exception(error))
                } ?: "An unknown error occurred"
                println("Auth error: $friendlyMessage")

                AuthState.Error(friendlyMessage)
            }
        }
    }

    private fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.SignedOut
    }
}
