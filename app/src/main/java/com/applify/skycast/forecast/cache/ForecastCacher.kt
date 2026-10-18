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

package com.applify.skycast.forecast.cache

import com.applify.skycast.common.util.getStringOrNull
import com.applify.skycast.forecast.Forecast
import com.applify.skycast.places.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class ForecastCacher(
    private val root: File,
    private val appVersionName: String,
) {
    private val coordsToData = mutableMapOf<Pair<Coordinates, String>, Forecast?>()

    suspend fun get(coords: Coordinates, source: String = OPEN_METEO_SOURCE): Forecast? {
        val cacheKey = coords to source
        val fromMemory = coordsToData[cacheKey]
        if (fromMemory != null) return fromMemory

        val fromFile = findForecastFile(coords, source)
            ?.let { fileToForecast(it) }
            ?: return null
        coordsToData[cacheKey] = fromFile

        return fromFile
    }

    suspend fun save(coords: Coordinates, data: Forecast, source: String = OPEN_METEO_SOURCE) {
        val file = findForecastFile(coords, source) ?: File(getDir(), "${coords.id}.$source")
        val jsonString = forecastToJsonString(data)
        withContext(Dispatchers.IO) {
            file.writeText(jsonString)
        }
        coordsToData[coords to source] = data
    }

    suspend fun delete(coords: Coordinates) {
        withContext(Dispatchers.IO) {
            getDir().listFiles()?.filter { it.name == coords.id || it.name.startsWith("${coords.id}.") }
                ?.forEach { it.delete() }
        }
        coordsToData.keys.removeAll { it.first == coords }
    }

    private suspend fun findForecastFile(coords: Coordinates, source: String): File? =
        withContext(Dispatchers.IO) {
            val directory = getDir()
            File(directory, "${coords.id}.$source").takeIf { it.exists() }
                ?: if (source == OPEN_METEO_SOURCE) File(directory, coords.id).takeIf { it.exists() } else null
        }

    private suspend fun fileToForecast(file: File): Forecast? {
        val json = JSONObject(
            withContext(Dispatchers.IO) {
                file.readText()
            }
        )
        return if (json.getStringOrNull(CacheJsonSerialNames.APP_VERSION_NAME) == null) {
            null
        } else {
            convertCacheJsonToForecast(json)
        }
    }

    private suspend fun forecastToJsonString(data: Forecast): String =
        withContext(Dispatchers.Default) {
            convertForecastToCacheJson(data).apply {
                put(CacheJsonSerialNames.APP_VERSION_NAME, appVersionName)
            }.toString()
        }

    private suspend fun getDir(): File =
        withContext(Dispatchers.IO) {
            File(root, "forecasts").apply {
                mkdir()
            }
        }

    companion object {
        private const val OPEN_METEO_SOURCE = "open-meteo"
    }
}
