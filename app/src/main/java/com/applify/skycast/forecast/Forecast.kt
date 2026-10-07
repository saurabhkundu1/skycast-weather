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

import com.applify.skycast.forecast.parameters.gust.GustPeriod
import com.applify.skycast.forecast.parameters.humidity.HumidityPeriod
import com.applify.skycast.forecast.parameters.pop.PopPeriod
import com.applify.skycast.forecast.parameters.precipitation.PrecipitationPeriod
import com.applify.skycast.forecast.parameters.pressure.PressurePeriod
import com.applify.skycast.forecast.parameters.sun.SunPeriod
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import com.applify.skycast.forecast.parameters.uvindex.UvIndexPeriod
import com.applify.skycast.forecast.parameters.visibility.VisibilityPeriod
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.units.Units
import com.applify.skycast.forecast.parameters.wind.WindPeriod
import java.time.Instant

data class Forecast(
    val timestamp: Instant,
    val temperature: TemperaturePeriod,
    val feelsLike: TemperaturePeriod,
    val dewPoint: TemperaturePeriod,
    val sun: SunPeriod?,
    val pop: PopPeriod,
    val precipitation: PrecipitationPeriod,
    val uvIndex: UvIndexPeriod,
    val wind: WindPeriod,
    val gust: GustPeriod,
    val pressure: PressurePeriod,
    val visibility: VisibilityPeriod,
    val humidity: HumidityPeriod,
    val condition: ConditionPeriod
) {
    init {
        requireMatching(
            temperature,
            feelsLike,
            dewPoint,
            pop,
            precipitation,
            uvIndex,
            wind,
            gust,
            pressure,
            visibility,
            humidity,
            condition
        )
    }

    fun convertTo(units: Units): Forecast =
        copy(
            temperature = temperature.convertTo(units.temperature),
            feelsLike = feelsLike.convertTo(units.temperature),
            dewPoint = dewPoint.convertTo(units.temperature),
            precipitation = precipitation.convertTo(units.precipitation),
            wind = wind.convertTo(units.windSpeed),
            gust = gust.convertTo(units.windSpeed),
            pressure = pressure.convertTo(units.pressure),
            visibility = visibility.convertTo(units.visibility)
        )
}