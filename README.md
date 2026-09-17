# Flower Lite Launcher

Flower Lite Launcher is a lightweight Android launcher designed for a clean, minimal home screen and fast app discovery. It lists installed apps, supports search, and launches them directly from the home screen.

## Features

- Minimal launcher UI with a dark theme
- Search field to quickly find apps
- Grid layout of installed apps
- Direct app launching through the Android package manager
- Simple structure that is easy to customize and extend

## Project structure

- `app/` — Android application module
- `app/src/main/java/` — launcher code
- `app/src/main/res/` — layout, theme, and string resources
- `app/src/main/AndroidManifest.xml` — launcher configuration and intent filters
- `build.gradle` / `settings.gradle` — Gradle project configuration

## Android launcher configuration

This app is configured as a real launcher by declaring these main intent filters:

- `android.intent.action.MAIN`
- `android.intent.category.HOME`
- `android.intent.category.DEFAULT`
- `android.intent.category.LAUNCHER`

That allows it to be installed and used as the device home screen.

## Requirements

- Android Studio
- JDK 17+
- Android SDK with platform 34 or newer

## Getting started

1. Open the repository in Android Studio.
2. Let Gradle sync complete.
3. Select an emulator or connected Android device.
4. Run the app to install it.

## Notes

This is a starter launcher implementation intended for customization. You can extend it with:

- custom wallpapers and themes
- app folders or favorites
- weather widgets
- gestures and swipe actions
- app shortcuts and personalization features

## License

This project is provided as-is for learning and experimentation.
