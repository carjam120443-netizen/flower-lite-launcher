# Flower Lite Launcher

Flower Lite Launcher is a premium-feeling Android launcher starter designed for a clean home screen, quick app access, and a polished everyday experience. It includes app search, smart groupings, wallpaper previews, a widget card, and swipe-based launcher modes.

## Features

- Premium dark-mode home screen
- Search field for installed apps
- Grid of app tiles with custom styling
- Favorites and productivity modes
- Wallpaper preview panel with quick theme switching
- Widget section for weather and focus summaries
- Swipe gestures to move between launcher views
- Built to behave like a full Android launcher via intent filters

## Project structure

- `app/` — Android application module
- `app/src/main/java/` — launcher activity and app-grid logic
- `app/src/main/res/` — layouts, drawables, and theme resources
- `app/src/main/AndroidManifest.xml` — launcher configuration
- `.github/workflows/android-build.yml` — GitHub Actions APK build flow
- `build.gradle` / `settings.gradle` — Gradle project setup

## Android launcher configuration

The app is configured as a real launcher with:

- `android.intent.action.MAIN`
- `android.intent.category.HOME`
- `android.intent.category.DEFAULT`
- `android.intent.category.LAUNCHER`

## Requirements

- Android Studio
- JDK 17+
- Android SDK with platform 34 or newer

## Getting started

1. Open the repo in Android Studio.
2. Let Gradle sync.
3. Add your Android SDK path in `local.properties` if needed.
4. Run the app on an emulator or physical device.

## APK action

The GitHub workflow builds a debug APK automatically through GitHub Actions:

- `.github/workflows/android-build.yml`

This action will compile the app and upload the generated APK as a workflow artifact.

## Notes

This project is designed as a launcher starter and can be extended with:

- custom app folder logic
- wallpaper selection persistence
- weather or clock widgets
- icon pack support
- drag-and-drop app organization
- keyboard shortcuts and gestures

## License

This project is provided as-is for learning, experimentation, and launcher customization.
