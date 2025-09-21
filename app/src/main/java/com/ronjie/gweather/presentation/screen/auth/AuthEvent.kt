package com.ronjie.gweather.presentation.screen.auth

sealed class AuthEvent {
    data class SignIn(val email: String, val password: String) : AuthEvent()
    data class SignUp(val email: String, val password: String) : AuthEvent()
    object SignOut : AuthEvent()
    object ResetState : AuthEvent()
}
