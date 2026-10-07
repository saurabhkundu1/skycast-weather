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

package com.applify.skycast.places.saved

import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.ForecastRepository
import com.applify.skycast.forecast.UpdateFrequency
import com.applify.skycast.places.Place
import com.applify.skycast.places.selected.SelectedPlaceRepository
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import com.applify.skycast.forecast.units.SelectedUnitsRepository
import java.time.Instant
import java.time.ZonedDateTime

class GetSavedPlaces(
    private val selectedUnitsRepo: SelectedUnitsRepository,
    private val selectedPlaceRepo: SelectedPlaceRepository,
    private val savedPlacesRepo: SavedPlacesRepository,
    private val forecastRepo: ForecastRepository,
) {
    suspend operator fun invoke(now: Instant): List<SavedPlace> {
        val selectedUnits = selectedUnitsRepo.getSelectedUnits()
        val selectedPlace = selectedPlaceRepo.getSelectedPlace()
        return savedPlacesRepo.getSavedPlaces().map { place ->
            val forecast = forecastRepo.get(
                location = place.location,
                units = selectedUnits,
                updateFrequency = UpdateFrequency.Never
            )
            getSavedPlace(
                now = now,
                place = place,
                selected = place == selectedPlace,
                tempPeriod = forecast?.temperature,
                condPeriod = forecast?.condition,
            )
        }
    }
}

fun getSavedPlace(
    now: Instant,
    place: Place,
    selected: Boolean,
    tempPeriod: TemperaturePeriod?,
    condPeriod: ConditionPeriod?
): SavedPlace {
    val location = place.location
    val dateTimeAtPlace = now.atZone(place.location.timeZone)
    val dateAtPlace = dateTimeAtPlace.toLocalDate()
    val tempDayAtPlace = tempPeriod?.dayPeriodOn(dateAtPlace)
    val condDayAtPlace = condPeriod?.dayPeriodOn(dateAtPlace)
    val conditions = if (tempDayAtPlace != null && condDayAtPlace != null) getConditions(
        dateTimeAtPlace,
        tempDayAtPlace,
        condDayAtPlace
    ) else null
    return SavedPlace(
        place = place,
        time = now.atZone(location.timeZone).toLocalTime(),
        selected = selected,
        conditions = conditions
    )
}

private fun getConditions(
    now: ZonedDateTime,
    tempDay: TemperaturePeriod,
    conditionDay: ConditionPeriod
): SavedPlace.Conditions {
    val nowInstant = now.toInstant()
    return SavedPlace.Conditions(
        temp = tempDay[nowInstant]!!.temperature,
        minTemp = tempDay.minimum,
        maxTemp = tempDay.maximum,
        condition = conditionDay[nowInstant]?.condition ?: conditionDay.day ?: conditionDay.night!!
    )
}