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

package com.applify.skycast.graphs.precipitation.compose

import com.applify.skycast.forecast.parameters.condition.Condition
import com.applify.skycast.forecast.parameters.condition.ConditionMoment
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.parameters.precipitation.MixedPrecipitation
import com.applify.skycast.forecast.parameters.precipitation.Precipitation
import com.applify.skycast.forecast.parameters.precipitation.PrecipitationMoment
import com.applify.skycast.forecast.parameters.precipitation.PrecipitationPeriod
import com.applify.skycast.forecast.parameters.precipitation.Rain
import com.applify.skycast.forecast.parameters.precipitation.Showers
import com.applify.skycast.forecast.parameters.precipitation.Snow
import com.applify.skycast.graphs.common.previews.GraphPreviewDstSwitch
import com.applify.skycast.graphs.common.previews.GraphPreviewNowPosition
import com.applify.skycast.graphs.common.previews.GraphPreviewValues
import com.applify.skycast.graphs.common.previews.GraphsPreviewParameterProvider
import com.applify.skycast.graphs.precipitation.PrecipitationGraphs
import com.applify.skycast.graphs.precipitation.getPrecipitationGraphs
import java.time.ZonedDateTime
import kotlin.random.Random

class PrecipitationGraphsPreviewParameterProvider : GraphsPreviewParameterProvider<PrecipitationGraphs>(
    graphPreviewValues = listOf(
        GraphPreviewValues.Flat(value = 2.5),
        // Everyday cases
        GraphPreviewValues.Random(from = 0.0, until = 5.0),
        // Everyday cases with a couple steps after heavy
        GraphPreviewValues.Random(from = 0.0, until = 8.0),
        // Unusually heavy cases
        GraphPreviewValues.Random(from = 0.0, until = 16.0)
    )
) {
    override fun generateGraphs(
        times: List<ZonedDateTime>,
        nowPosition: GraphPreviewNowPosition,
        dstSwitch: GraphPreviewDstSwitch,
        values: GraphPreviewValues
    ): PrecipitationGraphs {
        val precipitation = PrecipitationPeriod(
            moments = times.map {
                val amount = when (values) {
                    is GraphPreviewValues.Flat -> values.value
                    is GraphPreviewValues.Random -> Random.nextDouble(values.from, values.until)
                }
                PrecipitationMoment(
                    timeZdt = it,
                    precipitation = MixedPrecipitation(
                        unit = Precipitation.Unit.Millimeters,
                        rain = Rain(amount, Precipitation.Unit.Millimeters),
                        showers = Showers(amount, Precipitation.Unit.Millimeters),
                        snow = Snow(amount, Precipitation.Unit.Millimeters),
                    )
                )

            }
        )
        val condition = ConditionPeriod(
            times.map {
                ConditionMoment(
                    timeZdt = it,
                    condition = Condition(
                        wmoCode = 0,
                        isDay = it.toLocalTime().hour >= 7
                    )
                )
            }
        )
        return getPrecipitationGraphs(
            now = getNow(times, nowPosition),
            precipPeriod = precipitation,
            condPeriod = condition
        )!!
    }
}