# Skycast 🌦️

Modern weather app with graphs and thoughtful data visualization for Android. Spiritual successor to [Prognoza](https://github.com/davidtakac/prognoza).

<div align="center">
    <a href="https://fdroid.link/#https://saurabhkundu1.github.io/Applify/repo?fingerprint=DE62E0D386DB9B6FC616ADF6573F458AAC8D04F873007C73B8585FBEF0960255">
        <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="70" alt="Get it on F-Droid"/>
    </a>
    <a href="https://saurabhkundu1.github.io/Applify/">
        <img src="assets/badge_izzyondroid.png" height="70" alt="IzzyOnDroid"/>
    </a>
    <a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/saurabhkundu1/skycast-weather">
        <img src="assets/badge_obtainium.png" height="70" alt="Get it on Obtainium"/>
    </a>
    <a href="https://github.com/saurabhkundu1/skycast-weather/releases/latest">
        <img src="assets/badge_github.png" height="70" alt="GitHub Releases"/>
    </a>
</div>

<p align="center">
    <a href="https://github.com/saurabhkundu1/skycast-weather/actions/workflows/build_and_upload.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/saurabhkundu1/skycast-weather/build_and_upload.yml?style=flat-square&label=Build" alt="Build Status"/>
    </a>
    <a href="https://github.com/saurabhkundu1/skycast-weather/releases/latest">
        <img src="https://img.shields.io/github/v/release/saurabhkundu1/skycast-weather?style=flat-square&label=Latest%20Release" alt="Latest Release"/>
    </a>
    <a href="LICENSE.txt">
        <img src="https://img.shields.io/github/license/saurabhkundu1/skycast-weather?style=flat-square&color=blue" alt="License: GPL-3.0"/>
    </a>
    <a href="https://hosted.weblate.org/projects/bura/">
        <img src="https://img.shields.io/weblate/status/bura?style=flat-square&label=Translations" alt="Weblate Translation Status"/>
    </a>
</p>

---

## 🌟 Overview

**Skycast** transforms and visualizes weather data from [Open-Meteo.com](https://open-meteo.com) to provide essential weather information at a glance, while allowing you to dive deeper through elegant interactive graphs and rich metrics.

Built with a privacy-first mindset, Skycast delivers an intuitive, fast, and lightweight weather experience without tracking, ads, or requiring unnecessary device permissions.

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

- 🔒 **Privacy-First Design**: Does not access your device location (searches locations via Open-Meteo geocoding API).
- 🔑 **No API Key Required**: Ready to use right out of the box with zero setup.
- 📴 **Offline Support**: Caches weather data locally for offline viewing and instant startup.
- 📡 **Data Efficient**: Minimal network usage, optimized data fetching and bandwidth saving.
- 🎨 **Material You / Material Design 3**: Modern interface supporting Dynamic Color (Android 12+), Light, and Dark themes.
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
- 📐 **Customizable Units**: Choose preferred units for temperature, wind speed, pressure, precipitation, and visibility.
- 🌐 **Multilingual Support**: Available in 15+ languages including English, Spanish, French, German, Russian, Vietnamese, Croatian, Chinese (Simplified & Traditional), Polish, Swedish, Dutch, Arabic, Czech, and Greek.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Navigation**: Navigation Compose
- **Architecture**: Unidirectional Data Flow (UDF) with `ViewModel`, `StateFlow`, and Kotlin Coroutines
- **Build Tool**: Gradle (Kotlin DSL) with Android Gradle Plugin 9.x & JDK 21
- **Data Source**: [Open-Meteo API](https://open-meteo.com/) (Forecast & Geocoding APIs)
- **CI/CD**: GitHub Actions for automated build, signing, and artifact publication

---

## 🗺️ Roadmap

### Planned / In Progress
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
   - [ ] Home screen tiles
   - [ ] Air quality graphs
3. **Weather Alerts**
4. **Home Screen Widgets**
5. **Notification Widgets**
6. **Customizable Open-Meteo Data Sources**

### Non-Goals (Will Not Be Implemented)
To keep Skycast focused and lean, issues requesting these features will be closed as *not planned*:
- Manual refresh button
- Customizable refresh interval
- Alternative weather API providers
- OLED pure-black dark theme

---

## 🏗️ Building from Source

### Prerequisites
- [Android Studio Ladybug (or newer)](https://developer.android.com/studio)
- JDK 21
- Android SDK 35+

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/saurabhkundu1/skycast-weather.git
   cd skycast-weather
   ```

2. **Build debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on connected device:**
   ```bash
   ./gradlew installDebug
   ```

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
Skycast is free software licensed under the **GNU General Public License v3.0**. See the [LICENSE.txt](LICENSE.txt) file for details.

[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)
