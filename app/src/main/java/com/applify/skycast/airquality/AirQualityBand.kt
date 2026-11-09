package com.applify.skycast.airquality

import androidx.annotation.StringRes
import com.applify.skycast.R

@StringRes
fun airQualityBand(europeanAqi: Int): Int = when {
    europeanAqi <= 20 -> R.string.air_quality_band_good
    europeanAqi <= 40 -> R.string.air_quality_band_fair
    europeanAqi <= 60 -> R.string.air_quality_band_moderate
    europeanAqi <= 80 -> R.string.air_quality_band_poor
    europeanAqi <= 100 -> R.string.air_quality_band_very_poor
    else -> R.string.air_quality_band_extremely_poor
}
