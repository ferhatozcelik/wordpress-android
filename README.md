# WordPress Android App

A WordPress reader client for Android. The app pulls posts, categories and YouTube
videos from a WordPress REST API and a YouTube channel, then lets you browse, read and
bookmark them offline. Everything is stored locally with Room and synced with Retrofit.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Features

- Article feed with categories, search and infinite scrolling.
- Article detail with rendered HTML and images (Glide).
- YouTube videos feed and an embedded player, plus a live section.
- Bookmark (mark) articles and videos for offline access.
- Contact / social links screen driven by remote configuration.
- Light/dark theme, English and Turkish translations.
- Optional Firebase Analytics, Crashlytics and Performance.

## Screenshots

|<img src="screenshot/screenshot_1.jpg" width="200">|<img src="screenshot/screenshot_2.jpg" width="200">|<img src="screenshot/screenshot_3.jpg" width="200">|<img src="screenshot/screenshot_5.jpg" width="200">|
|:---:|:---:|:---:|:---:|

## Architecture

The data, Hilt and repository layers live in the `:wordpress-core` library module, while
`:app` contains the UI on top of it:

```
wordpress-core/                       # reusable library (:wordpress-core)
└── src/main/java/org/ferhatozcelik/
    ├── data/
    │   ├── entity/    # Room entities (Article, Video, Categories, Marked*)
    │   ├── local/     # Room database + DAOs + type converters
    │   ├── model/     # API response models
    │   └── remote/    # Retrofit AppApi
    ├── di/            # Hilt modules (ApiModule, AppModule, DatabaseModule)
    └── repository/    # ArticleRepository, VideoRepository

app/                                  # demo application (:app)
└── src/main/java/org/ferhatozcelik/
    ├── App.kt
    ├── interfaces/    # UI item click listener
    └── ui/
        ├── activitys/ # MainActivity + video player
        ├── adapters/  # RecyclerView adapters
        ├── dialogs/   # app info dialog
        ├── fragments/ # article, videos, marked, contact + view models
        └── util/      # date/network/html helpers
```

The project follows **MVVM**: fragments observe `LiveData` from `@HiltViewModel`
view models, which call repositories backed by Room and Retrofit.

`smoothbottombar` is a local library module that provides the animated bottom
navigation bar used by the main screen.

## Configuration

The app reads its configuration from `config/config.properties`. This file is **not**
committed (it may contain private keys); a ready-to-use template lives in
`config-example/config.properties`.

```bash
mkdir -p config
cp config-example/config.properties config/config.properties
```

When `config/config.properties` is missing the build automatically falls back to
`config-example/config.properties`, so a clean clone builds out of the box.

| Setting | Purpose |
| --- | --- |
| `APPLICATION_ID` | Application id (`com.ferhatozcelik.wordpress`). |
| `APPLICATION_NAME` / `APPLICATION_DESCRIPTION` | Shown on the contact screen. |
| `BASE_URL` | WordPress REST API base url (must end with `/`). |
| `YOUTUBE_API` / `YOUTUBE_API_KEY` / `CHANNEL_ID` / `PLAYLIST_ID` | YouTube feed configuration. |
| `*_STATUS` | Visibility of each social link (`0` visible, `1` invisible, `2` gone). |
| `storePassword` / `keyAlias` / `keyPassword` | Optional release signing. |

### Optional integrations

- **Firebase** — drop your `app/google-services.json` in place to enable Analytics,
  Crashlytics and Performance. The corresponding Gradle plugins are only applied when
  that file exists, so builds without it succeed.
- **Release signing** — place `config/keystore.jks` and set the `storePassword`,
  `keyAlias` and `keyPassword` properties. Without them the release build is produced
  unsigned instead of failing.

## Requirements

| Tool | Version |
| --- | --- |
| minSdk | 24 |
| compileSdk / targetSdk | 36 |
| Gradle | 8.14.5 |
| Android Gradle Plugin | 8.13.2 |
| Kotlin | 2.2.21 |
| Hilt | 2.57.2 |
| Room | 2.8.5 |
| JDK | 17 |

## Building

```bash
./gradlew :app:assembleDebug     # debug APK
./gradlew :app:assembleRelease   # release APK (signed only if configured)
./gradlew test                   # unit tests
```

## Library module

The data, networking and repository layers are packaged as the `:wordpress-core` Android
library (`com.ferhatozcelik:wordpress-core`). It reads the same `config/config.properties`
for its `BASE_URL` and `YOUTUBE_API` values. Publish it to your local Maven repository and
consume it from another project:

```bash
./gradlew :wordpress-core:publishToMavenLocal -PVERSION_NAME=1.0.0
```

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.ferhatozcelik:wordpress-core:1.0.0")
}
```

## License

Apache License 2.0 — see [LICENSE](LICENSE). If this project helped you, give it a ⭐️.
