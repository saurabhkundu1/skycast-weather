package com.applify.skycast.forecast.download

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
import com.applify.skycast.places.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.net.ssl.HttpsURLConnection

class WeatherApiDownloader(private val userAgent: String) {
    suspend fun get(location: Location, apiKey: String): Forecast? =
        download(location, apiKey)?.let { json -> runCatching { convert(json, location) }.getOrNull() }

    private suspend fun download(location: Location, apiKey: String): JSONObject? = withContext(Dispatchers.IO) {
        val query = URLEncoder.encode(
            "${"%.4f".format(Locale.ROOT, location.coordinates.latitude)},${"%.4f".format(Locale.ROOT, location.coordinates.longitude)}",
            Charsets.UTF_8.name()
        )
        val key = URLEncoder.encode(apiKey, Charsets.UTF_8.name())
        val url = URL("https://api.weatherapi.com/v1/forecast.json?key=$key&q=$query&days=3&aqi=no&alerts=no")
        val connection = try {
            url.openConnection() as HttpsURLConnection
        } catch (_: Exception) {
            return@withContext null
        }
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.setRequestProperty("User-Agent", userAgent)
            if (connection.responseCode != 200) return@withContext null
            BufferedReader(InputStreamReader(connection.inputStream)).use {
                JSONObject(it.readText())
            }
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun convert(json: JSONObject, location: Location): Forecast {
        val forecastDays = json.getJSONObject("forecast").getJSONArray("forecastday")
        val times = mutableListOf<ZonedDateTime>()
        val temperatures = mutableListOf<TemperatureMoment>()
        val feels = mutableListOf<TemperatureMoment>()
        val dewPoints = mutableListOf<TemperatureMoment>()
        val pops = mutableListOf<PopMoment>()
        val precipitations = mutableListOf<PrecipitationMoment>()
        val uvs = mutableListOf<UvIndexMoment>()
        val winds = mutableListOf<WindMoment>()
        val gusts = mutableListOf<GustMoment>()
        val pressures = mutableListOf<PressureMoment>()
        val visibilities = mutableListOf<VisibilityMoment>()
        val humidities = mutableListOf<HumidityMoment>()
        val conditions = mutableListOf<ConditionMoment>()
        val suns = mutableListOf<SunMoment>()
        val zone = location.timeZone

        for (dayIndex in 0 until forecastDays.length()) {
            val day = forecastDays.getJSONObject(dayIndex)
            val date = LocalDate.parse(day.getString("date"))
            val astro = day.getJSONObject("astro")
            parseAstro(date, astro.optString("sunrise"), zone)?.let { suns += SunMoment(it, SunEvent.Rise) }
            parseAstro(date, astro.optString("sunset"), zone)?.let { suns += SunMoment(it, SunEvent.Set) }
            val hours = day.getJSONArray("hour")
            for (hourIndex in 0 until hours.length()) {
                val hour = hours.getJSONObject(hourIndex)
                val time = Instant.ofEpochSecond(hour.getLong("time_epoch")).atZone(zone)
                times += time
                temperatures += TemperatureMoment(time, Temperature(hour.getDouble("temp_c"), Temperature.Unit.DegreesCelsius))
                feels += TemperatureMoment(time, Temperature(hour.getDouble("feelslike_c"), Temperature.Unit.DegreesCelsius))
                dewPoints += TemperatureMoment(time, Temperature(hour.getDouble("dewpoint_c"), Temperature.Unit.DegreesCelsius))
                pops += PopMoment(time, Pop(maxOf(hour.optDouble("chance_of_rain", 0.0), hour.optDouble("chance_of_snow", 0.0))))
                val rain = hour.optDouble("rain_mm", 0.0)
                val snow = hour.optDouble("snow_cm", 0.0)
                precipitations += PrecipitationMoment(
                    time,
                    MixedPrecipitation(
                        Rain(rain, Precipitation.Unit.Millimeters),
                        Showers.ZeroMillimeters,
                        Snow(snow, Precipitation.Unit.Centimeters),
                        Precipitation.Unit.Millimeters
                    )
                )
                uvs += UvIndexMoment(time, UvIndex(hour.optDouble("uv", 0.0)))
                winds += WindMoment(
                    time,
                    Wind(
                        WindSpeed(hour.getDouble("wind_kph") / 3.6, WindSpeed.Unit.MetersPerSecond),
                        WindDirection(hour.optDouble("wind_degree", 0.0))
                    )
                )
                gusts += GustMoment(time, WindSpeed(hour.getDouble("gust_kph") / 3.6, WindSpeed.Unit.MetersPerSecond))
                pressures += PressureMoment(time, Pressure(hour.getDouble("pressure_mb"), Pressure.Unit.Hectopascal))
                visibilities += VisibilityMoment(time, Visibility(hour.getDouble("vis_km") * 1000.0, Visibility.Unit.Meters))
                humidities += HumidityMoment(time, Humidity(hour.getDouble("humidity")))
                conditions += ConditionMoment(
                    time,
                    Condition(weatherApiCodeToWmo(hour.getJSONObject("condition").getInt("code")), hour.optInt("is_day", 1) == 1)
                )
            }
        }
        require(times.isNotEmpty()) { "WeatherAPI returned no hourly forecast data" }
        require(times.zipWithNext().all { (a, b) -> a < b }) { "WeatherAPI returned unordered hourly forecast data" }

        return Forecast(
            timestamp = Instant.now(),
            temperature = TemperaturePeriod(temperatures),
            feelsLike = TemperaturePeriod(feels),
            dewPoint = TemperaturePeriod(dewPoints),
            sun = suns.sortedBy { it.timeInstant }.distinctBy { it.timeInstant }.takeIf { it.isNotEmpty() }?.let(::SunPeriod),
            pop = PopPeriod(pops),
            precipitation = PrecipitationPeriod(precipitations),
            uvIndex = UvIndexPeriod(uvs),
            wind = WindPeriod(winds),
            gust = GustPeriod(gusts),
            pressure = PressurePeriod(pressures),
            visibility = VisibilityPeriod(visibilities),
            humidity = HumidityPeriod(humidities),
            condition = ConditionPeriod(conditions)
        )
    }

    private fun parseAstro(date: LocalDate, value: String, zone: java.time.ZoneId): ZonedDateTime? =
        runCatching {
            val localTime = LocalTime.parse(value, DateTimeFormatter.ofPattern("h:mm a", Locale.US))
            date.atTime(localTime).atZone(zone)
        }.getOrNull()

    private fun weatherApiCodeToWmo(code: Int): Int = when (code) {
        1000 -> 0
        1003 -> 2
        1006, 1009 -> 3
        1030, 1135, 1147 -> 45
        1069, 1204 -> 66
        1072, 1150, 1153, 1168 -> 51
        1171, 1201, 1207 -> 67
        1114, 1117, 1222, 1225 -> 75
        1210, 1213 -> 71
        1216, 1219 -> 73
        1087, 1273, 1279 -> 95
        1276, 1282 -> 96
        1063, 1180, 1183, 1240 -> 61
        1186, 1189 -> 63
        1192, 1195, 1246 -> 65
        1198 -> 66
        1066, 1237, 1261, 1264 -> 77
        1255 -> 85
        1258 -> 86
        1243 -> 81
        1246 -> 82
        1249, 1252 -> 80
        else -> 3
    }
}
