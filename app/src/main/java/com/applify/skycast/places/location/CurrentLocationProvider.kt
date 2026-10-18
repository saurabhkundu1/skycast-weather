/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast.
 *
 * Skycast is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.applify.skycast.places.location

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import androidx.core.content.ContextCompat
import com.applify.skycast.R
import com.applify.skycast.places.Coordinates
import com.applify.skycast.places.Location as PlaceLocation
import com.applify.skycast.places.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.URL
import java.time.ZoneId
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume

class CurrentLocationProvider(
    private val context: Context,
    private val userAgent: String,
) {
    suspend fun getCurrentPlace(): Place? = withContext(Dispatchers.IO) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return@withContext null
        }

        val manager = context.getSystemService(LocationManager::class.java)
        val provider = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .firstOrNull { name -> runCatching { manager.isProviderEnabled(name) }.getOrDefault(false) }
            ?: return@withContext null

        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getCurrentLocation(manager, provider)
        } else {
            getSingleUpdate(manager, provider)
        } ?: return@withContext null

        val address = getAddress(current)
        val coordinates = Coordinates(current.latitude, current.longitude)
        val name = address?.locality
            ?: address?.subAdminArea
            ?: address?.adminArea
            ?: context.getString(R.string.place_picker_current_location)

        Place(
            name = name,
            admin1 = address?.adminArea,
            admin2 = address?.subAdminArea,
            admin3 = null,
            admin4 = null,
            countryCode = address?.countryCode,
            countryName = address?.countryName,
            location = PlaceLocation(
                timeZone = getTimeZone(coordinates) ?: ZoneId.systemDefault(),
                coordinates = coordinates,
            ),
        )
    }

    private suspend fun getCurrentLocation(
        manager: LocationManager,
        provider: String,
    ): Location? = withTimeoutOrNull(20_000) {
        suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationSignal()
            continuation.invokeOnCancellation { cancellation.cancel() }
            manager.getCurrentLocation(provider, cancellation, context.mainExecutor) { location ->
                if (continuation.isActive) continuation.resume(location)
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getSingleUpdate(
        manager: LocationManager,
        provider: String,
    ): Location? = withTimeoutOrNull(20_000) {
        suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (continuation.isActive) continuation.resume(location)
                    manager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) = Unit
                override fun onProviderEnabled(provider: String) = Unit
                override fun onProviderDisabled(provider: String) = Unit
            }
            continuation.invokeOnCancellation { manager.removeUpdates(listener) }
            manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun getAddress(location: Location): Address? = withContext(Dispatchers.IO) {
        runCatching {
            if (!Geocoder.isPresent()) return@runCatching null
            Geocoder(context, Locale.getDefault())
                .getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()
        }.getOrNull()
    }

    private fun getTimeZone(coordinates: Coordinates): ZoneId? {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast" +
                "?latitude=${coordinates.latitude}" +
                "&longitude=${coordinates.longitude}" +
                "&current=temperature_2m" +
                "&timezone=auto",
        )
        val connection = runCatching { url.openConnection() as HttpsURLConnection }.getOrNull()
            ?: return null
        return try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("User-Agent", userAgent)
            if (connection.responseCode != HttpsURLConnection.HTTP_OK) return null
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            ZoneId.of(JSONObject(response).getString("timezone"))
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
