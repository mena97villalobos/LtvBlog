// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha11")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath(
            "com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.0")
    }
}

plugins { id("com.diffplug.spotless") version "5.15.0" }

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt", "**/*.kts")
        targetExclude("$buildDir/**/*.kt")
        ktfmt("0.24").dropboxStyle()
    }
}
