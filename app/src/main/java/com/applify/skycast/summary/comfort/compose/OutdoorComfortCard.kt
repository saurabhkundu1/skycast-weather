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

package com.applify.skycast.summary.comfort.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.applify.skycast.R
import com.applify.skycast.airquality.AirQualityReading
import com.applify.skycast.forecast.parameters.temperature.string
import com.applify.skycast.forecast.parameters.wind.string
import com.applify.skycast.summary.SummaryState
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OutdoorComfortCard(
    state: SummaryState.Success,
    airQuality: AirQualityReading?,
    modifier: Modifier = Modifier
) {
    val uvValue = state.uvIndex.now.value
    val humidityVal = state.humidity.humidityNow.value.roundToInt()
    val feelsTemp = state.feelsLike.feelsLikeNow.string()
    val windSpeed = state.wind.windNow.speed.string()

    val isHighUv = uvValue >= 6
    val isHumid = humidityVal >= 75
    val isHighAqi = airQuality != null && airQuality.europeanAqi > 50

    val (comfortHeadline, headlineColor) = when {
        isHighAqi -> "Air Quality Notice · Take Precautions" to Color(0xFFF59E0B)
        isHighUv -> "High UV Index · Sun Protection Recommended" to Color(0xFFEAB308)
        isHumid -> "Warm & Humid Atmosphere" to Color(0xFF06B6D4)
        else -> "Pleasant & Clear Outdoor Weather" to Color(0xFF10B981)
    }

    val runningStatus = when {
        isHighAqi || isHighUv || isHumid -> stringResource(R.string.outdoor_comfort_caution)
        else -> stringResource(R.string.outdoor_comfort_optimal)
    }
    val cyclingStatus = when {
        isHighAqi -> stringResource(R.string.outdoor_comfort_caution)
        isHighUv -> stringResource(R.string.outdoor_comfort_good)
        else -> stringResource(R.string.outdoor_comfort_optimal)
    }
    val walkingStatus = when {
        isHighAqi -> stringResource(R.string.outdoor_comfort_good)
        else -> stringResource(R.string.outdoor_comfort_optimal)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.outdoor_comfort_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = headlineColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = comfortHeadline,
                    color = headlineColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityBadge(
                    activity = stringResource(R.string.outdoor_comfort_running),
                    status = runningStatus,
                    modifier = Modifier.weight(1f)
                )
                ActivityBadge(
                    activity = stringResource(R.string.outdoor_comfort_cycling),
                    status = cyclingStatus,
                    modifier = Modifier.weight(1f)
                )
                ActivityBadge(
                    activity = stringResource(R.string.outdoor_comfort_walking),
                    status = walkingStatus,
                    modifier = Modifier.weight(1f)
                )
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Feels $feelsTemp · Wind $windSpeed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "UV $uvValue · Humidity $humidityVal%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActivityBadge(activity: String, status: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = activity,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
