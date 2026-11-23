# Skycast 🌦️

Modern weather application with interactive graphs, air quality monitoring, and thoughtful data visualization for Android. Spiritual successor to [Prognoza](https://github.com/davidtakac/prognoza).

<div align="center">
    <a href="https://saurabhkundu1.github.io/Applify/">
        <img src="assets/badge_izzyondroid.png" height="70" alt="Applify Repository"/>
    </a>
    <a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/saurabhkundu1/skycast-weather">
        <img src="assets/badge_obtainium.png" height="70" alt="Get it on Obtainium"/>
    </a>
    <a href="https://github.com/saurabhkundu1/skycast-weather/releases/latest">
        <img src="assets/badge_github.png" height="70" alt="GitHub Releases"/>
    </a>
</div>

<p align="center">
    <a href="https://github.com/saurabhkundu1/skycast-weather/actions/workflows/android-ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/saurabhkundu1/skycast-weather/android-ci.yml?style=flat-square&label=Android%20CI" alt="Android CI Status"/>
    </a>
    <a href="https://developer.android.com">
        <img src="https://img.shields.io/badge/Platform-Android%209.0%2B%20(API%2028%2B)-green.svg?style=flat-square" alt="Platform"/>
    </a>
    <a href="https://developer.android.com">
        <img src="https://img.shields.io/badge/Compile%20SDK-37-brightgreen.svg?style=flat-square" alt="Compile SDK 37"/>
    </a>
    <a href="https://developer.android.com">
        <img src="https://img.shields.io/badge/Target%20SDK-37-brightgreen.svg?style=flat-square" alt="Target SDK 37"/>
    </a>
    <a href="LICENSE">
        <img src="https://img.shields.io/badge/License-GPL--3.0-blue.svg?style=flat-square" alt="License: GPL-3.0"/>
    </a>
    <a href="https://hosted.weblate.org/projects/bura/">
        <img src="https://img.shields.io/weblate/status/bura?style=flat-square&label=Translations" alt="Weblate Translation Status"/>
    </a>
</p>

---

## 🌟 Overview

**Skycast** transforms and visualizes weather data from [Open-Meteo.com](https://open-meteo.com) to provide essential weather information at a glance, while allowing you to dive deeper through elegant interactive graphs, air quality diagnostics, and rich meteorological metrics.

Built with a privacy-first mindset, Skycast delivers an intuitive, fast, and lightweight weather experience without tracking, ads, or requiring unnecessary device permissions.

> **Signing-key migration:** The previous app-signing key was exposed in public Git history and must not sign new releases. The replacement keeps the package ID `com.applify.skycast` and uses a new signer. Remove the old Skycast install before installing the replacement from Applify; saved places and preferences may need to be configured again.

---

## 📱 Screenshots

<p align="center">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="31%" alt="Overview Screen"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="31%" alt="Detailed Metrics"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="31%" alt="Weather Graphs"/>
</p>

<p align="center">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width="31%" alt="Graphs Page"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" width="31%" alt="Dark Theme"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" width="31%" alt="Settings Screen"/>
</p>

---

## ✨ Features

- 🔒 **Privacy-First Design**: Location search works through Open-Meteo geocoding. Optional coarse device location can select your current place; its coordinates go to Open-Meteo for time-zone and air-quality data, and to the selected weather provider for forecasts.
- 🔑 **No API Key Required**: Ready to use right out of the box with zero setup.
- 📴 **Offline Support**: Caches weather data locally for offline viewing and instant startup.
- 📡 **Data Efficient**: Minimal network usage, optimized data fetching and bandwidth saving.
- 🎨 **Material You / Material Design 3**: Modern interface supporting Dynamic Color (Android 12+), Light, and Dark themes with frosted glassmorphic UI elements.
- 📊 **Rich Weather Graphs**:
  - Temperature trends with "Feels like" comparison and day/night transitions.
  - Probability of Precipitation (PoP) graphs.
  - Precipitation amounts and totals.
- 🌡️ **Detailed Weather Metrics**:
  - Current & Hourly forecasts (with sun position tracking).
  - 7-day daily forecast with intuitive colored temperature range bars.
  - Feels-like temperature calculations.
  - Precipitation intensity, accumulative totals, and probability.
  - Wind speed, direction, and gust tracking.
  - UV Index levels with protection advisories.
  - Humidity levels & Dew point calculations.
  - Atmospheric pressure and pressure tendencies.
  - Visibility distances.
  - Sunrise, sunset, and daylight duration.
- 💨 **Air Quality Monitoring**: View European AQI and PM2.5 model estimates for the selected place, credited to CAMS ENSEMBLE via Open-Meteo. Includes graceful 24-hour caching for offline access.
- 📐 **Customizable Units**: Choose preferred units for temperature, wind speed, pressure, precipitation, and visibility.
- 🌐 **Multilingual Support**: Available in 15+ languages including English, Spanish, French, German, Russian, Vietnamese, Croatian, Chinese (Simplified & Traditional), Polish, Swedish, Dutch, Arabic, Czech, and Greek.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **Target SDK**: 37 (Android 15+)
- **Compile SDK**: 37
- **Minimum SDK**: 28 (Android 9.0 Pie)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Navigation**: Navigation Compose
- **Architecture**: Unidirectional Data Flow (UDF) with `ViewModel`, `StateFlow`, and Kotlin Coroutines
- **Build Tool**: Gradle (Kotlin DSL) with Android Gradle Plugin 8.x/9.x & JDK 21
- **Data Source**: [Open-Meteo APIs](https://open-meteo.com/) (Forecast, Air Quality & Geocoding), with optional WeatherAPI forecasts
- **CI/CD**: GitHub Actions (`android-ci.yml` and `build_and_upload.yml`) for continuous testing, strict zero-warning lint checks, and automated artifact publication

---

## 🗺️ Roadmap

### Completed & In Progress
1. **Graphs**
   - [x] Precipitation
   - [x] Temperature
   - [x] Probability of Precipitation (PoP)
   - [ ] UV Index
   - [ ] Wind
   - [ ] Pressure
   - [ ] Humidity
   - [ ] Visibility
   - [ ] Feels like
2. **Air Quality**
   - [x] Home screen tiles & European AQI model
   - [x] PM2.5 monitoring
   - [ ] Air quality historical graphs
3. **Weather Alerts**
4. **Home Screen Widgets** [x]
5. **Notification Widgets**
6. **Customizable Open-Meteo Data Sources**

### Non-Goals (Out of Scope)
To keep Skycast focused and lean, issues requesting these features will be closed as *not planned*:
- Manual refresh button
- Customizable refresh interval
- Proprietary closed-source weather APIs
- Pure-black OLED dark themes

---

## 🏗️ Building from Source

### Prerequisites
- [Android Studio Ladybug (or newer)](https://developer.android.com/studio)
- JDK 21 (or JDK 17+)
- Android SDK 37 with Build Tools installed

### Command-Line Build

```powershell
# Assemble debug APK
.\gradlew.bat assembleDebug

# Run unit tests
.\gradlew.bat testDebugUnitTest

# Run strict Android lint checks
.\gradlew.bat lintDebug
```

On Linux or macOS:
```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🌍 Translations

Translations are managed via Weblate. Contributions are always welcome!

<a href="https://hosted.weblate.org/projects/bura/">
    <img src="https://hosted.weblate.org/widgets/bura/-/multi-auto.svg" alt="Translation status"/>
</a>

Help translate Skycast into your language on [Weblate](https://hosted.weblate.org/projects/bura/).

---

## 🤝 Contributions

Contributions are welcome! Before starting work on a non-trivial change or new feature, please open an issue to discuss the proposed solution.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## ❓ FAQ

### What does the colored temperature bar mean?
The extremes of the outer background spectrum represent the full temperature range of the entire week. The inner colored bar shows the min-to-max temperature range for that specific day, and the white dot indicates the current temperature.

### I can't find my location!
The Open-Meteo geocoding API works best with clean, single-word search terms (e.g., `Berlin` or `Tokyo`). Avoid extra punctuation, postal codes, or multi-word region qualifiers in the search field.

---

## 📜 Credits & License

- **Forecast & Location Data**: Provided by [Open-Meteo](https://open-meteo.com/) (Licensed under [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/))
- **Icons**: Sun, moon, sunrise, and sunset icons adapted from [Feather Icons](https://feathericons.com/) (Licensed under [MIT License](https://github.com/feathericons/feather/blob/main/LICENSE))

### License
Skycast is free software licensed under the **[GNU General Public License v3.0](LICENSE)**. See the [LICENSE](LICENSE) file for the full license text.

[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)
