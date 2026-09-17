import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    `maven-publish`
}

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

kapt {
    correctErrorTypes = true
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "org.ferhatozcelik.core"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        buildConfigField("String", "BASE_URL", quoted("BASE_URL", "https://ferhatozcelik.com/"))
        buildConfigField("String", "YOUTUBE_API", quoted("YOUTUBE_API", "https://www.googleapis.com/youtube"))
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Networking
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp.logging)

    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Coroutines
    api(libs.kotlinx.coroutines.core)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.ferhatozcelik"
            artifactId = "wordpress-core"
            version = providers.gradleProperty("VERSION_NAME").getOrElse("1.0.0")

            afterEvaluate { from(components["release"]) }

            pom {
                name.set("wordpress-core")
                description.set("Core data, networking and repository layer for the ferhatozcelik WordPress Android app.")
                url.set("https://github.com/ferhatozcelik/wordpress-android")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("ferhatozcelik")
                        name.set("Ferhat Ozcelik")
                        url.set("https://github.com/ferhatozcelik")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ferhatozcelik/wordpress-android.git")
                    developerConnection.set("scm:git:ssh://github.com/ferhatozcelik/wordpress-android.git")
                    url.set("https://github.com/ferhatozcelik/wordpress-android")
                }
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
