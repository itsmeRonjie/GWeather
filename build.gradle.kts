// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57.1")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.2.20-2.0.3")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.2.20")
        classpath("com.google.gms:google-services:4.4.3")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
}
