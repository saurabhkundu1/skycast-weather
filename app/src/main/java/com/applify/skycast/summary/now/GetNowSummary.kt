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

package com.applify.skycast.summary.now

import com.applify.skycast.forecast.parameters.condition.Condition
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.parameters.temperature.Temperature
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import java.time.ZonedDateTime

fun getNowSummary(
    now: ZonedDateTime,
    tempPeriod: TemperaturePeriod,
    feelsPeriod: TemperaturePeriod,
    condPeriod: ConditionPeriod
): NowSummary? {
    val nowInstant = now.toInstant()
    val tempToday = tempPeriod.dayPeriodOn(now.toLocalDate()) ?: return null
    return NowSummary(
        temp = tempPeriod[nowInstant]?.temperature ?: return null,
        feelsLike = feelsPeriod[nowInstant]?.temperature ?: return null,
        minTemp = tempToday.minimum,
        maxTemp = tempToday.maximum,
        cond = condPeriod[nowInstant]?.condition ?: return null
    )
}

data class NowSummary(
    val temp: Temperature,
    val feelsLike: Temperature,
    val minTemp: Temperature,
    val maxTemp: Temperature,
    val cond: Condition
)