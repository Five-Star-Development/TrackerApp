plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

fun getSecret(name: String): String? {
    return try {
        val process = ProcessBuilder(
            "gcloud", "secrets", "versions", "access", "latest", "--secret=$name"
        )
            // keep gcloud warnings out of the secret value, show them in the build output instead
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()

        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()
        if (exitCode == 0 && output.isNotEmpty()) output else {
            logger.warn("Failed to get secret $name from gcloud (exit $exitCode)")
            null
        }
    } catch (e: Exception) {
        logger.warn("Error reading secret $name: ${e.message}")
        null
    }
}

fun getCachedSecret(name: String): String {
    val cacheFile = File(rootDir, "local-secrets/$name.txt")

    // an empty cache file is treated as missing so a previously failed fetch is retried
    val cached = cacheFile.takeIf { it.exists() }?.readText()?.trim()
    if (!cached.isNullOrEmpty()) return cached

    val secret = getSecret(name) ?: throw GradleException(
        "Secret $name is not available. Run `gcloud auth login` and build again, " +
            "or put the value into local-secrets/$name.txt"
    )
    cacheFile.parentFile.mkdirs()
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
    implementation(project(":feature:modeselection"))
    implementation(project(":feature:tracker"))
    implementation(project(":feature:observer"))
    implementation(project(":core:settings"))
    implementation(project(":core:location"))

    // Firebase (database instance is created in TrackingApplication)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

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

    // Unit Tests
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
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