
import java.io.BufferedReader
import java.io.InputStreamReader

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

fun getSecret(name: String): String {
    return try {
        val process = ProcessBuilder(
            "gcloud", "secrets", "versions", "access", "latest", "--secret=$name"
        ).redirectErrorStream(true).start()

        val output = BufferedReader(InputStreamReader(process.inputStream)).readText().trim()
        val exitCode = process.waitFor()
        if (exitCode == 0) output else {
            println("Warning: failed to get secret $name (exit $exitCode)")
            ""
        }
    } catch (e: Exception) {
        println("Error reading secret $name: ${e.message}")
        ""
    }
}

fun getCachedSecret(name: String): String {
    val cacheDir = File(rootDir, "local-secrets")
    val cacheFile = File(cacheDir, "$name.txt")

    if (cacheFile.exists()) return cacheFile.readText().trim()

    val secret = getSecret(name)
    cacheDir.mkdirs()
    cacheFile.writeText(secret)
    return secret
}

android {
    namespace = "dev.five_star.trackingapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.five_star.trackingapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = getCachedSecret("GOOGLE_MAPS_API_KEY")
        buildConfigField("String", "FIREBASE_DATABASE_URL", "\"${getCachedSecret("FIREBASE_DATABASE_URL")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.database.ktx)

    // Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Unit Tests
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(kotlin("test"))

    // Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}