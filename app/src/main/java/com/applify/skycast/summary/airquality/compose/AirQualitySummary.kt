/*
 * Copyright 2024 David Takač
 *
 * This file is part of Skycast and is licensed under the GNU General Public License v3.0 or later.
 */

package com.applify.skycast.summary.airquality.compose

import android.text.format.DateFormat as AndroidDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.applify.skycast.R
import com.applify.skycast.airquality.AirQualityReading
import com.applify.skycast.airquality.airQualityBand
import com.applify.skycast.summary.common.compose.SummaryTile
import java.text.NumberFormat
import java.util.Date

@Composable
fun AirQualitySummary(reading: AirQualityReading, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pm25 = remember(reading.pm25) {
        NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }.format(reading.pm25)
    }
    val fetchedAt = remember(reading.fetchedAt, context) {
        val date = Date(reading.fetchedAt)
        "${AndroidDateFormat.getDateFormat(context).format(date)} ${AndroidDateFormat.getTimeFormat(context).format(date)}"
    }
    SummaryTile(
        label = { Text(stringResource(R.string.air_quality_title)) },
        value = { Text(stringResource(R.string.air_quality_european_aqi, reading.europeanAqi)) },
        supportingValue = { Text(stringResource(R.string.air_quality_pm25, pm25)) },
        bottom = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(stringResource(airQualityBand(reading.europeanAqi)))
                Text(stringResource(R.string.air_quality_source))
                if (reading.isStale) Text(stringResource(R.string.air_quality_cached, fetchedAt))
            }
        },
        modifier = modifier.fillMaxWidth(),
    )
}
