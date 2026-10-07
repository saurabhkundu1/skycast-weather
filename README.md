# Skycast
Modern weather app with graphs and thoughtful data visualization. Spiritual successor to [Prognoza](https://github.com/davidtakac/prognoza).

<div>
    <a href="https://fdroid.link/#https://saurabhkundu1.github.io/Applify/repo?fingerprint=DE62E0D386DB9B6FC616ADF6573F458AAC8D04F873007C73B8585FBEF0960255">
        <img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="80" align="center"/>
    </a>
    <a href="https://saurabhkundu1.github.io/Applify/">
        <img src="assets/badge_izzyondroid.png" height="80" align="center"/>
    </a>
    <a href="https://apps.obtainium.imranr.dev/redirect.html?r=obtainium://add/https://github.com/saurabhkundu1/skycast-weather">
        <img src="assets/badge_obtainium.png" height="80" align="center"/>
    </a>
    <a href="https://github.com/saurabhkundu1/skycast-weather/releases/latest">
        <img src="assets/badge_github.png" height="80" align="center"/>
    </a>
</div>

## Screenshots
<p align="left">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.png" width="30%"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="30%"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.png" width="30%"/>
</p>

<p align="left">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/4.png" width="30%"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/5.png" width="30%"/>
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/6.png" width="30%"/>
</p>

## Features
Skycast transforms and visualizes weather data from Open-Meteo.com to give you essential weather information at a glance, while allowing you to dive deeper with graphs.

Other features include:
- Works offline
- Uses mobile data sparingly
- Does not require an API key
- Does not access your location
- Material Design 3 / Material You
- Dark and light theme
- Customizable measurement units

## Will be implemented
Sorted by priority:

1. Graphs
   1. ~~Precipitation~~
   2. UV index
   3. Wind
   4. Pressure
   5. Humidity
   6. Visibility
   7. Feels like
2. Air quality
   1. Home screen tiles
   2. Graphs
3. Weather alerts
4. Home screen widgets
5. Notification widgets
6. Customizable [Open-Meteo data sources](https://open-meteo.com/en/docs#data-sources)

## Will not be implemented
Issues requesting these features will be closed as not planned:

- Manual refresh
- Customizable refresh period
- Other weather sources
- OLED dark theme

## Translations
To contribute translations, use [Weblate](https://hosted.weblate.org/projects/bura/).

## Contributions
Before you start working on a contribution, please open an issue (if one is not already opened) and
describe the problem and your proposed solution. After we agree, you may open a PR. That way you 
won't waste time working on something that may not be merged if it conflicts with the project.

## Donations
I do not accept donations at the moment.

## FAQ

### Meaning of the colored temperature bar?
The extreme ends of the spectrum are the full temperature range of the week. The colored bar is the temperature range for that day, and the dot is the current temperature.

### I can't find my location!
The Open-Meteo geocoding API is a bit particular. I've found it is often better to enter a simple, 
single-word, generic search term like "Berlin" and then choose from the list of results. Entering
more complicated queries with multiple words, commas, etc. confuses the API.

## Credit
- Forecast data by [Open-Meteo](https://open-meteo.com/) licensed under [Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)  
- Location data by [Open-Meteo](https://open-meteo.com/) licensed under [Attribution 4.0 International (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/)  
- Sun, moon, sunrise and sunset icons adapted from [Feather icons](https://feathericons.com/) licensed under the [MIT License](https://github.com/feathericons/feather/blob/main/LICENSE)  

## License
[![GNU GPLv3 Image](https://www.gnu.org/graphics/gplv3-127x51.png)](https://www.gnu.org/licenses/gpl-3.0.en.html)  
Skycast is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
