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

package com.applify.skycast.graphs.temperature.compose

import com.applify.skycast.forecast.parameters.condition.Condition
import com.applify.skycast.forecast.parameters.condition.ConditionMoment
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.parameters.temperature.Temperature
import com.applify.skycast.forecast.parameters.temperature.TemperatureMoment
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import com.applify.skycast.graphs.common.previews.GraphPreviewDstSwitch
import com.applify.skycast.graphs.common.previews.GraphPreviewNowPosition
import com.applify.skycast.graphs.common.previews.GraphPreviewValues
import com.applify.skycast.graphs.common.previews.GraphsPreviewParameterProvider
import com.applify.skycast.graphs.temperature.TemperatureGraphs
import com.applify.skycast.graphs.temperature.getTemperatureGraphs
import java.time.ZonedDateTime
import kotlin.random.Random

class TemperatureGraphsPreviewParameterProvider : GraphsPreviewParameterProvider<TemperatureGraphs>(
    graphPreviewValues = listOf(
        GraphPreviewValues.Flat(value = 25.0),
        GraphPreviewValues.Random(from = 12.0, until = 27.0)
    )
) {
    override fun generateGraphs(
        times: List<ZonedDateTime>,
        nowPosition: GraphPreviewNowPosition,
        dstSwitch: GraphPreviewDstSwitch,
        values: GraphPreviewValues,
    ): TemperatureGraphs {
        val tempPeriod = TemperaturePeriod(
            times.map {
                TemperatureMoment(
                    timeZdt = it,
                    temperature = Temperature(
                        value = when (values) {
                            is GraphPreviewValues.Flat -> values.value
                            is GraphPreviewValues.Random -> Random.nextDouble(values.from, values.until)
                        },
                        unit = Temperature.Unit.DegreesCelsius
                    )
                )
            }
        )
        val condPeriod = ConditionPeriod(
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
        return getTemperatureGraphs(
            now = getNow(times, nowPosition),
            tempPeriod = tempPeriod,
            condPeriod = condPeriod
        )!!
    }
}