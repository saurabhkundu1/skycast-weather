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

package com.applify.skycast.forecast.cache

import com.applify.skycast.forecast.Forecast
import com.applify.skycast.forecast.parameters.condition.Condition
import com.applify.skycast.forecast.parameters.condition.ConditionMoment
import com.applify.skycast.forecast.parameters.condition.ConditionPeriod
import com.applify.skycast.forecast.parameters.gust.GustMoment
import com.applify.skycast.forecast.parameters.gust.GustPeriod
import com.applify.skycast.forecast.parameters.humidity.Humidity
import com.applify.skycast.forecast.parameters.humidity.HumidityMoment
import com.applify.skycast.forecast.parameters.humidity.HumidityPeriod
import com.applify.skycast.forecast.parameters.pop.Pop
import com.applify.skycast.forecast.parameters.pop.PopMoment
import com.applify.skycast.forecast.parameters.pop.PopPeriod
import com.applify.skycast.forecast.parameters.precipitation.MixedPrecipitation
import com.applify.skycast.forecast.parameters.precipitation.Precipitation
import com.applify.skycast.forecast.parameters.precipitation.PrecipitationMoment
import com.applify.skycast.forecast.parameters.precipitation.PrecipitationPeriod
import com.applify.skycast.forecast.parameters.precipitation.Rain
import com.applify.skycast.forecast.parameters.precipitation.Showers
import com.applify.skycast.forecast.parameters.precipitation.Snow
import com.applify.skycast.forecast.parameters.pressure.Pressure
import com.applify.skycast.forecast.parameters.pressure.PressureMoment
import com.applify.skycast.forecast.parameters.pressure.PressurePeriod
import com.applify.skycast.forecast.parameters.sun.SunEvent
import com.applify.skycast.forecast.parameters.sun.SunMoment
import com.applify.skycast.forecast.parameters.sun.SunPeriod
import com.applify.skycast.forecast.parameters.temperature.Temperature
import com.applify.skycast.forecast.parameters.temperature.TemperatureMoment
import com.applify.skycast.forecast.parameters.temperature.TemperaturePeriod
import com.applify.skycast.forecast.parameters.uvindex.UvIndex
import com.applify.skycast.forecast.parameters.uvindex.UvIndexMoment
import com.applify.skycast.forecast.parameters.uvindex.UvIndexPeriod
import com.applify.skycast.forecast.parameters.visibility.Visibility
import com.applify.skycast.forecast.parameters.visibility.VisibilityMoment
import com.applify.skycast.forecast.parameters.visibility.VisibilityPeriod
import com.applify.skycast.forecast.parameters.wind.Wind
import com.applify.skycast.forecast.parameters.wind.WindDirection
import com.applify.skycast.forecast.parameters.wind.WindMoment
import com.applify.skycast.forecast.parameters.wind.WindPeriod
import com.applify.skycast.forecast.parameters.wind.WindSpeed
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ConvertCacheJsonToForecastTest {
    @Test
    fun `convert json to forecast`() = runTest {
        val json = JSONObject(getJson())
        assertEquals(getForecast(), convertCacheJsonToForecast(json))
    }

    @Test
    fun `convert forecast to json`() = runTest {
        val forecast = getForecast()
        assertEquals(JSONObject(getJson()).toString(), convertForecastToCacheJson(forecast).toString())
    }
}

private val timestamp = Instant.ofEpochSecond(1779224400)
private val hour = ZonedDateTime.ofInstant(timestamp, ZoneId.systemDefault())

