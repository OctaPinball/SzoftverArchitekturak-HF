// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("com.android.application") version "8.7.1" apply false
    id("com.android.library") version "8.7.1" apply false
    kotlin("jvm") version "2.0.0"
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
    repositories {
        mavenCentral()
    }
}
val defaultMinSdkVersion by extra(23)
