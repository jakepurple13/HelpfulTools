import org.gradle.kotlin.dsl.kotlin

object Dependencies {
    const val material = "com.google.android.material:material:1.1.0"

    object Versions {
        const val coroutineVersion = "1.4.0"
        const val kotlinVersion = "1.4.10"
        const val buildToolsVersion = "4.1.0"
        const val buildSdk = "30.0.2"
    }


    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutineVersion}"
    }

    object BuildPlugins {
        const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.buildToolsVersion}"
        const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
        const val androidApplication = "com.android.application"
        const val kotlinAndroid = "kotlin-android"
        const val kotlinAndroidExtensions = "kotlin-android-extensions"
        const val kotlinKapt = "kotlin-kapt"
    }

    object AndroidSdk {
        const val compile = 30
        const val target = compile
    }

    object Libraries {
        private object Versions {
            const val jetpack = "1.1.0"
            const val constraintLayout = "1.1.3"
            const val ktx = "1.2.0"
        }

        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Dependencies.Versions.kotlinVersion}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.jetpack}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val ktxCore = "androidx.core:core-ktx:${Versions.ktx}"
    }

    object TestLibraries {
        private object Versions {
            const val junit4 = "4.13"
            const val testRunner = "1.1.0-alpha4"
            const val espresso = "3.2.0"
        }

        const val junit4 = "junit:junit:${Versions.junit4}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    }

}

fun org.gradle.kotlin.dsl.DependencyHandlerScope.implement(item: Array<String>) = item.forEach { add("implementation", it) }

fun org.gradle.kotlin.dsl.PluginDependenciesSpecScope.defaultPlugins(withKapt: Boolean = false) {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    if (withKapt) kotlin("kapt")
}