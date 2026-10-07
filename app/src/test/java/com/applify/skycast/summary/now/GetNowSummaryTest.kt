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
import com.applify.skycast.forecast.parameters.condition.ConditionMoment
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.parameters.temperature.Temperature
import com.applify.skycast.forecast.parameters.temperature.TemperatureMoment
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import com.applify.skycast.unixEpochStartZdt
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.temporal.ChronoUnit

class GetNowSummaryTest {
    @Test
    fun `summarizes current temperature, feels like and description and returns min and max temp of today`() =
        runTest {
            val firstDayFirstMoment = unixEpochStartZdt.plus(22, ChronoUnit.HOURS)
            val now = firstDayFirstMoment.plus(10, ChronoUnit.MINUTES)
            val firstDaySecondMoment = firstDayFirstMoment.plus(1, ChronoUnit.HOURS)
            val secondDayFirstMoment = firstDaySecondMoment.plus(1, ChronoUnit.HOURS)
            val temperaturePeriod = TemperaturePeriod(
                moments = listOf(
                    TemperatureMoment(
                        firstDayFirstMoment,
                        Temperature(0.0, Temperature.Unit.DegreesCelsius)
                    ),
                    TemperatureMoment(
                        firstDaySecondMoment,
                        Temperature(1.0, Temperature.Unit.DegreesCelsius)
                    ),
                    TemperatureMoment(
                        secondDayFirstMoment,
                        Temperature(20.0, Temperature.Unit.DegreesCelsius)
                    )
                )
            )
            val feelsLikePeriod = TemperaturePeriod(
                moments = listOf(
                    TemperatureMoment(
                        firstDayFirstMoment,
                        Temperature(-1.0, Temperature.Unit.DegreesCelsius)
                    ),
                    TemperatureMoment(
                        firstDaySecondMoment,
                        Temperature(0.0, Temperature.Unit.DegreesCelsius)
                    ),
                    TemperatureMoment(
                        secondDayFirstMoment,
                        Temperature(20.0, Temperature.Unit.DegreesCelsius)
                    )
                )
            )
            val conditionPeriod = ConditionPeriod(
                moments = listOf(
                    ConditionMoment(firstDayFirstMoment, Condition(wmoCode = 1, isDay = false)),
                    ConditionMoment(firstDaySecondMoment, Condition(wmoCode = 2, isDay = false)),
                    ConditionMoment(secondDayFirstMoment, Condition(wmoCode = 3, isDay = true))
                )
            )
            val summary = getNowSummary(now, temperaturePeriod, feelsLikePeriod, conditionPeriod)
            Assert.assertEquals(
                NowSummary(
                    temp = Temperature(0.0, Temperature.Unit.DegreesCelsius),
                    feelsLike = Temperature(-1.0, Temperature.Unit.DegreesCelsius),
                    minTemp = Temperature(0.0, Temperature.Unit.DegreesCelsius),
                    maxTemp = Temperature(1.0, Temperature.Unit.DegreesCelsius),
                    cond = Condition(1, false)
                ),
                summary
            )
        }

    @Test
    fun `summary is outdated when no data after now`() = runTest {
        val firstMoment = unixEpochStartZdt
        val afterFirstMoment = firstMoment.plus(1, ChronoUnit.HOURS)
        val now = afterFirstMoment.plus(10, ChronoUnit.MINUTES)
        val temperaturePeriod = TemperaturePeriod(
            moments = listOf(
                TemperatureMoment(firstMoment, Temperature(0.0, Temperature.Unit.DegreesCelsius)),
            )
        )
        val feelsLikePeriod = TemperaturePeriod(
            moments = listOf(
                TemperatureMoment(firstMoment, Temperature(-1.0, Temperature.Unit.DegreesCelsius)),
            )
        )
        val conditionPeriod = ConditionPeriod(
            moments = listOf(
                ConditionMoment(firstMoment, Condition(wmoCode = 1, isDay = false)),
            )
        )
        Assert.assertNull(
            getNowSummary(
                now = now,
                tempPeriod = temperaturePeriod,
                feelsPeriod = feelsLikePeriod,
                condPeriod = conditionPeriod
            )
        )
    }
}