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

package com.applify.skycast.summary.humidity

import com.applify.skycast.forecast.parameters.humidity.Humidity
import com.applify.skycast.forecast.parameters.humidity.HumidityPeriod
import com.applify.skycast.forecast.parameters.temperature.Temperature
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import java.time.ZonedDateTime

fun getHumiditySummary(
    now: ZonedDateTime,
    humidityPeriod: HumidityPeriod,
    dewPointPeriod: TemperaturePeriod
): HumiditySummary? {
    val nowInstant = now.toInstant()
    return HumiditySummary(
        humidityNow = humidityPeriod[nowInstant]?.humidity ?: return null,
        dewPointNow = dewPointPeriod[nowInstant]?.temperature ?: return null
    )
}

data class HumiditySummary(
    val humidityNow: Humidity,
    val dewPointNow: Temperature
)