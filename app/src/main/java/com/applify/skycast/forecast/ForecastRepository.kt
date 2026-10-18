/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast.
 *
 * Skycast is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Skycast is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skycast. If not, see <https://www.gnu.org/licenses/>.
 */

package com.applify.skycast.forecast

import com.applify.skycast.forecast.cache.ForecastCacher
import com.applify.skycast.forecast.download.ForecastDownloader
import com.applify.skycast.forecast.download.InternetChecker
import com.applify.skycast.forecast.download.WeatherApiDownloader
import com.applify.skycast.forecast.download.WeatherProvider
import com.applify.skycast.places.Coordinates
import com.applify.skycast.forecast.units.Units
import com.applify.skycast.places.Location
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import java.security.MessageDigest
import com.applify.skycast.settings.WeatherProviderRepository

class ForecastRepository(
    private val cacher: ForecastCacher,
    private val downloader: ForecastDownloader,
    private val internetChecker: InternetChecker,
    private val weatherProviderRepository: WeatherProviderRepository,
    private val weatherApiDownloader: WeatherApiDownloader
) {
    private val coordsToMutex = mutableMapOf<Coordinates, Mutex>()

    suspend fun get(
        location: Location,
        units: Units,
        updateFrequency: UpdateFrequency = UpdateFrequency.App
    ): Forecast? =
        coordsToMutex.getOrPut(location.coordinates, defaultValue = { Mutex() }).withLock {
            val provider = weatherProviderRepository.getProvider()
            val apiKey = if (provider == WeatherProvider.WeatherApi) weatherProviderRepository.getWeatherApiKey() else null
            val source = when (provider) {
                WeatherProvider.OpenMeteo -> "open-meteo"
                WeatherProvider.WeatherApi -> "weatherapi-${apiKey.orEmpty().sha256()}"
            }
            val cached = cacher.get(location.coordinates, source)
            if (shouldUpdate(cached, updateFrequency)) {
                val downloaded = when (provider) {
                    WeatherProvider.OpenMeteo -> downloader.get(location)
                    WeatherProvider.WeatherApi -> apiKey?.let { weatherApiDownloader.get(location, it) }
                }
                if (downloaded != null) {
                    cacher.save(location.coordinates, downloaded, source)
                    downloaded
                } else {
                    cached
                }
            } else {
                cached
            }
        }?.convertTo(units)

    private fun shouldUpdate(cached: Forecast?, updateFrequency: UpdateFrequency): Boolean {
        val expiresAfter = updateFrequency.expiresAfter ?: return false
        val shouldUpdate = cached == null
                || Duration.between(
                        cached.timestamp,
                        Instant.now()
                ) >= expiresAfter
        return shouldUpdate && internetChecker.hasInternet()
    }
}

private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
    .digest(toByteArray(Charsets.UTF_8))
    .take(8)
    .joinToString("") { "%02x".format(it) }

enum class UpdateFrequency(val expiresAfter: Duration?) {
    App(expiresAfter = Duration.ofHours(1)),
    Never(expiresAfter = null)
}
