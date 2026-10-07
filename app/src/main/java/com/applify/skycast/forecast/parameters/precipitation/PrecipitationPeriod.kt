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

package com.applify.skycast.forecast.parameters.precipitation

import com.applify.skycast.forecast.HourPeriod
import java.time.Instant
import java.time.LocalDate

class PrecipitationPeriod(moments: List<PrecipitationMoment>) : HourPeriod<PrecipitationMoment>(moments) {
    val total: MixedPrecipitation get() = map { it.precipitation }.reduce { acc, precipitation -> acc + precipitation }
    val max: MixedPrecipitation get() = maxOf { it.precipitation }

    fun periodUntil(hourExclusive: Instant, takeLast: Int? = null) =
        momentsUntil(hourExclusive, takeLast)?.let { PrecipitationPeriod(it) }

    fun periodFrom(hourInclusive: Instant, take: Int? = null) =
        momentsFrom(hourInclusive, take)?.let { PrecipitationPeriod(it) }

    fun dayPeriodsFrom(dayInclusive: LocalDate, take: Int? = null) =
        dayMomentsFrom(dayInclusive, take)?.map { PrecipitationPeriod(it) }

    fun convertTo(unit: Precipitation.Unit): PrecipitationPeriod {
        if (first().precipitation.unit == unit) return this
        val convertedMoments = map { PrecipitationMoment(it.timeZdt, it.precipitation.convertTo(unit)) }
        return PrecipitationPeriod(convertedMoments)
    }
}