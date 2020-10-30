plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("com.github.dcendents.android-maven")
}
group = "com.github.jakepurple13"
android {
    compileSdkVersion(Dependencies.AndroidSdk.compile)
    buildToolsVersion(Dependencies.Versions.buildSdk)

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(Dependencies.AndroidSdk.target)
        versionCode = 1
        versionName = "1.0"

        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    //implementation fileTree(dir: "libs", include: ["*.jar"])
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Dependencies.Versions.kotlinVersion}")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    testImplementation("junit:junit:4.13.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
}

apply(from = "../publish.gradle")

/*
// build a jar with source files
task sourcesJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier = 'sources'
}

task javadoc(type: Javadoc) {
    failOnError  false
    source = android.sourceSets.main.java.sourceFiles
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
    classpath += configurations.compile
}

// build a jar with javadoc
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from javadoc.destinationDir
}

artifacts {
    archives sourcesJar
    archives javadocJar
}*/
