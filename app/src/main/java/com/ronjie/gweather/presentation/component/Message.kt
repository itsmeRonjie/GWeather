package com.ronjie.gweather.presentation.component

data class Message(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val isError: Boolean = false
)
