plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Dependencies.AndroidSdk.compile)
    buildToolsVersion(Dependencies.Versions.buildSdk)
    defaultConfig {
        applicationId = "com.programmersbox.helpfultools"
        minSdkVersion(27)
        targetSdkVersion(Dependencies.AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/deserialization.kotlin_module")
        exclude("META-INF/util.runtime.kotlin_module")
        exclude("META-INF/metadata.kotlin_module")
        exclude("META-INF/metadata.jvm.kotlin_module")
        exclude("META-INF/descriptors.jvm.kotlin_module")
        exclude("META-INF/descriptors.kotlin_module")
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

}

dependencies {
    //implementation fileTree (dir: "libs", include: ["*.jar"])
    implementation(Dependencies.Libraries.kotlinStdLib)
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.3")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.google.android.material:material:1.2.1")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    val jakepurple13 = "10.5.2"
    implementation("com.github.jakepurple13.HelpfulTools:flowutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:gsonutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:helpfulutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:loggingutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:rxutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:dragswipe:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:funutils:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:dslannotations:$jakepurple13")
    //kapt("com.github.jakepurple13.HelpfulTools:dslprocessor:$jakepurple13")
    implementation("com.github.jakepurple13.HelpfulTools:thirdpartyutils:$jakepurple13")
    implementation(project(":testingplayground"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Dependencies.Versions.coroutineVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Dependencies.Versions.coroutineVersion}")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.jakewharton.rxbinding2:rxbinding:2.2.0")
    implementation("com.jakewharton.rxbinding2:rxbinding-kotlin:2.2.0")
}