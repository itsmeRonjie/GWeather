package com.ronjie.gweather.utils

import com.google.firebase.auth.FirebaseAuthException

fun getFriendlyAuthErrorMessage(exception: Exception): String {
    // First check if the exception message contains any known error patterns
    val message = exception.message ?: "An unknown error occurred"

    return when {
        // Handle Firebase Auth exceptions
        exception is FirebaseAuthException -> {
            when (exception.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Please enter a valid email address"
                "ERROR_WRONG_PASSWORD" -> "Incorrect password. Please try again"
                "ERROR_USER_NOT_FOUND" -> "No account found with this email"
                "ERROR_USER_DISABLED" -> "This account has been disabled"
                "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later"
                "ERROR_OPERATION_NOT_ALLOWED" -> "This operation is not allowed"
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered"
                "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use a stronger password"
                "ERROR_INVALID_CREDENTIAL" -> "Invalid login credentials"
                else -> "Authentication failed: ${exception.message}"
            }
        }

        message.contains("given string is empty or null", ignoreCase = true) ->
            "Please fill in all required fields"

        message.contains("email address is badly formatted", ignoreCase = true) ->
            "Please enter a valid email address"

        message.contains("auth credential is incorrect", ignoreCase = true) ->
            "Incorrect email or password. Please try again"

        message.contains("network error", ignoreCase = true) ->
            "Network error. Please check your internet connection"

        message.contains("Password should be at least 6 characters", ignoreCase = true) ->
            "Password should be at least 6 characters"

        else -> message.removePrefix("An error occurred: ").replaceFirstChar { it.uppercase() }
    }
}
