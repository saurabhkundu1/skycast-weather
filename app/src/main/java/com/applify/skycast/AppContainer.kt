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

package com.applify.skycast

import android.content.Context
import android.content.SharedPreferences
import com.applify.skycast.common.util.getAppVersionName
import com.applify.skycast.common.util.getUserAgent
import com.applify.skycast.forecast.ForecastRepository
import com.applify.skycast.forecast.cache.ForecastCacher
import com.applify.skycast.forecast.download.ForecastDownloader
import com.applify.skycast.forecast.download.InternetChecker
import com.applify.skycast.forecast.units.SelectedUnitsRepository
import com.applify.skycast.places.saved.DeletePlace
import com.applify.skycast.places.saved.GetSavedPlaces
import com.applify.skycast.places.saved.SavedPlacesRepository
import com.applify.skycast.places.search.SearchPlaces
import com.applify.skycast.places.selected.SelectPlace
import com.applify.skycast.places.selected.SelectedPlaceRepository
import com.applify.skycast.unexpectederror.UnexpectedErrorConsumer
import com.applify.skycast.unexpectederror.UnexpectedErrorRepository
import com.applify.skycast.unexpectederror.UnexpectedErrorSetter

class AppContainer(private val appContext: Context) {
    val prefs: SharedPreferences get() = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val root get() = appContext.filesDir
    private val userAgent: String get() = getUserAgent(appContext)

    private val forecastCacher by lazy {
        ForecastCacher(
            root = root,
            appVersionName = getAppVersionName(appContext),
        )
    }

    private val unexpectedErrorRepository by lazy { UnexpectedErrorRepository() }
    val unexpectedErrorSetter: UnexpectedErrorSetter = unexpectedErrorRepository
    val unexpectedErrorConsumer: UnexpectedErrorConsumer = unexpectedErrorRepository

    val forecastRepo by lazy {
        ForecastRepository(
            cacher = forecastCacher,
            downloader = ForecastDownloader(userAgent),
            internetChecker = InternetChecker(appContext)
        )
    }

    val selectedPlaceRepo by lazy { SelectedPlaceRepository(prefs, savedPlacesRepo) }
    val selectedUnitsRepo by lazy { SelectedUnitsRepository(prefs) }

    private val savedPlacesRepo by lazy { SavedPlacesRepository(root) }
    val getSavedPlaces get() = GetSavedPlaces(selectedUnitsRepo, selectedPlaceRepo, savedPlacesRepo, forecastRepo)
    val searchPlaces get() = SearchPlaces(userAgent)
    val selectPlace get() = SelectPlace(selectedPlaceRepo, savedPlacesRepo)
    val deletePlace get() = DeletePlace(savedPlacesRepo, forecastCacher)
}