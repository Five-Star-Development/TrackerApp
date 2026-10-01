plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev.five_star.trackingapp.core.location"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.lifecycle.service)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)

    // Location
    implementation(libs.play.services.location)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.coroutines.test)
}
