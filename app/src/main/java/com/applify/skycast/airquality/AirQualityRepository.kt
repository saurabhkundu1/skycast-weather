/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast and is licensed under the GNU General Public License v3.0 or later.
 */

package com.applify.skycast.airquality

import android.content.SharedPreferences
import androidx.core.content.edit
import com.applify.skycast.forecast.download.InternetChecker
import com.applify.skycast.places.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import kotlin.math.roundToInt

data class AirQualityReading(
    val europeanAqi: Int,
    val pm25: Double,
    val fetchedAt: Long = 0L,
    val isStale: Boolean = false,
)

class AirQualityRepository(
    private val preferences: SharedPreferences,
    private val internetChecker: InternetChecker,
    private val userAgent: String,
) {
    private val mutex = Mutex()

    suspend fun get(location: Location): AirQualityReading? = mutex.withLock {
        val caches = readCaches()
        val cached = caches[location.coordinates.id]
        val now = System.currentTimeMillis()
        if (cached != null && now >= cached.fetchedAt && now - cached.fetchedAt < CACHE_LIFETIME_MS) return@withLock cached
        val stale = staleAirQualityCache(cached, now)
        if (!internetChecker.hasInternet()) return@withLock stale

        val reading = download(location)?.copy(isStale = false) ?: return@withLock stale
        writeCaches(caches + (location.coordinates.id to reading))
        reading
    }

    private fun readCaches(): Map<String, AirQualityReading> {
        val raw = preferences.getString(CACHE_KEY, null) ?: return emptyMap()
        return decodeAirQualityCaches(raw)
    }

    private fun readingFromJson(json: JSONObject) = AirQualityReading(
        europeanAqi = json.getInt("europeanAqi"),
        pm25 = json.getDouble("pm25"),
        fetchedAt = json.getLong("fetchedAt"),
    )

    private fun writeCaches(caches: Map<String, AirQualityReading>) {
        preferences.edit {
            putString(CACHE_KEY, encodeAirQualityCaches(caches))
        }
    }

    private suspend fun download(location: Location): AirQualityReading? = withContext(Dispatchers.IO) {
        val query = "https://air-quality-api.open-meteo.com/v1/air-quality" +
                "?latitude=${formatCoordinate(location.coordinates.latitude)}" +
                "&longitude=${formatCoordinate(location.coordinates.longitude)}" +
                "&current=european_aqi,pm2_5" +
                "&timezone=${URLEncoder.encode(location.timeZone.id, Charsets.UTF_8.name())}" +
                "&timeformat=unixtime"
        val connection = try {
            URL(query).openConnection() as HttpsURLConnection
        } catch (_: Exception) {
            return@withContext null
        }
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("User-Agent", userAgent)
            if (connection.responseCode != 200) return@withContext null
            val response = BufferedReader(InputStreamReader(connection.inputStream, Charsets.UTF_8)).use {
                JSONObject(it.readText())
            }
            val current = response.optJSONObject("current") ?: return@withContext null
            val aqi = current.optDouble("european_aqi", Double.NaN)
            val pm25 = current.optDouble("pm2_5", Double.NaN)
            if (!aqi.isFinite() || aqi < 0 || !pm25.isFinite() || pm25 < 0) return@withContext null
            AirQualityReading(europeanAqi = aqi.roundToInt(), pm25 = pm25, fetchedAt = System.currentTimeMillis())
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun formatCoordinate(value: Double) = String.format(Locale.ROOT, "%.2f", value)

    companion object {
        private const val CACHE_KEY = "skycast_air_quality"
        private const val CACHE_LIFETIME_MS = 60 * 60 * 1000L
    }
}

internal fun staleAirQualityCache(reading: AirQualityReading?, now: Long): AirQualityReading? = reading
    ?.takeIf { now >= it.fetchedAt && now - it.fetchedAt <= 24 * 60 * 60 * 1000L }
    ?.copy(isStale = true)

internal fun decodeAirQualityCaches(raw: String): Map<String, AirQualityReading> = runCatching {
    val json = JSONObject(raw)
    fun reading(value: JSONObject) = AirQualityReading(
        europeanAqi = value.getInt("europeanAqi"),
        pm25 = value.getDouble("pm25"),
        fetchedAt = value.getLong("fetchedAt"),
    )
    if (json.has("location")) mapOf(json.getString("location") to reading(json))
    else json.keys().asSequence().mapNotNull { id -> runCatching { id to reading(json.getJSONObject(id)) }.getOrNull() }.toMap()
}.getOrDefault(emptyMap())

internal fun encodeAirQualityCaches(caches: Map<String, AirQualityReading>): String {
    val json = JSONObject()
    caches.entries.sortedByDescending { it.value.fetchedAt }.take(5).forEach { (id, reading) ->
        json.put(id, JSONObject()
            .put("fetchedAt", reading.fetchedAt)
            .put("europeanAqi", reading.europeanAqi)
            .put("pm25", reading.pm25))
    }
    return json.toString()
}
