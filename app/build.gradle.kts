import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.hilt)
}

/**
 * Secret configuration.
 *
 * Values are read from `config/config.properties`. When that file is absent (for example
 * in a fresh clone) the checked-in `config-example/config.properties` is used so the app
 * can still be built without any private keys.
 */
val configProperties = Properties().apply {
    val candidates = listOf(
        rootProject.file("config/config.properties"),
        rootProject.file("config-example/config.properties"),
    )
    candidates.firstOrNull { it.exists() }?.inputStream()?.use { load(it) }
}

fun prop(key: String, default: String = ""): String =
    configProperties.getProperty(key)?.takeIf { it.isNotBlank() } ?: default

fun quoted(key: String, default: String = ""): String = "\"${prop(key, default)}\""

val keystoreFile = rootProject.file("config/keystore.jks")
val hasReleaseSigning = keystoreFile.exists() &&
    prop("storePassword").isNotBlank() &&
    prop("keyAlias").isNotBlank()

// Firebase requires a `google-services.json`. Only wire it up when the file is present
// so that a clean checkout builds without any Firebase credentials.
val firebaseConfigured = file("google-services.json").exists()
if (firebaseConfigured) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    apply(plugin = "com.google.firebase.firebase-perf")
}

kapt {
    correctErrorTypes = true
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

android {
    namespace = "org.ferhatozcelik"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = prop("APPLICATION_ID", "com.ferhatozcelik.wordpress")
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 9
        versionName = "2.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", quoted("BASE_URL", "https://ferhatozcelik.com/"))
        buildConfigField("String", "YOUTUBE_API", quoted("YOUTUBE_API", "https://www.googleapis.com/youtube"))
        buildConfigField("String", "YOUTUBE_API_KEY", quoted("YOUTUBE_API_KEY"))
        buildConfigField("String", "CHANNEL_ID", quoted("CHANNEL_ID", "UC..."))
        buildConfigField("String", "PLAYLIST_ID", quoted("PLAYLIST_ID", "UU..."))
        buildConfigField("String", "DONATION_URL", quoted("DONATION_URL", "null"))
        buildConfigField("String", "TWITCH_URL", quoted("TWITCH_URL", "null"))
        buildConfigField("String", "YOUTUBE_URL", quoted("YOUTUBE_URL", "https://youtube.com/ferhatozcelik"))
        buildConfigField("String", "TWITTER_URL", quoted("TWITTER_URL", "https://www.twitter.com/ferhatozcelik"))
        buildConfigField("String", "INSTAGRAM_URL", quoted("INSTAGRAM_URL", "https://www.instagram.com/ferhatozcelik0"))
        buildConfigField("String", "SPOTIFY_URL", quoted("SPOTIFY_URL", "null"))

        resValue("string", "APPLICATION_NAME", prop("APPLICATION_NAME", "Ferhat OZCELIK"))
        resValue(
            "string",
            "APPLICATION_DESCRIPTION",
            prop("APPLICATION_DESCRIPTION", "Android Developer and Physicist"),
        )

        resValue("integer", "DONATION_STATUS", prop("DONATION_STATUS", "2"))
        resValue("integer", "TWITCH_STATUS", prop("TWITCH_STATUS", "2"))
        resValue("integer", "YOUTUBE_STATUS", prop("YOUTUBE_STATUS", "0"))
        resValue("integer", "TWITTER_STATUS", prop("TWITTER_STATUS", "0"))
        resValue("integer", "INSTAGRAM_STATUS", prop("INSTAGRAM_STATUS", "0"))
        resValue("integer", "SPOTIFY_STATUS", prop("SPOTIFY_STATUS", "2"))
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = keystoreFile
                storePassword = prop("storePassword")
                keyAlias = prop("keyAlias")
                keyPassword = prop("keyPassword")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
        }
        release {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "true"
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Local libraries
    implementation(project(":smoothbottombar"))
    implementation(project(":wordpress-core"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.play.services.base)

    // Dagger Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Glide
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Video player
    implementation(libs.youtube.player)

    // Local unit tests
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.core)

    // Instrumented unit tests
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.dexmaker.mockito)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockito.core)
}
