// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false

}

buildscript {      // this is safe arg for making data transferable between different navigation.
    repositories {
        google()
    }
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }
}