package com.ronjie.gweather.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ronjie.gweather.domain.repository.AuthRepository
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
                AuthState.Error(result.errorMessage ?: "Unknown error occurred")
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
                AuthState.Error(result.errorMessage ?: "Unknown error occurred")
            }
        }
    }

    private fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.SignedOut
    }
}

sealed class AuthEvent {
    data class SignIn(val email: String, val password: String) : AuthEvent()
    data class SignUp(val email: String, val password: String) : AuthEvent()
    object SignOut : AuthEvent()
    object ResetState : AuthEvent()
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object SignedOut : AuthState()
    data class Error(val message: String) : AuthState()
}
