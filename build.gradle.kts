// build.gradle.kts (Project: SmartRetail)
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    // INI YANG SERING LUPA DITAMBAHKAN:
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}