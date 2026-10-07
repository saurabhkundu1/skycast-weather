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

package com.applify.skycast.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.applify.skycast.App
import com.applify.skycast.common.util.launchCatching
import com.applify.skycast.forecast.ForecastRepository
import com.applify.skycast.forecast.units.SelectedUnitsRepository
import com.applify.skycast.places.selected.SelectedPlaceRepository
import com.applify.skycast.summary.daily.DailySummary
import com.applify.skycast.summary.daily.getDailySummary
import com.applify.skycast.summary.feelslike.FeelsLikeSummary
import com.applify.skycast.summary.feelslike.getFeelsLikeSummary
import com.applify.skycast.summary.hourly.HourSummary
import com.applify.skycast.summary.hourly.getHourlySummary
import com.applify.skycast.summary.humidity.HumiditySummary
import com.applify.skycast.summary.humidity.getHumiditySummary
import com.applify.skycast.summary.now.NowSummary
import com.applify.skycast.summary.now.getNowSummary
import com.applify.skycast.summary.precipitation.PrecipitationSummary
import com.applify.skycast.summary.precipitation.getPrecipitationSummary
import com.applify.skycast.summary.pressure.PressureSummary
import com.applify.skycast.summary.pressure.getPressureSummary
import com.applify.skycast.summary.sun.SunSummary
import com.applify.skycast.summary.sun.getSunSummary
import com.applify.skycast.summary.uvindex.UvIndexSummary
import com.applify.skycast.summary.uvindex.getUvIndexSummary
import com.applify.skycast.summary.visibility.VisibilitySummary
import com.applify.skycast.summary.visibility.getVisibilitySummary
import com.applify.skycast.summary.wind.WindSummary
import com.applify.skycast.summary.wind.getWindSummary
import com.applify.skycast.unexpectederror.UnexpectedErrorSetter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant

class SummaryViewModel(
    private val placeRepo: SelectedPlaceRepository,
    private val unitsRepo: SelectedUnitsRepository,
    private val forecastRepo: ForecastRepository,
    private val unexpectedErrorSetter: UnexpectedErrorSetter
) : ViewModel() {
    private val _state = MutableStateFlow<SummaryState>(SummaryState.Initial)
    val state = _state.asStateFlow()

    fun getSummary() {
        viewModelScope.launchCatching(unexpectedErrorSetter) {
            if (_state.value == SummaryState.Loading) {
                return@launchCatching
            }
            if (_state.value !is SummaryState.Success) {
                _state.value = SummaryState.Loading
            }
            _state.value = getState()
        }
    }
    
    private suspend fun getState(): SummaryState {
        val location = placeRepo.getSelectedPlace()?.location ?: return SummaryState.NoSelectedPlace
        val coords = location.coordinates
        val units = unitsRepo.getSelectedUnits()
        val now = Instant.now().atZone(location.timeZone)
        val forecast = forecastRepo.get(location, units) ?: return SummaryState.FailedToDownload

        val nowSummary = getNowSummary(
            now = now,
            tempPeriod = forecast.temperature,
            feelsPeriod = forecast.feelsLike,
            condPeriod = forecast.condition
        ) ?: return SummaryState.Outdated

        val hourlySummary = getHourlySummary(
            now = now,
            tempPeriod = forecast.temperature,
            popPeriod = forecast.pop,
            condPeriod = forecast.condition,
            sunPeriod = forecast.sun
        ) ?: return SummaryState.Outdated

        val dailySummary = getDailySummary(
            now = now,
            tempPeriod = forecast.temperature,
            condPeriod = forecast.condition,
            popPeriod = forecast.pop
        ) ?: return SummaryState.Outdated

        val precipSummary = getPrecipitationSummary(
            now = now,
            precipPeriod = forecast.precipitation
        ) ?: return SummaryState.Outdated

        val uvIndexSummary = getUvIndexSummary(
            now = now,
            uvIndexPeriod = forecast.uvIndex
        ) ?: return SummaryState.Outdated

        val windSummary = getWindSummary(
            now = now,
            windPeriod = forecast.wind,
            gustPeriod = forecast.gust
        ) ?: return SummaryState.Outdated

        val pressureSummary = getPressureSummary(
            now = now,
            pressurePeriod = forecast.pressure
        ) ?: return SummaryState.Outdated

        val humiditySummary = getHumiditySummary(
            now = now,
            humidityPeriod = forecast.humidity,
            dewPointPeriod = forecast.dewPoint
        ) ?: return SummaryState.Outdated

        val visSummary = getVisibilitySummary(
            now = now,
            visPeriod = forecast.visibility
        ) ?: return SummaryState.Outdated

        val sunSummary = getSunSummary(
            now = now,
            sunPeriod = forecast.sun,
            condPeriod = forecast.condition
        ) ?: return SummaryState.Outdated

        val feelsLikeSummary = getFeelsLikeSummary(
            now = now,
            tempPeriod = forecast.temperature,
            feelsPeriod = forecast.feelsLike
        ) ?: return SummaryState.Outdated

        return SummaryState.Success(
            now = nowSummary,
            hourly = hourlySummary,
            daily = dailySummary,
            precip = precipSummary,
            uvIndex = uvIndexSummary,
            wind = windSummary,
            pressure = pressureSummary,
            humidity = humiditySummary,
            vis = visSummary,
            sun = sunSummary,
            feelsLike = feelsLikeSummary,
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val container = (checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as App).container
                return SummaryViewModel(
                    container.selectedPlaceRepo,
                    container.selectedUnitsRepo,
                    container.forecastRepo,
                    container.unexpectedErrorSetter
                ) as T
            }
        }
    }
}

sealed interface SummaryState {
    data class Success(
        val now: NowSummary,
        val hourly: List<HourSummary>,
        val daily: DailySummary,
        val precip: PrecipitationSummary,
        val uvIndex: UvIndexSummary,
        val wind: WindSummary,
        val pressure: PressureSummary,
        val humidity: HumiditySummary,
        val vis: VisibilitySummary,
        val sun: SunSummary,
        val feelsLike: FeelsLikeSummary
    ) : SummaryState

    data object Loading : SummaryState
    data object FailedToDownload : SummaryState
    data object Outdated : SummaryState
    data object NoSelectedPlace : SummaryState
    data object Initial : SummaryState
}