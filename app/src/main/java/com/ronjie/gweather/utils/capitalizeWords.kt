package com.ronjie.gweather.utils

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