private fun getJson(): String = """
    {
        "timestamp": ${timestamp.epochSecond},
        "temperature_period": [
            {
                "hour": "$hour",
                "temperature_value": 1.0,
                "temperature_unit": "DegreesCelsius"
            }
        ],
        "feels_like_period": [
            {
                "hour": "$hour",
                "temperature_value": 1.0,
                "temperature_unit": "DegreesCelsius"
            }
        ],
        "dew_point_period": [
            {
                "hour": "$hour",
                "temperature_value": 1.0,
                "temperature_unit": "DegreesCelsius"
            }
        ],
        "sun_period": [
            {
                "time": "$hour",
                "sun_event": "Rise"
            }
        ],
        "pop_period": [
            {
                "hour": "$hour",
                "pop_percent": 1.0
            }
        ],
        "precipitation_period": [
            {
                "hour": "$hour",
                "rain_value": 1.0,
                "rain_unit": "Millimeters",
                "showers_value": 1.0,
                "showers_unit": "Millimeters",
                "snow_value": 1.0,
                "snow_unit": "Centimeters",
                "precipitation_unit": "Millimeters"
            }
        ],
        "uv_index_period": [
            {
                "hour": "$hour",
                "uv_index": 1
            }
        ],
        "wind_period": [
            {
                "hour": "$hour",
                "wind_speed_value": 1.0,
                "wind_speed_unit": "MetersPerSecond",
                "direction_from_degrees": 1.0
            }
        ],
        "gust_period": [
            {
                "hour": "$hour",
                "wind_speed_value": 1.0,
                "wind_speed_unit": "MetersPerSecond"
            }
        ],
        "pressure_period": [
            {
                "hour": "$hour",
                "pressure_value": 1.0,
                "pressure_unit": "Hectopascal"
            }
        ],
        "visibility_period": [
            {
                "hour": "$hour",
                "visibility_value": 1.0,
                "visibility_unit": "Kilometers"
            }
        ],
        "humidity_period": [
            {
                "hour": "$hour",
                "humidity_percent": 1.0
            }
        ],
        "condition_period": [
            {
                "hour": "$hour",
                "condition_is_day": false,
                "condition_wmo_code": 1
            }
        ]
    }
""".trimIndent()

private fun getForecast() = Forecast(
    timestamp = timestamp,
    temperature = TemperaturePeriod(
        moments = listOf(
            TemperatureMoment(
                hour,
                Temperature(1.0, Temperature.Unit.DegreesCelsius)
            )
        )
    ),
    feelsLike = TemperaturePeriod(
        listOf(
            TemperatureMoment(
                hour,
                Temperature(1.0, Temperature.Unit.DegreesCelsius)
            )
        )
    ),
    dewPoint = TemperaturePeriod(
        listOf(
            TemperatureMoment(
                hour,
                Temperature(1.0, Temperature.Unit.DegreesCelsius)
            )
        )
    ),
    sun = SunPeriod(listOf(SunMoment(hour, SunEvent.Rise))),
    pop = PopPeriod(listOf(PopMoment(hour, Pop(1.0)))),
    precipitation = PrecipitationPeriod(
        listOf(
            PrecipitationMoment(
                hour,
                precipitation = MixedPrecipitation(
                    rain = Rain(1.0, Precipitation.Unit.Millimeters),
                    showers = Showers(1.0, Precipitation.Unit.Millimeters),
                    snow = Snow(1.0, Precipitation.Unit.Centimeters),
                    unit = Precipitation.Unit.Millimeters
                )
            )
        )
    ),
    uvIndex = UvIndexPeriod(listOf(UvIndexMoment(hour, UvIndex(1.0)))),
    wind = WindPeriod(
        listOf(
            WindMoment(
                hour,
                Wind(
                    WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond),
                    WindDirection(1.0)
                )
            )
        )
    ),
    gust = GustPeriod(listOf(GustMoment(hour, WindSpeed(1.0, WindSpeed.Unit.MetersPerSecond)))),
    pressure = PressurePeriod(
        listOf(
            PressureMoment(
                hour,
                Pressure(1.0, Pressure.Unit.Hectopascal)
            )
        )
    ),
    visibility = VisibilityPeriod(
        listOf(
            VisibilityMoment(
                hour,
                Visibility(1.0, Visibility.Unit.Kilometers)
            )
        )
    ),
    humidity = HumidityPeriod(listOf(HumidityMoment(hour, Humidity(1.0)))),
    condition = ConditionPeriod(listOf(ConditionMoment(hour, Condition(1, false))))
)